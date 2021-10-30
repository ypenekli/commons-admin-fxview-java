package com.yp.core.fxview;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Liste extends AForm {

	@FXML
	private WebView web;

	@Override
	public void initialize(URL pLocation, ResourceBundle pResources) {
	}

	@Override
	public String getHelpFileName() {
		return null;
	}


	@Override
	public void refresh(ActionEvent arg0) {
		String url = app.getApplicationWebUrl();
		if (dataEntity != null && !dataEntity.isNull("url")) {
			url = (String) dataEntity.get("url");
		}
		WebEngine wb = web.getEngine();
		wb.load(url);
	}

	@Override
	public void synchronize(boolean pToForm, Object[] pAdditionalParams) {
	}

}
