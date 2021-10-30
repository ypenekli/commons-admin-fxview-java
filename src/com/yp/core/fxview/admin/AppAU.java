package com.yp.core.fxview.admin;

import java.net.URL;
import java.util.ResourceBundle;

import com.yp.admin.data.App;
import com.yp.admin.model.AppModel;
import com.yp.core.BaseConstants;
import com.yp.core.entity.IDataEntity;
import com.yp.core.ref.IReference;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class AppAU extends RootPage {
	@FXML
	private TableView<IDataEntity> tApps;
	@FXML
	private Label txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtDesc;
	@FXML
	private TextField txtUrl;
	@FXML
	private TextField txtAuthor;
	@FXML
	private TextField txtOrganization;
	@FXML
	private ComboBox<IReference<String>> scmTarget;
	@FXML
	private Button btnSave;

	public void initialize(final URL location, final ResourceBundle resources) {
		scmTarget.setItems(FXCollections.observableArrayList(getAppModel().getTargetList()));
		setHideAdd(true);
	}

	public String getHelpFileName() {
		return null;
	}

	public void synchronize(final boolean pToForm, final Object[] pAdditionalParams) {
		if (dataEntity != null) {
			final App de = (App) dataEntity;
			if (pToForm) {
				txtId.setText(de.getId());
				txtName.setText(de.getName());
				txtDesc.setText(de.getDescription());
				txtUrl.setText(de.getUrl());
				txtAuthor.setText(de.getAutor());
				txtOrganization.setText(de.getOrganization());
				final IReference<String> target = AppModel.TARGET.get(de.getTarget());
				if (target != null) {
					scmTarget.getSelectionModel().select(target);
				} else {
					scmTarget.getSelectionModel().clearSelection();
				}
			} else {				
				de.setName(txtName.getText());
				de.setDescription(txtDesc.getText());
				de.setUrl(txtUrl.getText());
				de.setAuthor(txtAuthor.getText());
				de.setOrganization(txtOrganization.getText());
				if (scmTarget.getSelectionModel().getSelectedIndex() > -1) {
					de.setTarget(scmTarget.getValue().getKey());
				}
			}
		}
	}

	@Override
	public void save(final ActionEvent arg0) {
		synchronize(false, null);
		result = getAppModel().save((App) dataEntity, getUser());
		if (result.isSuccess()) {
			addMessage(BaseConstants.MESSAGE_INFO, result.getMessage());
		} else {
			addMessage(BaseConstants.MESSAGE_WARNING, this.result.getMessage());
		}
		this.getAlert().showAndWait();
	}

//	@Override
//	public void add(final ActionEvent arg0) {
//		if(!dataEntity.isNew()) {
//			String id = ((Apps)dataEntity).getId();
//			int idx = Integer.parseInt(id.substring(id.indexOf(".") + 1));
//			idx += 1;
//			Apps newApp = new Apps(String.format("0.%s", idx));
//			newApp.setAutor(getUser().getFullName());
//			dataEntity = newApp;
//			synchronize(true, (Object[]) null);
//		}
//	}

	@Override
	public void close(ActionEvent arg0) {
		hide(arg0);
	}

}
