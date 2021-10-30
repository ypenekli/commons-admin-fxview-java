package com.yp.core.fxview.web;

import com.yp.core.tools.StringTool;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Browser extends Region {
	final WebView browser;
	final WebEngine webEngine;

	public Browser() {
		this.browser = new WebView();
		this.webEngine = this.browser.getEngine();
		this.getStyleClass().add("browser");
		this.getChildren().add(this.browser);
	}

	public Browser(String pUrl, String pHeader, String pMimeType) {
		this();
		load(pUrl, pHeader, pMimeType);
	}

	public void load(String pUrl, String pHeader, String pMimeType) {
		System.out.println("url :" + pUrl);
		if (StringTool.isNull(pMimeType))
			webEngine.load(pUrl);
		else {
			// webEngine.loadContent(pUrl, "application/pdf");
			// webEngine.loadContent("<a href=\"" + pUrl + "\">" + pBaslik +
			// "</a>");
			webEngine.loadContent(pUrl, pMimeType);
		}
	}

	public void loadContent(final String pContent, final String pMimeType) {
		if (!StringTool.isNull(pMimeType))
			webEngine.loadContent(pContent, pMimeType);
		else webEngine.loadContent(pContent, "text/html");
	}

	public void refresh() {
		webEngine.reload();
	}

	public String convertStreamToString(java.io.InputStream is) {
		if (is != null) {
			java.util.Scanner s = new java.util.Scanner(is);
			s.useDelimiter("\\A");
			String dSnc = s.hasNext() ? s.next() : "";
			s.close();
			return dSnc;
		} else return "";
	}

	public Node createSpacer() {
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		return spacer;
	}

	@Override
	protected void layoutChildren() {
		double w = getWidth();
		double h = getHeight();
		layoutInArea(browser, 0, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
	}

	@Override
	protected double computePrefWidth(double height) {
		return 750;
	}

	@Override
	protected double computePrefHeight(double width) {
		return 500;
	}
}