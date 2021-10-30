package com.yp.core.fxview.web;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class WebViewSample extends Application {
	private Scene scene;

	@Override
	public void start(Stage stage) {
		// create the scene
		stage.setTitle("Web View");
		scene = new Scene(new Browser("http://www.oracle.com/products/index.html", "Goster..", null), 750, 500,
				Color.web("#666970"));
		stage.setScene(scene);
		scene.getStylesheets().add("webviewsample/BrowserToolbar.css");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
