package com.yp.core.fxview.admin;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.yp.admin.data.GroupUser;
import com.yp.admin.data.Group;
import com.yp.admin.data.AppFunc;
import com.yp.admin.data.User;
import com.yp.core.BaseConstants;
import com.yp.core.entity.IDataEntity;
import com.yp.core.fxview.AForm;
import com.yp.core.fxview.TreeModel;
import com.yp.core.tools.ITree;
import com.yp.core.user.IUser;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class GroupList extends RootPage {
	@FXML
	private TableView<User> tUsers;
	@FXML
	private TableView<GroupUser> tUsers2;
	@FXML
	private TextField txtName;
	@FXML
	private TableView<IDataEntity> tGroups;
	@FXML
	private TableView<IDataEntity> tSubitems;
	@FXML
	private Tab tab1;
	@FXML
	private Tab tab2;
	@FXML
	private Tab tab3;
	@FXML
	private TreeView<ITree<?>> trAppSubitems;
	@FXML
	private TreeView<ITree<?>> trGroupSubitems;		
	private TreeItem<ITree<?>> selectedTreeItem;
	private ITree<?> selectedItem;
	private IUser selectedUser;
	private GroupUser selectedGroupUser;
	private MenuItem deleteGroupFnMenuItem;

	public void initialize(final URL location, final ResourceBundle resources) {
		buildGroupsTable();
		buildAppsTree();
		buildGroupFnTree();
		buildUsersTable();
		buildGroupUsersTable();
	}

	public String getHelpFileName() {
		return null;
	}

	public void synchronize(final boolean pToForm, final Object[] pAdditionalParams) {
		if (dataEntity != null && pToForm) {
			findAppFns();
			findGroupFns();
			findGroupUsers();
			deleteGroupFnMenuItem.setDisable((boolean) ((Group) this.dataEntity).isAdmin());
		}
	}

	@Override
	public void refresh(final ActionEvent arg0) {
		List<Group> groupList = getGroupModel().findUserGroupList(getUser().getId());
		refresh(tGroups, groupList, (IDataEntity) null);
	}

	private void findAppFns() {
		final Group group = (Group) dataEntity;
		if (group != null) {
			List<AppFunc> appTree = getAppFuncModel().findUserAppFuncs(getUser().getId(), group.getAppId());
			if (appTree != null) {
				TreeItem<ITree<?>> root = TreeModel.buildTreeNode(appTree, 0);
				trAppSubitems.setRoot(root);
				trAppSubitems.getRoot().setExpanded(true);
			}
		}
	}

	private void findGroupFns() {
		final Group group = (Group) dataEntity;
		if (group != null) {
			List<AppFunc> groupTree = getAppFuncModel().findGroupAppFuncs(group.getId(), group.getAppId());
			if (groupTree != null) {
				trGroupSubitems.setRoot(TreeModel.buildTreeNode(groupTree, 0));
				trGroupSubitems.getRoot().setExpanded(true);
			}
		}
	}

	private void buildGroupsTable() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem addMenuItem = new MenuItem(BaseConstants.getString("FrmGroup.Add.Group"));
		final MenuItem updateMenuItem = new MenuItem(BaseConstants.getString("Guncelle"));
		addMenuItem.setOnAction(this::add);
		updateMenuItem.setOnAction(this::update);
		this.tGroups.setRowFactory(tv -> {
			final TableRow<IDataEntity> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty()) {
					dataEntity = row.getItem();
					if (event.getClickCount() == 2) {
						showGroup(null);
					}
				}
			});
			return row;
		});
		contextMenu.getItems().addAll(addMenuItem, new SeparatorMenuItem(), updateMenuItem, new SeparatorMenuItem());
		tGroups.contextMenuProperty().set(contextMenu);
		tGroups.getSelectionModel().selectedIndexProperty().addListener((sec, si1, si2) -> {
			dataEntity = tGroups.getSelectionModel().getSelectedItem();
			synchronize(true, null);
		});
	}

	private void buildAppsTree() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem ekleMenuItem = new MenuItem(BaseConstants.getString("FrmGroup.Add.Islev"));
		ekleMenuItem.setOnAction(this::addFnToGroup);
		contextMenu.getItems().addAll(ekleMenuItem);
		trAppSubitems.contextMenuProperty().set(contextMenu);
		trAppSubitems.getSelectionModel().selectedIndexProperty().addListener((sec, si1, si2) -> {
			selectedTreeItem = trAppSubitems.getSelectionModel().getSelectedItem();
			if (selectedTreeItem != null) {
				selectedItem = selectedTreeItem.getValue();
			}
		});
		trAppSubitems.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void buildGroupFnTree() {
		final ContextMenu contextMenu = new ContextMenu();
		deleteGroupFnMenuItem = new MenuItem(BaseConstants.getString("FrmGroup.Delete.Islev"));
		deleteGroupFnMenuItem.setOnAction(this::deleteFnFromGroup);
		contextMenu.getItems().addAll(deleteGroupFnMenuItem);
		trGroupSubitems.contextMenuProperty().set(contextMenu);
		trGroupSubitems.getSelectionModel().selectedIndexProperty().addListener((sec, si1, si2) -> {
			selectedTreeItem = trGroupSubitems.getSelectionModel().getSelectedItem();
			if (selectedTreeItem != null) {
				selectedItem = selectedTreeItem.getValue();
			}
		});
	}

	private void showGroup(final ActionEvent arg0) {
		final AForm dFrmIslem = showModal(this.id, ".GroupAU", BaseConstants.getString("FrmGroup.Header"), dataEntity,
				(ArrayList<IDataEntity>) null, false);
		result = dFrmIslem.getResult();
		if (result != null && result.isSuccess()) {
			refresh(arg0);
		}
	}

	@Override
	public void add(final ActionEvent arg0) {
		dataEntity = new Group(-1);
		showGroup(arg0);
	}

	public void update(final ActionEvent arg0) {
		dataEntity = tGroups.getSelectionModel().getSelectedItem();
		showGroup(arg0);
	}

	public void dragFn(final MouseEvent event) {
		final Dragboard db = this.trAppSubitems.startDragAndDrop(TransferMode.ANY);
		selectedTreeItem = trAppSubitems.getSelectionModel().getSelectedItem();
		final ClipboardContent content = new ClipboardContent();
		content.putString((String) selectedTreeItem.getValue().getValue());
		db.setContent(content);
		event.consume();
	}

	public void acceptDragEvents(final DragEvent event) {
		if (event.getGestureSource() != this.trGroupSubitems && event.getDragboard().hasString()) {
			event.acceptTransferModes(TransferMode.ANY);
		}
		event.consume();
	}

	private String[] getSellection(final TreeItem<ITree<?>> pSelectedTreeItem, final boolean pSellectParent) {
		final List<String> res = new ArrayList<>();
		res.add((String) pSelectedTreeItem.getValue().getValue());
		if (pSellectParent) {
			getSellectionParents(pSelectedTreeItem, res);
		}
		getSellectionChildren(pSelectedTreeItem, res);
		final String[] de = new String[res.size()];
		return res.toArray(de);
	}

	private List<String> getSellectionParents(final TreeItem<ITree<?>> pSelectedTreeItem, List<String> pSelection) {
		final TreeItem<ITree<?>> parent = pSelectedTreeItem.getParent();
		if (parent == null) {
			return pSelection;
		}
		pSelection.add((String) parent.getValue().getValue());
		return getSellectionParents(parent, pSelection);
	}

	private List<String> getSellectionChildren(final TreeItem<ITree<?>> pSelectedTreeItem, List<String> pSelection) {
		final ObservableList<TreeItem<ITree<?>>> children = pSelectedTreeItem.getChildren();
		if (children != null) {
			for (final TreeItem<ITree<?>> treeItem : children) {
				pSelection.add((String) treeItem.getValue().getValue());
				getSellectionChildren(treeItem, pSelection);
			}
		}
		return pSelection;
	}

	private void expandSelection(final TreeItem<ITree<?>> root, final ITree<?> selectedData) {
		selectedTreeItem = TreeModel.getTreeViewItem(root, selectedData);
		if (selectedTreeItem != null) {
			if (!selectedTreeItem.isLeaf()) {
				selectedTreeItem.setExpanded(true);
			} else if (selectedTreeItem.getParent() != null) {
				selectedTreeItem.getParent().setExpanded(true);
			}
		}
	}

	private void addFnToGroup(final ActionEvent arg0) {
		selectedTreeItem = trAppSubitems.getSelectionModel().getSelectedItem();
		if (selectedTreeItem != null && this.selectedTreeItem.getParent() != null) {
			selectedItem = selectedTreeItem.getValue();
			final String[] adds = getSellection(selectedTreeItem, true);
			getGroupModel().addFnToGroup(((Group) dataEntity).getId(), adds, this.getUser(), getClientIP());
			findGroupFns();
			expandSelection(trGroupSubitems.getRoot(), selectedItem);
		}
	}

	private void deleteFnFromGroup(final ActionEvent arg0) {
		selectedTreeItem = trGroupSubitems.getSelectionModel().getSelectedItem();
		if (this.selectedTreeItem != null && this.selectedTreeItem.getParent() != null) {
			ITree<?> selectedParent = selectedTreeItem.getParent().getValue();
			final String[] deletes = getSellection(selectedTreeItem, false);
			getGroupModel().deleteFnFromGroup(((Group) dataEntity).getId(), deletes, getUser(), getClientIP());
			findGroupFns();
			expandSelection(trGroupSubitems.getRoot(), selectedParent);
		}
	}

	public void dropFn(final DragEvent event) {
		final Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasString()) {
			addFnToGroup(null);
			success = true;
		}
		event.setDropCompleted(success);
		event.consume();
	}

	public void findUsers(final ActionEvent arg0) {
		if (dataEntity != null) {
			final List<User> userList = getUserModel().findByName(txtName.getText());
			refresh(tUsers, userList, (IDataEntity) null);
		}
	}

	public void findGroupUsers() {
		if (dataEntity != null) {
			final List<IDataEntity> userList = getGroupModel().findGroupUsers(((Group) dataEntity).getId());
			refresh(tUsers2, userList, (IDataEntity) null);
		}
	}

	public void dragUser(final MouseEvent event) {
		final Dragboard db = this.trAppSubitems.startDragAndDrop(TransferMode.ANY);
		selectedUser = tUsers.getSelectionModel().getSelectedItem();
		final ClipboardContent content = new ClipboardContent();
		content.putString(selectedUser.getEmail());
		db.setContent(content);
		event.consume();
	}

	public void acceptUserDragEvents(final DragEvent event) {
		if (event.getGestureSource() != this.tUsers && event.getDragboard().hasString()) {
			event.acceptTransferModes(TransferMode.ANY);
		}
		event.consume();
	}

	public void dropUser(final DragEvent event) {
		final Dragboard db = event.getDragboard();
		boolean success = false;
		if (db.hasString()) {
			addUserToGroup(null);
			success = true;
		}
		event.setDropCompleted(success);
		event.consume();
	}

	private void buildUsersTable() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem ekleMenuItem = new MenuItem(BaseConstants.getString("FrmGroup.Add.User"));
		ekleMenuItem.setOnAction(this::addUserToGroup);
		contextMenu.getItems().addAll(ekleMenuItem);
		tUsers.contextMenuProperty().set(contextMenu);
		tUsers.getSelectionModel().selectedIndexProperty()
				.addListener((sec, si1, si2) -> selectedUser = tUsers.getSelectionModel().getSelectedItem());
	}

	private void buildGroupUsersTable() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem ekleMenuItem = new MenuItem(BaseConstants.getString("FrmGroup.Delete.User"));
		ekleMenuItem.setOnAction(this::deleteUserFromGroup);
		contextMenu.getItems().addAll(ekleMenuItem);
		tUsers2.contextMenuProperty().set(contextMenu);
		tUsers2.getSelectionModel().selectedIndexProperty()
				.addListener((sec, si1, si2) -> selectedGroupUser = tUsers2.getSelectionModel().getSelectedItem());
	}

	private void addUserToGroup(final ActionEvent event) {
		selectedUser = tUsers.getSelectionModel().getSelectedItem();
		if (selectedUser != null) {
			final Integer[] usersToAdd = { selectedUser.getId() };
			getGroupModel().addUserToGroup(((Group) dataEntity).getId(), usersToAdd, getUser(), getClientIP());
			findGroupUsers();
		}
	}

	private void deleteUserFromGroup(final ActionEvent event) {
		selectedGroupUser = tUsers2.getSelectionModel().getSelectedItem();
		if (selectedGroupUser != null) {
			final Integer[] usersToDelete = { selectedGroupUser.getUserId() };
			getGroupModel().deleteUserFromGroup(((Group) dataEntity).getId(), usersToDelete, getUser(), getClientIP());
			findGroupUsers();
		}
	}
}
