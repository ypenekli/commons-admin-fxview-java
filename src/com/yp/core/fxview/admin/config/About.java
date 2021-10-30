package com.yp.core.fxview.admin.config;

import java.net.URL;
import java.util.ResourceBundle;

import com.yp.core.BaseConstants;
import com.yp.core.fxview.admin.RootPage;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class About extends RootPage {

	@FXML
	private Label etkYazar, txtYazar, etkTarih, txtTarih;

	@Override
	public void initialize(URL pLocation, ResourceBundle pResources) {
		etkYazar.setText(BaseConstants.getString("Hakkinda.etkYazar"));
		txtYazar.setText(BaseConstants.getString("Hakkinda.txtYazar"));
		etkTarih.setText(BaseConstants.getString("Hakkinda.etkTarih"));
		txtTarih.setText(BaseConstants.getString("Hakkinda.txtTarih"));
	}

	@Override
	public String getHelpFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void synchronize(boolean pToForm, Object[] pAdditionalParams) {
		// TODO Auto-generated method stub

	}

}
