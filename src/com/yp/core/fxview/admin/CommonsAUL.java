package com.yp.core.fxview.admin;

import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import com.yp.admin.data.Common;
import com.yp.core.BaseConstants;
import com.yp.core.db.Pager;
import com.yp.core.entity.IDataEntity;
import com.yp.core.entity.IResult;

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

public class CommonsAUL extends RootPage {
	@FXML
	private TableView<IDataEntity> tSubitems1;
	@FXML
	private TableView<IDataEntity> tSubitems2;
	@FXML
	private Label txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtShortname;
	@FXML
	private TextField txtDef;
	@FXML
	private ComboBox<Common> cmbRoot;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnAddLeaf;
	@FXML
	private Button btnAddParent;
	@FXML
	private Button btnParent;

	private Pager pager = new Pager();
	private Common selectedRoot;
	private List<Common> rootSubitems;
	private List<Common> subitems1;
	private List<Common> subitems2;
	private Deque<IDataEntity> refNode;

	public void initialize(final URL location, final ResourceBundle resources) {		
		refNode = new LinkedList<>();
		selectedRoot = new Common(0);
		readRoots();
		buildSubitems1Table();
		buildSubitems2Table();
	}

	public String getHelpFileName() {
		return null;
	}

	
	private void readRoots() {
		pager.reset(50);
		IResult<List<Common>> res = getCommonModel().findByParent(0, pager);
		rootSubitems = res.getData();
		pager.setLength(res.getDataLength());
		
		cmbRoot.setItems(FXCollections.observableArrayList(rootSubitems));
		if (selectedRoot != null) {
			cmbRoot.getSelectionModel().select(selectedRoot);
		}
	}

	public void synchronize(final boolean pToForm, final Object[] pAdditionalParams) {
		if (dataEntity != null) {
			final Common de = (Common) dataEntity;
			if (pToForm) {
				txtId.setText(de.getId().toString());
				txtName.setText(de.getName());
				txtShortname.setText(de.getAbrv());
				txtDef.setText(de.getDescription());
				if (!de.isLeaf()) {
					pager.reset(50);
					IResult<List<Common>> res = getCommonModel().findByParent(de.getId(), pager);
					System.out.println("count select items :" + res.getDataLength());
					pager.setLength(res.getDataLength());
					subitems2 = res.getData();
					refresh(tSubitems2, subitems2, (IDataEntity) null);
				}
				checkFormItems(de.isLeaf());
			} else {
				de.setName(txtName.getText());
				de.setAbrv(txtShortname.getText());
				de.setDescription(txtDef.getText());
			}
		}
	}

	@Override
	public void refresh(final ActionEvent arg0) {
		readRoots();
		tSubitems1.setItems(null);
	}

	private void sellectItem(final IDataEntity de) {
		refNode.add(dataEntity);
		dataEntity = de;
	}

	private void update(final ActionEvent event) {
		refNode.clear();
		sellectItem(tSubitems1.getSelectionModel().getSelectedItem());
		if (dataEntity != null) {
			synchronize(true, null);
		}
	}

	private void buildSubitems1Table() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem addMenuItem = new MenuItem(BaseConstants.getString("EkleYeni"));
		final MenuItem updateMenuItem = new MenuItem(BaseConstants.getString("Guncelle"));
		addMenuItem.setOnAction(this::add);
		updateMenuItem.setOnAction(this::update);
		contextMenu.getItems().addAll(addMenuItem, new SeparatorMenuItem(), updateMenuItem, new SeparatorMenuItem());
		tSubitems1.contextMenuProperty().set(contextMenu);
		tSubitems1.getSelectionModel().selectedIndexProperty().addListener((sec, si1, si2) -> {
			refNode.clear();
			sellectItem(tSubitems1.getSelectionModel().getSelectedItem());
			synchronize(true, null);
		});
	}

	private void update2(final ActionEvent event) {
		sellectItem(tSubitems2.getSelectionModel().getSelectedItem());
		if (dataEntity != null) {
			synchronize(true, null);
		}
	}

	private void buildSubitems2Table() {
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem addParentMenuItem = new MenuItem(BaseConstants.getString("FrmAppAUL.Add.Parent"));
		final MenuItem addLeafMenuItem = new MenuItem(BaseConstants.getString("FrmAppAUL.Add.Leaf"));
		final MenuItem updateMenuItem = new MenuItem(BaseConstants.getString("Guncelle"));
		addParentMenuItem.setOnAction(this::addParent);
		addLeafMenuItem.setOnAction(this::addLeaf);
		updateMenuItem.setOnAction(this::update2);
		tSubitems2.setRowFactory(tv -> {
			final TableRow<IDataEntity> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getClickCount() == 2) {
					sellectItem(row.getItem());
					synchronize(true, null);
				}
			});
			return row;
		});
		contextMenu.getItems().addAll(updateMenuItem, new SeparatorMenuItem(), addParentMenuItem,
				new SeparatorMenuItem(), addLeafMenuItem, new SeparatorMenuItem());
		tSubitems2.contextMenuProperty().set(contextMenu);
	}

	@Override
	public void save(final ActionEvent arg0) {
		synchronize(false, null);
		result = getCommonModel().save((Common) dataEntity, getUser());
		if (result.isSuccess()) {
			addMessage(BaseConstants.MESSAGE_INFO, result.getMessage());
			if (((Common) dataEntity).getLevel() > 2) {
				goUp(arg0);
			} else {
				selectRoot(arg0);
				// refresh(arg0);
			}
		} else {
			addMessage(BaseConstants.MESSAGE_WARNING, result.getMessage());
		}
		getAlert().showAndWait();
	}

	public void addRoot(final ActionEvent arg0) {
		add(Common.root, this.rootSubitems, true);
		selectedRoot = (Common) dataEntity;
	}

	@Override
	public void add(final ActionEvent arg0) {
		add(selectedRoot, subitems1, true);
	}

	public void addParent(final ActionEvent arg0) {
		add((Common) dataEntity, subitems2, false);
	}

	public void addLeaf(final ActionEvent arg0) {
		add((Common) dataEntity, subitems2, true);
	}

	private void add(final Common pParent, final List<Common> pSubitems, final boolean pIsLeaf) {
		if (pParent != null) {
			int size = 0;
			if (!BaseConstants.isEmpty(pSubitems))
				size = pSubitems.size();
			sellectItem(pParent.addSubitem(size, pIsLeaf));
			synchronize(true, null);
		}
	}

	public void goUp(final ActionEvent arg0) {
		if (!refNode.isEmpty()) {
			dataEntity = refNode.removeLast();
			synchronize(true, null);
		}
	}

	private void checkFormItems(final boolean pDisable) {
		btnAddLeaf.setDisable(pDisable);
		btnAddParent.setDisable(pDisable);
		btnParent.setDisable(refNode.size() < 2);
	}

	public void selectRoot(final ActionEvent arg0) {
		selectedRoot = cmbRoot.getValue();
		if (selectedRoot != null) {
			pager.reset(50);
			IResult<List<Common>> res = getCommonModel().findByParent(selectedRoot.getId(), pager);
			System.out.println("count select root :" + res.getDataLength());
			pager.setLength(res.getDataLength());
			subitems1 = res.getData();
			refresh(tSubitems1, subitems1, (IDataEntity) null);
		}
	}
}
