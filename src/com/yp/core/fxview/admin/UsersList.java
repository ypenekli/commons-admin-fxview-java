package com.yp.core.fxview.admin;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.yp.admin.Constants;
import com.yp.admin.data.User;
import com.yp.core.entity.IDataEntity;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class UsersList extends RootPage {
	@FXML
	private TableView<User> tUsers;
	@FXML
	private TextField txtName;
	//@FXML
	//private ToolBar toolBar;
	//@FXML
	//private ToolBar toolBarController;

	public void initialize(final URL location, final ResourceBundle resources) {			
		this.buildUsersTable();
	}

	public String getHelpFileName() {
		return null;
	}

	public void synchronize(final boolean pToForm, final Object[] pAdditionalParams) {
	}

	private void showDialog(final User pUser) {
		showModal(this.id, ".UserAU", Constants.getString("Users.Header"), pUser, null, false);
	}

	private void buildUsersTable() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem addMenuItem = new MenuItem(Constants.getString("EkleYeni"));
		final MenuItem updateMenuItem = new MenuItem(Constants.getString("Guncelle"));
		final MenuItem autorizeMenuItem = new MenuItem(Constants.getString("FrmSahis.Yetki"));
		final MenuItem removeMenuItem = new MenuItem(Constants.getString("Arsiv.Arsivle"));
		removeMenuItem.setStyle("-fx-font-weight: bold;-fx-text-fill: red;");
		addMenuItem.setOnAction(this::add);
		updateMenuItem.setOnAction(this::update);
		removeMenuItem.setOnAction(this::save);

		tUsers.setRowFactory(tv -> {
			final TableRow<User> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					dataEntity = row.getItem();
					if (event.getClickCount() == 2) {
						showDialog((User) dataEntity);
					}
				}
			});
			return row;
		});
		contextMenu.getItems().addAll(addMenuItem, new SeparatorMenuItem(), updateMenuItem, new SeparatorMenuItem(),
				autorizeMenuItem, new SeparatorMenuItem(), removeMenuItem);
		tUsers.contextMenuProperty().set(contextMenu);
		tUsers.getSelectionModel().selectedIndexProperty()
				.addListener((sec, si1, si2) -> dataEntity = tUsers.getSelectionModel().getSelectedItem());
	}

	public void find(final ActionEvent arg0) {
		final List<User> userList = getUserModel().findByName(txtName.getText());
		refresh(tUsers, userList, (IDataEntity) null);
	}

	public void save(final ActionEvent arg0) {
		dataEntity = tUsers.getSelectionModel().getSelectedItem();
		System.out.println("asivle");
	}

	public void add(final ActionEvent arg0) {
		showDialog((User) (dataEntity = new User(-1)));
	}

	public void update(final ActionEvent arg0) {
		dataEntity = tUsers.getSelectionModel().getSelectedItem();
		showDialog((User) dataEntity);
	}

	static void access$1(final UsersList list, final IDataEntity dataEntity) {
		list.dataEntity = dataEntity;
	}
}