package com.yp.core.fxview.admin.config;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yp.admin.data.Export;
import com.yp.admin.model.ExportModel;
import com.yp.core.AModel;
import com.yp.core.BaseConstants;
import com.yp.core.db.OnExportListener.PHASE;
import com.yp.core.entity.IDataEntity;
import com.yp.core.entity.IResult;
import com.yp.core.fxview.AForm;
import com.yp.core.fxview.admin.RootPage;
import com.yp.core.fxview.gui.FormatedCell;
import com.yp.core.log.MyLogger;
import com.yp.core.ref.IReference;
import com.yp.core.tools.ResourceWalker;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;

public class Config extends RootPage {
	@FXML
	private ComboBox<MyConnInfo> cbSourceDb, cbTargetDb;
	@FXML
	private ComboBox<IReference<String>> cbDbResources;
	@FXML
	private CheckBox onyDefaultDb;
	@FXML
	private TextField txtAyrac, txtUrl, txtSurucu, txtKln, txtParolaA, txtKynSema, txtHdfSema, txtSuz;
	@FXML
	private PasswordField txtParola;
	@FXML
	private CheckBox onyGosterParola;
	@FXML
	private Label etkKynBaglanti;
	@FXML
	private Button btnDbCreate;
	@FXML
	private TableView<MyExports> tListe;
	@FXML
	private TableColumn<IDataEntity, String> stnGrup;
	@FXML
	private TableColumn<IDataEntity, Integer> stnSirnu;
	@FXML
	private TableColumn<IDataEntity, Double> stnProgres;
	@FXML
	private TableColumn<IDataEntity, String> stnQuery;
	@FXML
	private TableColumn<IDataEntity, Boolean> stnDeleteRows;
	@FXML
	private TextArea txtConsole;
	@FXML
	private FormatedCell<IDataEntity, Integer> fKyntoplam, fHdftoplam;

	private CheckBox select_all;

	private List<MyConnInfo> connConfList;
	private List<MyExports> transferList = new ArrayList<>();
	private FilteredList<MyExports> filteredTransferList;
	private MyConnInfo sourceDb, targetDb;
	private DbExport dbExport;

	private void setDefultDb(MyConnInfo pDefaultDb) {
		sourceDb = pDefaultDb;
		for (MyConnInfo vs : connConfList) {
			vs.setDefaultDb(pDefaultDb.equals(vs));
			System.out.println(vs.getKey() + ":" + vs.isDefaultDb());
		}
	}

	private StringProperty targetSchema;

	public StringProperty targetSchemaProperty() {
		if (targetSchema == null)
			targetSchema = new SimpleStringProperty();
		return targetSchema;
	}

	@Override
	public void initialize(URL pLocation, ResourceBundle pResources) {
		connConfList = MyConnInfo.loadList(getExportModel().getDbList());

		cbSourceDb.setItems(FXCollections.observableArrayList(connConfList));
		cbTargetDb.setItems(FXCollections.observableArrayList(connConfList));
		sourceDb = new MyConnInfo(getExportModel().getDefaultDb());
		targetDb = new MyConnInfo(getExportModel().getRemoteDb());

		cbSourceDb.getSelectionModel().select(sourceDb);
		cbTargetDb.getSelectionModel().select(targetDb);
		etkKynBaglanti.setText(sourceDb.toString());

		List<IReference<String>> dbResources = getExportModel().getDbResourceList();
		if (dbResources != null)
			cbDbResources.setItems(FXCollections.observableArrayList(dbResources));

		txtHdfSema.textProperty().bindBidirectional(targetSchemaProperty());
		targetSchemaProperty()
				.addListener((pObservable, pOldValue, pNewValue) -> txtKynSema.setText(txtHdfSema.getText()));

		buildTransfersTable();

		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem cut = new MenuItem(BaseConstants.getString("Cut"));
		cut.setOnAction(event -> txtConsole.cut());

		final MenuItem copy = new MenuItem(BaseConstants.getString("Copy"));
		copy.setOnAction(event -> {
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(txtConsole.getSelectedText());
			clipboard.setContent(content);
		});

		final MenuItem clear = new MenuItem(BaseConstants.getString("Clear"));
		clear.setOnAction(event -> txtConsole.clear());

		final MenuItem selectAll = new MenuItem(BaseConstants.getString("Select.All"));
		selectAll.setOnAction(event -> txtConsole.selectAll());
		contextMenu.getItems().addAll(selectAll, new SeparatorMenuItem(), copy, new SeparatorMenuItem(), cut,
				new SeparatorMenuItem(), clear);
		txtConsole.setContextMenu(contextMenu);

		txtSuz.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredTransferList.setPredicate(de -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				// Compare first name and last name of every person with filter
				// text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (de.getGroupCode().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches first name.
				}
				return false; // Does not match.
			});
		});

		fKyntoplam.setFormat(BaseConstants.FORMAT_NUMBER_WITHOUT_DECIMAL_SEP);
		fHdftoplam.setFormat(BaseConstants.FORMAT_NUMBER_WITHOUT_DECIMAL_SEP);

		checkDisplay();

	}

	private void checkDisplay() {
		boolean disable = getExportModel().isRemotingEnabled();
		btnDbCreate.setDisable(disable);
	}

	private void buildTransfersTable() {
		stnGrup.setCellFactory(TextFieldTableCell.<IDataEntity>forTableColumn());
		stnSirnu.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

		stnQuery.setCellFactory(TextFieldTableCell.<IDataEntity>forTableColumn());
		stnProgres.setCellFactory(ProgressBarTableCell.forTableColumn());
		stnDeleteRows.setCellFactory(CheckBoxTableCell.forTableColumn(stnDeleteRows));
		select_all = new CheckBox();
		select_all.setOnAction(this::selectAllBoxes);
		stnDeleteRows.setGraphic(select_all);

		tListe.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem transferMenuItem = new MenuItem(BaseConstants.getString("Transfer"));
		transferMenuItem.setOnAction(this::onExport);
		final MenuItem deleteMenuItem = new MenuItem(BaseConstants.getString("SilSecililer"));
		deleteMenuItem.setOnAction(this::delete);
		contextMenu.getItems().addAll(transferMenuItem, new SeparatorMenuItem(), deleteMenuItem);
		tListe.contextMenuProperty().set(contextMenu);
	}

	public void selectAllBoxes(ActionEvent e) {
		for (MyExports ex : transferList) {
			ex.deleteTargetTableRowsProperty().set(select_all.isSelected());
		}
	}

	@Override
	public String getHelpFileName() {
		return "";
	}

	@Override
	public void synchronize(boolean pToForm, Object[] pAdditionalParams) {
		if (pToForm) {
			txtUrl.textProperty().bindBidirectional(sourceDb.dbUrlProperty());
			txtAyrac.textProperty().bindBidirectional(sourceDb.dbSeperatorProperty());
			txtKln.textProperty().bindBidirectional(sourceDb.dbUserProperty());
			txtParolaA.textProperty().bindBidirectional(sourceDb.dbPasswordProperty());
			txtParola.textProperty().bindBidirectional(sourceDb.dbPasswordProperty());
			txtSurucu.textProperty().bindBidirectional(sourceDb.dbDriverProperty());
			onyDefaultDb.selectedProperty().bindBidirectional(sourceDb.defaultDbProperty());
		} else {
			txtUrl.textProperty().unbindBidirectional(sourceDb.dbUrlProperty());
			txtAyrac.textProperty().unbindBidirectional(sourceDb.dbSeperatorProperty());
			txtKln.textProperty().unbindBidirectional(sourceDb.dbUserProperty());
			txtParolaA.textProperty().unbindBidirectional(sourceDb.dbPasswordProperty());
			txtParola.textProperty().unbindBidirectional(sourceDb.dbPasswordProperty());
			txtSurucu.textProperty().unbindBidirectional(sourceDb.dbDriverProperty());
			onyDefaultDb.selectedProperty().unbindBidirectional(sourceDb.defaultDbProperty());
		}

	}

	@FXML
	public void onDbResourcesChanged(ActionEvent event) {
		if ("-1".equals(cbDbResources.getValue().getKey())) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(BaseConstants.getString("FrmConfig.FindDbResource.Header"));
			fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SQL(.sql)", "*.sql"));
			File fExcell = fileChooser.showOpenDialog(app.getPrimaryStage());
			if (fExcell != null) {
				System.out.println("dosya adi :" + fExcell.getAbsolutePath());
				cbDbResources.getValue().setDescription(fExcell.getAbsolutePath());
			} else
				cbDbResources.getValue().setDescription("");
		}

	}

	// Event Listener on ComboBox[#scmKynBaglanti].onAction
	@FXML
	public void onSourceDbChanged(ActionEvent event) {
		synchronize(false, null);
		sourceDb = cbSourceDb.getValue();
		synchronize(true, null);
	}

	// Event Listener on ComboBox[#scmKynBaglanti].onAction
	@FXML
	public void onTargetDbChanged(ActionEvent event) {
		targetDb = cbTargetDb.getValue();
	}

	// Event Listener on Button[#tusVtOlustur].onAction
	@FXML
	public void onCreateDb(ActionEvent event) {
		System.setProperty("derby.system.home", BaseConstants.getRootAddress());
		getConfirm().setContentText(BaseConstants.getString("FrmConfig.CreateDb.Warning"));
		Optional<ButtonType> dC = getConfirm().showAndWait();
		if (dC.get() == AForm.CONFIRM_OK) {
			ResourceWalker rw = new ResourceWalker();

			try {
				String dbResourceFile = "/sql/creates";
				rw.walk(dbResourceFile);
				IReference<String> selected = cbDbResources.getValue();
				if ("-1".equals(selected.getKey())) {
					getExportModel().executeSQLfromFile(selected.getDescription());
				} else {
					dbResourceFile += "/" + selected.getValue();
					getExportModel().executeSQLfromResourceFile(dbResourceFile);
				}
			} catch (Exception e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, null, e);
			}

		}

	}

	// Event Listener on CheckBox[#onyGosterParola].onAction
	@FXML
	public void onShowPassword(ActionEvent event) {
		txtParolaA.setVisible(onyGosterParola.isSelected());
		txtParola.setVisible(!onyGosterParola.isSelected());
	}

	@FXML
	public void onDefaultDbChanged(ActionEvent event) {
		if (onyDefaultDb.isSelected())
			setDefultDb(cbSourceDb.getValue());
	}

	private void findCount(MyExports pVs, int pMax) {
		if (pVs != null) {
			pVs.setSourceCount(0);
			tListe.refresh();
			Service<Integer> count = new Service<Integer>() {
				@Override
				protected Task<Integer> createTask() {
					return new Task<Integer>() {
						@Override
						protected Integer call() throws Exception {
							return new ExportModel().findDbTableCount(pVs.getSourceSchema(), pVs.getSourceTable());
						}
					};
				}

				@Override
				protected void failed() {
					super.failed();
					updateProgress(pMax);
				}

				@Override
				protected void succeeded() {
					super.succeeded();
					pVs.setSourceCount(getValue());
					tListe.refresh();
					updateProgress(pMax);
				}
			};
			count.start();
		}
	}

	@FXML
	public void onFindCounts(ActionEvent event) {
		List<MyExports> selection = tListe.getSelectionModel().getSelectedItems();
		updateProgress(0);
		if (!BaseConstants.isEmpty(selection)) {
			selection.forEach(vs -> findCount(vs, selection.size()));
		}
	}

	private void updateGui(PHASE phase, int taskCount, String message) {
		Platform.runLater(() -> tListe.refresh());
		if (phase != PHASE.PROCEED)
			Platform.runLater(() -> txtConsole.appendText(message + BaseConstants.EOL));

		if (phase == PHASE.ENDS_ALL || phase == PHASE.FAILS_ALL)
			updateProgress(taskCount);
	}

	@FXML
	public void delete(ActionEvent event) {
		List<MyExports> selection = tListe.getSelectionModel().getSelectedItems();
		if (!BaseConstants.isEmpty(selection)) {
			List<Export> list = new ArrayList<>();
			selection.forEach(e -> {
				e.delete();
				list.add(e);
			});
			IResult<List<Export>> res = getExportModel().saveAll(list);
			if (res.isSuccess())
				refresh(event);
		}
	}

	// Event Listener on Button[#tusAktar].onAction
	@FXML
	public void onExport(ActionEvent event) {
		targetDb = cbTargetDb.getValue();
		List<MyExports> selection = tListe.getSelectionModel().getSelectedItems();
		updateProgress(0);
		if (!BaseConstants.isEmpty(selection)) {
			int taskCount = selection.size();
			dbExport = new DbExport(taskCount);
			Object[] sorted = selection.toArray();
			Arrays.sort(sorted);
			for (int dI = 0; dI < taskCount; dI++) {
				MyExports vs = (MyExports) sorted[dI];
				vs.setDeleteTargetTableRows(vs.deleteTargetTableRowsProperty().get());
				vs.progresProperty().set(0.0);
				dbExport.export(targetDb, vs, (phase, progress, count, message) -> {
					vs.onProceed(phase, progress, count, message);
					updateGui(phase, taskCount, message);
				}, 10);
			}
		}
	}

	// Event Listener on Button[#tusVtYaz].onAction
	@FXML
	public void onSaveExports(ActionEvent event) {
		List<IDataEntity> updateList = new ArrayList<>();
		List<IDataEntity> addList = new ArrayList<>();
		transferList.forEach(v -> {
			if (v.isNew()) {
				v.setTargetSchema(txtHdfSema.getText());
				addList.add(v);
			} else if (v.isUpdated()) {
				updateList.add(v);
			}
		});
		result = getExportModel().saveAtomic(addList, updateList);
		if (result.isSuccess()) {
			transferList.forEach(MyExports::accept);
			addMessage(BaseConstants.MESSAGE_INFO, result.getMessage());
		} else
			addMessage(BaseConstants.MESSAGE_WARNING, result.getMessage());
		getAlert().showAndWait();
	}

	// Event Listener on Button[#tusDurdur].onAction
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@FXML
	public void onCancelExports(ActionEvent event) {
		if (dbExport != null) {
			dbExport.cancelExport((List) tListe.getSelectionModel().getSelectedItems());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void save(ActionEvent pArg0) {
		getExportModel().saveConfig((List) connConfList);
		etkKynBaglanti.setText(sourceDb.toString());
	}

	@SuppressWarnings({ "unchecked" })
	public void onFindDbTables(ActionEvent event) {
		Export transfer = new Export(txtKynSema.getText(), "", txtHdfSema.getText(), "");
		AForm dFrmIslem = showDialog(".DbTables", BaseConstants.getString("FrmTabloSec.etkBaslik"), transfer, null,
				false);
		result = dFrmIslem.getResult();
		if (result != null && result.isSuccess()) {
			list = AModel.load((List<IDataEntity>) result.getData(), MyExports.class);
			list.forEach(v -> {
				if (transferList.stream().noneMatch(e -> e.equals(v)))
					transferList.add((MyExports) v);
			});
		}

		txtSuz.setText("A");
		txtSuz.setText("");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void refresh(ActionEvent arg0) {
		tListe.setItems(null);
		transferList.clear();
		List<Export> list1 = getExportModel().getExportList(txtKynSema.getText(), txtHdfSema.getText());
		if (list1 != null) {
			transferList.addAll((List) AModel.load(list1, MyExports.class));
		}
		filteredTransferList = new FilteredList<>(FXCollections.observableList(transferList), p -> true);
		tListe.setItems(filteredTransferList);
		txtSuz.setText("A");
		txtSuz.setText("");
	}
}
