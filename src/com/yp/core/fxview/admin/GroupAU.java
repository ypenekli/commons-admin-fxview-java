package com.yp.core.fxview.admin;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.yp.admin.data.Group;
import com.yp.admin.data.App;
import com.yp.core.BaseConstants;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class GroupAU extends RootPage {
	@FXML
	private Label txtGroupId;
	@FXML
	private ComboBox<App> chApps;
	@FXML
	private TextField txtGroupName;
	
	
	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
	}

	public String getHelpFileName() {
		return null;
	}

	@Override
	public void synchronize(final boolean pToForm, final Object[] pAdditionalParams) {
		if (dataEntity != null) {
			final Group group = (Group) dataEntity;
			if (pToForm) {
				txtGroupId.setText(group.getId().toString());
				txtGroupName.setText(group.getName());
				chApps.getSelectionModel().select(new App(group.getAppId()));
			} else {
				group.setName(txtGroupName.getText());
				if (chApps.getSelectionModel().getSelectedIndex() > -1) {
					group.setAppId(chApps.getValue().getId());
				}
			}
			chApps.setDisable(!dataEntity.isNew());
		}
	}

	@Override
	public void refresh(final ActionEvent arg0) {
		chApps.getItems().clear();
		List<App> appList = getAppModel().findApps(getUser().getId());
		if (!BaseConstants.isEmpty(appList))
			chApps.setItems(FXCollections.observableArrayList(appList));
	}

	@Override
	public void save(final ActionEvent arg0) {
		synchronize(false, null);
		result = this.getGroupModel().save((Group) dataEntity, getUser());
		if (result.isSuccess()) {
			addMessage(BaseConstants.MESSAGE_INFO, result.getMessage());
		} else {
			addMessage(BaseConstants.MESSAGE_WARNING, result.getMessage());
		}
		getAlert().showAndWait();
	}

	@Override
	public void close(ActionEvent arg0) {
		hide(arg0);
	}

	public void add(ActionEvent arg0) {
		dataEntity = new Group(-1);
		synchronize(true, (Object[]) null);
	}
}
