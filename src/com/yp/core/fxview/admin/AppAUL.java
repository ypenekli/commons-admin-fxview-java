package com.yp.core.fxview.admin;

import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.yp.admin.Constants;
import com.yp.admin.data.AppFunc;
import com.yp.admin.data.App;
import com.yp.admin.model.AppFuncModel;
import com.yp.core.BaseConstants;
import com.yp.core.entity.IDataEntity;
import com.yp.core.fxview.AForm;
import com.yp.core.ref.IReference;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AppAUL extends RootPage {
	@FXML
	private TableView<App> tApps;
	@FXML
	private TableView<AppFunc> tSubitems;
	@FXML
	private Label txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtUrl;
	@FXML
	private ComboBox<IReference<String>> scmTarget;
	@FXML
	private Button btnAddFunc;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnUp;
	@FXML
	private ToolBar toolBar;
	
	private AppFunc selectedAppFuncs;
	private List<App> appList;
	private List<AppFunc> subitems;
	private Deque<AppFunc> appNode = new LinkedList<>();

	public void initialize(final URL location, final ResourceBundle resources) {
		scmTarget.setItems(FXCollections.observableArrayList(getAppFuncModel().getTargetList()));
		buildAppsTable();
		buildItemsTable();
	}

	public String getHelpFileName() {
		return null;
	}

	public void synchronize(final boolean pToForm, final Object[] pAdditionalParams) {
		if (selectedAppFuncs != null) {
			if (pToForm) {
				txtId.setText(selectedAppFuncs.getId());
				txtName.setText(selectedAppFuncs.getName());
				txtUrl.setText(selectedAppFuncs.getUrl());
				final IReference<String> target = AppFuncModel.TARGET.get(selectedAppFuncs.getTarget());
				if (target != null) {
					scmTarget.getSelectionModel().select(target);
				} else {
					scmTarget.getSelectionModel().clearSelection();
				}
				if (!selectedAppFuncs.isLeaf()) {
					subitems = getAppFuncModel().findAppFuncs(selectedAppFuncs.getId());
					refresh(tSubitems, subitems, (IDataEntity) null);
				}
				checkFormItems();
			} else {
				selectedAppFuncs.setName(txtName.getText());
				selectedAppFuncs.setUrl(txtUrl.getText());
				if (scmTarget.getSelectionModel().getSelectedIndex() > -1) {
					selectedAppFuncs.setTarget(scmTarget.getValue().getKey());
				}
			}
		}
	}

	@Override
	public void refresh(final ActionEvent arg0) {
		appList = getAppModel().findApps(getUser().getId());
		refresh(tApps, appList, (IDataEntity) null);
	}

	private void showDialog(final App pApp) {
		AForm form = showModal(this.id, ".AppAU", Constants.getString("FrmAppAUL.Header"), pApp, null,
				false);
		if (form.getResult() != null && form.getResult().isSuccess())
			refresh(null);
	}

	private void updateApp(final ActionEvent arg0) {
		dataEntity = tApps.getSelectionModel().getSelectedItem();
		showDialog((App) dataEntity);
	}

	private void buildAppsTable() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem addMenuItem = new MenuItem(BaseConstants.getString("FrmAppAUL.Add.Application"));
		final MenuItem updateMenuItem = new MenuItem(BaseConstants.getString("Guncelle"));

		addMenuItem.setOnAction(this::add);
		updateMenuItem.setOnAction(this::updateApp);

		tApps.setRowFactory(tv -> {
			final TableRow<App> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getClickCount() == 2) {
					updateApp(null);
				}
			});
			return row;
		});

		contextMenu.getItems().addAll(addMenuItem, new SeparatorMenuItem(), updateMenuItem, new SeparatorMenuItem());
		tApps.contextMenuProperty().set(contextMenu);
		tApps.getSelectionModel().selectedIndexProperty().addListener((sec, si1, si2) -> {
			sellectApp(tApps.getSelectionModel().getSelectedItem());
			synchronize(true, null);
		});
	}

	private void updateFunc(final ActionEvent event) {
		sellectAppFuncs(tSubitems.getSelectionModel().getSelectedItem());
		if (selectedAppFuncs != null) {
			synchronize(true, null);
		}
	}

	private void buildItemsTable() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem addLeafMenuItem = new MenuItem(BaseConstants.getString("FrmAppAUL.Add.Leaf"));
		final MenuItem updateMenuItem = new MenuItem(BaseConstants.getString("Guncelle"));
		addLeafMenuItem.setOnAction(this::addFunc);
		updateMenuItem.setOnAction(this::updateFunc);
		tSubitems.setRowFactory(tv -> {
			final TableRow<AppFunc> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getClickCount() == 2) {
					sellectAppFuncs(row.getItem());
					synchronize(true, null);
				}
			});
			return row;
		});
		contextMenu.getItems().addAll(updateMenuItem, new SeparatorMenuItem(), addLeafMenuItem,
				new SeparatorMenuItem());
		tSubitems.contextMenuProperty().set(contextMenu);
	}

	private void sellectApp(final App de) {
		if (de != null) {
			dataEntity = de;
			appNode.clear();
			String appId = de.getId();
			selectedAppFuncs = new AppFunc(appId);
			selectedAppFuncs.setAppId(appId);
			selectedAppFuncs.setParentId(appId);
			selectedAppFuncs.setName(de.getName());
			selectedAppFuncs.setUrl(de.getUrl());
			selectedAppFuncs.setLevel(1);
			selectedAppFuncs.accept();
		}
	}

	private void sellectAppFuncs(final AppFunc de) {
		appNode.add(selectedAppFuncs);
		selectedAppFuncs = de;
	}

	public void save(final ActionEvent arg0) {
		synchronize(false, null);
		result = getAppFuncModel().save(selectedAppFuncs, ((App) dataEntity).getGroupId(), getUser());
		if (result.isSuccess()) {
			addMessage(BaseConstants.MESSAGE_INFO, result.getMessage());
			if (selectedAppFuncs.getLevel() > 1) {
				goUp(arg0);
			} else {
				refresh(arg0);
			}
		} else {
			addMessage(BaseConstants.MESSAGE_WARNING, this.result.getMessage());
		}
		this.getAlert().showAndWait();
	}

	@Override
	public void add(final ActionEvent arg0) {
		int idx = 0;
		if (!BaseConstants.isEmpty(appList))
			idx = appList.size();
		idx += 1;
		App newApp = new App(String.format("0.%s", idx));
		newApp.setAuthor(getUser().getFullName());
		showModal(this.id, ".AppAU", Constants.getString("FrmAppAUL.Header"), newApp, null, false);
	}

	public void addFunc(final ActionEvent arg0) {
		if (selectedAppFuncs != null) {
			sellectAppFuncs(selectedAppFuncs.addSubitem(subitems.size(), true));
			synchronize(true, null);
		}
	}

	public void goUp(final ActionEvent arg0) {
		if (!appNode.isEmpty()) {
			selectedAppFuncs = appNode.removeLast();
			synchronize(true, null);
		}
	}

	private void checkFormItems() {
		btnAddFunc.setDisable(selectedAppFuncs == null || selectedAppFuncs.isNew());
		btnSave.setDisable(selectedAppFuncs.getId().equals(selectedAppFuncs.getParentId()));
	}
}
