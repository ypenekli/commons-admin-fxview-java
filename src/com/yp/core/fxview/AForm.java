package com.yp.core.fxview;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import com.yp.Root;
import com.yp.admin.model.GroupModel;
import com.yp.admin.model.UserModel;
import com.yp.core.BaseConstants;
import com.yp.core.entity.IDataEntity;
import com.yp.core.entity.IResult;
import com.yp.core.fxview.web.Browser;
import com.yp.core.tools.DateTime;
import com.yp.core.tools.StringTool;
import com.yp.core.user.IUser;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class AForm implements Initializable {

	@FXML
	protected String id;
	@FXML
	protected Pane self;

	protected static ALauncher app;
	@SuppressWarnings("rawtypes")
	protected IResult result;
	protected IDataEntity dataEntity;
	protected List<IDataEntity> list;
	protected static NumberFormat formatCurrency;
	protected String message;
	private Alert alert;
	private Alert confirm;
	private UserModel userModel;
	private GroupModel groupModel;
	private Date date;
	private Calendar c;
	private Property<String> mode;
	private BooleanProperty hideClose;
	private BooleanProperty hideRefresh;
	private BooleanProperty hideAdd;
	private BooleanProperty hideSave;
	private BooleanProperty hideReport;
	private StringProperty labelSave;
	protected OnFindAllCompletedListener<?> onFindAllCompletedListener;
	protected OnFindOneCompletedListener<?> onFindOneCompletedListener;
	protected OnSaveCompletedListener<?> onSaveCompletedListener;

	static {
		formatCurrency = NumberFormat.getCurrencyInstance();
	}

	protected AForm() {
		this.mode = new SimpleStringProperty();
		this.hideClose = new SimpleBooleanProperty();
		this.hideRefresh = new SimpleBooleanProperty();
		this.hideAdd = new SimpleBooleanProperty();
		this.hideSave = new SimpleBooleanProperty();
		this.hideReport = new SimpleBooleanProperty();
		this.labelSave = new SimpleStringProperty(BaseConstants.getString("Save"));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void refresh(final TableView<? extends IDataEntity> pTablo, final List pList) {
		pTablo.setItems(null);
		if (!BaseConstants.isEmpty(pList)) {
			pTablo.setItems(FXCollections.observableList(pList));
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IDataEntity refresh(final TableView pTablo, final List pList, final IDataEntity pSelection) {
		IDataEntity selection = pSelection;
		if (selection == null)
			selection = (IDataEntity) pTablo.getSelectionModel().getSelectedItem();

		pTablo.setItems(null);
		if (!BaseConstants.isEmpty(pList)) {
			IDataEntity dVs = (IDataEntity) pList.get(pList.size() - 1);
			if (dVs.isNew())
				pList.remove(pList.size() - 1);

			pTablo.layout();
			pTablo.setItems(FXCollections.observableList(pList));
			if (selection != null)
				pTablo.getSelectionModel().select(selection);
			else {
				pTablo.getSelectionModel().clearAndSelect(0);
				selection = (IDataEntity) pTablo.getSelectionModel().getSelectedItem();
			}
		}
		return selection;
	}

	public abstract String getHelpFileName();

	public abstract void synchronize(boolean pToForm, Object[] pAdditionalParams);

	public String getMessage() {
		return message;
	}

	protected Alert getAlert() {
		if (alert == null) {
			ButtonType ok = new ButtonType(BaseConstants.getString("Tamam"), ButtonBar.ButtonData.OK_DONE);
			alert = new Alert(AlertType.INFORMATION, "", ok);
			alert.setResizable(true);
			alert.setTitle(app.getApplicationName());
			alert.setHeaderText("");
		}
		return alert;
	}

	public static final ButtonType CONFIRM_OK = new ButtonType(BaseConstants.getString("Tamam"),
			ButtonType.OK.getButtonData());
	public static final ButtonType CONFIRM_CANCEL = new ButtonType(BaseConstants.getString("Iptal"),
			ButtonType.CANCEL.getButtonData());

	protected Alert getConfirm() {
		if (confirm == null) {
			confirm = new Alert(AlertType.CONFIRMATION, "", CONFIRM_OK, CONFIRM_CANCEL);
			confirm.setResizable(true);
			confirm.setTitle(app.getApplicationName());
			confirm.setHeaderText("");
		}
		return confirm;
	}

	public void addMessage(String summary, String detail) {
		getAlert().setHeaderText(summary);
		if (summary.equals(BaseConstants.MESSAGE_WARNING))
			alert.setAlertType(AlertType.ERROR);
		else
			alert.setAlertType(AlertType.INFORMATION);

		alert.contentTextProperty().set(detail);
	}

	protected byte[] getImage(String fileName) {
		InputStream is = null;
		byte[] b = null;
		try {
			is = new BufferedInputStream(this.getClass().getClassLoader().getResourceAsStream(fileName));
			b = new byte[is.available()];
			is.read(b);
		} catch (IOException e) {
			app.logger.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
		}
		return b;
	}

	public IUser getUser() {
		return app.user;
	}

	public UserModel getUserModel() {
		if (userModel == null) {
			userModel = new UserModel();
		}
		return userModel;
	}

	public GroupModel getGroupModel() {
		if (groupModel == null) {
			groupModel = new GroupModel();
		}
		return groupModel;
	}

	public IDataEntity getDataEntity() {
		return dataEntity;
	}

	public void show(String pIslvkod, String pUrl, String pBaslik, IDataEntity pDataEntity, ResourceBundle pResources) {
		String fnId = pIslvkod + pUrl;
		app.logger.log(Level.INFO, "Sub function id {0}.", fnId);
		URL dUrl = null;
		Pane dForm = app.getForm(fnId);
		if (dForm == null && pUrl != null) {
			try {
				String dSayfa = app.getFormUrl(pUrl);
				dUrl = Root.class.getResource(dSayfa);
				FXMLLoader fxmlLoader = new FXMLLoader(dUrl, pResources == null ? app.getBundle() : pResources);
				dForm = fxmlLoader.load();
				AForm aForm = fxmlLoader.<AForm>getController();
				aForm.id = pIslvkod;
				dForm.setUserData(aForm);
				dForm.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
					if (evt.getCode() == KeyCode.F1) {
						showHelp("YARDIM", null, aForm);
					}
				});
				app.loadForm(fnId, dForm);
			} catch (Exception e) {
				app.logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		if (dForm != null) {
			if (!StringTool.isNull(pBaslik))
				app.getPrimaryStage().setTitle(app.getApplicationName() + "-> " + pBaslik);

			BorderPane.setAlignment(dForm, Pos.TOP_LEFT);
			app.getHome().root.setCenter(dForm);

			AForm aForm = (AForm) dForm.getUserData();
			aForm.dataEntity = pDataEntity;
			aForm.refresh(null);
			aForm.synchronize(true, null);
		}
	}

	protected AForm showModal(String pIslvkod, String pUrl, String pTitle, IDataEntity pDataEntity,
			List<IDataEntity> pList, boolean pResizable) {
		String fnId = pIslvkod + pUrl;
		app.logger.log(Level.INFO, "Sub function id {0}.", fnId);
		URL dUrl = null;
		Pane dForm = app.getForm(fnId);
		if (dForm == null && pUrl != null) {
			try {
				String dSayfa = app.getFormUrl(pUrl);
				dUrl = Root.class.getResource(dSayfa);
				FXMLLoader fxmlLoader = new FXMLLoader(dUrl, app.getBundle());
				dForm = fxmlLoader.load();
				AForm aForm1 = fxmlLoader.<AForm>getController();
				aForm1.id = pIslvkod;
				dForm.setUserData(aForm1);
				dForm.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
					if (evt.getCode() == KeyCode.F1) {
						showHelp("YARDIM", null, aForm1);
					}
				});

				aForm1.setOnFindAllCompletedListener(onFindAllCompletedListener);
				aForm1.setOnFindOneCompletedListener(onFindOneCompletedListener);
				aForm1.setOnSaveCompletedListener(onSaveCompletedListener);

				app.loadForm(fnId, dForm);
			} catch (Exception e) {
				app.logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		if (dForm != null) {
			Scene scene = dForm.getScene();
			if (scene == null)
				scene = new Scene(dForm);
			Stage stage = new Stage();
			stage.setResizable(pResizable);
			stage.setScene(scene);
			stage.setTitle(pTitle);
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(app.getPrimaryStage().getScene().getWindow());
			stage.centerOnScreen();
			final AForm sncForm = (AForm) dForm.getUserData();
			stage.setOnCloseRequest(handle -> sncForm.close((ActionEvent) null));

			sncForm.dataEntity = pDataEntity;
			sncForm.list = pList;
			sncForm.refresh(null);
			sncForm.synchronize(true, null);
			stage.showAndWait();
			return sncForm;
		}
		return null;
	}

	public AForm showDialog(String pUrl, String pTitle, IDataEntity pDataEntity, List<IDataEntity> pList,
			boolean pResizable) {
		Stage stage = new Stage();
		Parent root = null;
		AForm aForm = null;
		try {
			String dSayfa = app.getFormUrl(pUrl);
			URL dUrl = Root.class.getResource(dSayfa);
			FXMLLoader fxmlLoader = new FXMLLoader(dUrl, app.getBundle());
			root = fxmlLoader.load();
			aForm = fxmlLoader.<AForm>getController();
			aForm.setOnFindAllCompletedListener(onFindAllCompletedListener);
			aForm.setOnFindOneCompletedListener(onFindOneCompletedListener);
			aForm.setOnSaveCompletedListener(onSaveCompletedListener);
		} catch (IOException e) {
			app.logger.log(Level.SEVERE, e.getMessage(), e);
		}
		if (root != null) {
			Scene scene = new Scene(root);
			stage.setResizable(pResizable);
			stage.setScene(scene);
			stage.setTitle(pTitle);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.centerOnScreen();

			aForm.dataEntity = pDataEntity;
			aForm.list = pList;
			aForm.refresh(null);
			aForm.synchronize(true, null);

			stage.showAndWait();
		}
		return aForm;
	}

	public AForm showModelessDialog(String pUrl, String pTitle, IDataEntity pDataEntity, List<IDataEntity> pList,
			boolean pResizable) {
		Stage stage = new Stage();
		Parent root = null;
		AForm aForm = null;
		try {
			String dSayfa = app.getFormUrl(pUrl);
			URL dUrl = Root.class.getResource(dSayfa);
			FXMLLoader fxmlLoader = new FXMLLoader(dUrl, app.getBundle());
			root = fxmlLoader.load();
			aForm = fxmlLoader.<AForm>getController();
			aForm.setOnFindAllCompletedListener(onFindAllCompletedListener);
			aForm.setOnFindOneCompletedListener(onFindOneCompletedListener);
			aForm.setOnSaveCompletedListener(onSaveCompletedListener);
		} catch (IOException e) {
			app.logger.log(Level.SEVERE, e.getMessage(), e);
		}
		if (root != null) {
			Scene scene = new Scene(root);
			stage.setResizable(pResizable);
			stage.setScene(scene);
			stage.setTitle(pTitle);
			stage.initOwner(app.getPrimaryStage().getScene().getWindow());
			stage.centerOnScreen();

			aForm.dataEntity = pDataEntity;
			aForm.list = pList;
			aForm.refresh(null);
			aForm.synchronize(true, null);

			stage.show();
		}
		return aForm;
	}

	public void showWebDialog(String pUrl, String pTitle, String pMimeType, IDataEntity pDataEntity) {
		if (app.WEB == null && pUrl != null) {
			Stage stage = new Stage();
			stage.setTitle(pTitle);
			Browser bw = new Browser();
			Scene scene = new Scene(bw, 750, 500, Color.web("#666970"));
			stage.setScene(scene);
			stage.setUserData(pDataEntity);
			app.WEB = stage;
			stage.setOnCloseRequest(event -> System.out.println("Stage is closing"));
		}
		((Browser) app.WEB.getScene().getRoot()).load(pUrl, pTitle, pMimeType);
		app.WEB.showAndWait();
	}

	public void showWebDialog(String pContent, String pTitle, String pMimeType) {
		if (app.WEB == null && pContent != null) {
			Stage stage = new Stage();
			stage.setTitle(pTitle);
			Browser bw = new Browser();
			Scene scene = new Scene(bw, 750, 500, Color.web("#666970"));
			stage.setScene(scene);
			// stage.setUserData(pDataEntity);
			app.WEB = stage;
			stage.setOnCloseRequest(event -> System.out.println("Stage is closing"));
		}
		((Browser) app.WEB.getScene().getRoot()).loadContent(pContent, pMimeType);
		app.WEB.showAndWait();
	}

	public void showHelp(String pBaslik, String pMimeTipi, AForm pForm) {
		String url = pForm == null ? getHelpFileName() : pForm.getHelpFileName();
		if (!StringTool.isNull(url)) {
			if (app.getHelpUrl().startsWith("http")) {
				url = app.getHelpUrl() + url;
			} else {
				url = "file:///" + BaseConstants.getRootAddress() + app.getHelpUrl() + url;
			}
			showWebDialog(url, pBaslik, pMimeTipi, null);
		}
	}

	public void close(AForm pForm) {
		// Pane dP = (Pane) session.getPrimaryStage().getScene().getRoot();
		// dP.getChildren().removeAll(session.getForm(pForm.id));
		app.getHome().root.setCenter(null);
	}

	protected String getClientIP() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException h) {
			app.logger.log(Level.SEVERE, h.getMessage(), h);
		}
		return "1.1.1.1";
	}

	protected void setLastUserInfo(IDataEntity pDataEntity) {
		String user = "Admin@yp.com";
		if (app.getUser() != null) {
			user = app.getUser().getEmail();
		}
		pDataEntity.setLastClientInfo(user, getClientIP(), getDate());
	}

	protected void setUserInfo(IDataEntity pDataEntity) {
		String user = "Admin@yp.com";
		if (app.getUser() != null) {
			user = app.getUser().getEmail();
		}
		pDataEntity.setClientInfo(user, getClientIP(), getDate());
	}

	public Date getDate() {
		if (date == null) {
			date = new Date();
		}
		return date;
	}

	public Calendar today() {
		if (c == null) {
			c = Calendar.getInstance();
		}
		return c;
	}

	public String getDateTR(BigDecimal pDate) {
		return DateTime.asDateTR(pDate);
	}

	public void setDataEntity(IDataEntity pDataEntity) {
		dataEntity = pDataEntity;
		if (dataEntity != null) {
			setMode(dataEntity.isNew() ? BaseConstants.NEW : BaseConstants.UPDATE);
		}
	}

	public Property<String> modeProperty() {
		return mode;
	}

	public String getMode() {
		return mode.getValue();
	}

	public void setMode(String pMode) {
		mode.setValue(pMode);
	}

	public BooleanProperty hideCloseProperty() {
		return hideClose;
	}

	public BooleanProperty hideRefreshProperty() {
		return hideRefresh;
	}

	public BooleanProperty hideAddProperty() {
		return hideAdd;
	}

	public BooleanProperty hideSaveProperty() {
		return hideSave;
	}

	public BooleanProperty hideReportProperty() {
		return hideReport;
	}

	public Boolean getHideClose() {
		return hideClose.getValue();
	}

	public void setHideClose(Boolean pGosterMe) {
		hideClose.setValue(pGosterMe);
	}

	public Boolean getHideRefresh() {
		return hideRefresh.getValue();
	}

	public void setHideRefresh(Boolean pGosterMe) {
		hideRefresh.setValue(pGosterMe);
	}

	public Boolean getHideAdd() {
		return hideAdd.getValue();
	}

	public void setHideAdd(Boolean pGosterMe) {
		hideAdd.setValue(pGosterMe);
	}

	public Boolean getHideSave() {
		return hideSave.getValue();
	}

	public void setHideSave(Boolean pGosterMe) {
		hideSave.setValue(pGosterMe);
	}

	public Boolean getHideReport() {
		return hideReport.getValue();
	}

	public void setHideReport(Boolean pHide) {
		hideReport.setValue(pHide);
	}

	public StringProperty labelSaveProperty() {
		return labelSave;
	}

	public String getLabelSave() {
		return labelSave.getValue();
	}

	public void setLabelSave(String pEtiket) {
		labelSave.setValue(pEtiket);
	}

	@FXML
	public void close(ActionEvent arg0) {
		show(id, ".Empty", null, dataEntity, null);
	}
	
	public void hide(final ActionEvent arg0) {
		((Stage) self.getScene().getWindow()).hide();
	}

	@FXML
	public void cancel(ActionEvent arg0) {
		show(id, ".Empty", null, dataEntity, null);
	}

	@FXML
	public void refresh(ActionEvent arg0) {
		System.out.println("refresh");
	}

	@FXML
	public void add(ActionEvent arg0) {
		System.out.println("ekle");
	}

	@FXML
	public void save(ActionEvent arg0) {
		System.out.println("kaydet");
	}

	@FXML
	public void report(ActionEvent arg0) {
		System.out.println("raporla");
	}

	@SuppressWarnings("rawtypes")
	@FXML
	public IResult getResult() {
		return result;
	}

	public void customResize(TableView<?> view) {

		AtomicLong width = new AtomicLong();
		view.getColumns().forEach(col -> {
			width.addAndGet((long) col.getWidth());
		});
		double tableWidth = view.getWidth();

		if (tableWidth > width.get()) {
			view.getColumns().forEach(col -> {
				col.setPrefWidth(col.getWidth() + ((tableWidth - width.get()) / view.getColumns().size()));
			});
		}
	}

	public List<IDataEntity> getList() {
		return list;
	}

	public void setListe(List<IDataEntity> pList) {
		list = pList;
	}

	public void setOnFindAllCompletedListener(OnFindAllCompletedListener<?> listener) {
		onFindAllCompletedListener = listener;
	}

	public void setOnFindOneCompletedListener(OnFindOneCompletedListener<?> listener) {
		onFindOneCompletedListener = listener;
	}

	public void setOnSaveCompletedListener(OnSaveCompletedListener<?> listener) {
		onSaveCompletedListener = listener;
	}

	public interface OnFindAllCompletedListener<T extends IDataEntity> {
		void onFindCompleted(List<T> list);
	}

	public interface OnFindOneCompletedListener<T extends IDataEntity> {
		void onFindCompleted(T vs);
	}

	public interface OnSaveCompletedListener<T> {
		void onSaveCompleted(IResult<T> result);
	}
}
