package com.yp.core.fxview.admin.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.yp.admin.data.Export;
import com.yp.core.BaseConstants;
import com.yp.core.entity.IDataEntity;
import com.yp.core.entity.Result;
import com.yp.core.fxview.admin.RootPage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class DbTableList extends RootPage {

	@FXML
	private TableView<IDataEntity> tListe;

	public void initialize(final URL pLocation, final ResourceBundle pResources) {
		tListe.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	private void findDbTables() {
		if (dataEntity != null) {
			Export tf = (Export) dataEntity;
			List<Export> tableList = getExportModel().findDbTables(tf.getSourceSchema());
			if (!BaseConstants.isEmpty(tableList)) {
				tableList.forEach(de -> de.setTargetSchema(tf.getTargetSchema()));
				refresh(tListe, tableList);
			}
		}
	}

	@Override
	public void refresh(ActionEvent arg0) {
		findDbTables();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void close(ActionEvent arg0) {
		list = tListe.getSelectionModel().getSelectedItems();
		if (!BaseConstants.isEmpty(list)) {
			result = new Result<List<IDataEntity>>(true, BaseConstants.MESSAGE_DATA_TRANSFER_SUCCEEDED);
			result.setData(list);
		} else {
			result = new Result<ArrayList<IDataEntity>>(false, BaseConstants.MESSAGE_DATA_TRANSFER_ERROR);
		}
		((Stage) self.getScene().getWindow()).hide();
	}

	@Override
	public void cancel(final ActionEvent arg0) {
		list = null;
		result = new Result<ArrayList<IDataEntity>>(false, BaseConstants.MESSAGE_DATA_TRANSFER_ERROR);
		((Stage) self.getScene().getWindow()).hide();
	}

	public String getHelpFileName() {
		return null;
	}

	public void synchronize(final boolean pToForm, final Object[] pAdditionalParams) {
	}
}
