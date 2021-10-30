package com.yp.core.fxview;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import com.yp.admin.data.AppFunc;
import com.yp.admin.data.App;
import com.yp.core.BaseConstants;
import com.yp.core.entity.IDataEntity;
import com.yp.core.tools.StringTool;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class Home extends AForm {
	@FXML
	public BorderPane root;
	@FXML
	private Pane menu;
	private MenuBar menuBar;
	@FXML
	private HBox statusBar;
	@FXML
	public Pane content;
	@FXML
	public ProgressBar progressBar;
	@FXML
	Label statusLabel1;
	@FXML
	Label statusLabel2;

	private static String localConfig;

	static {
		Home.localConfig = Locale.getDefault().getLanguage();
	}

	public void createMenu(final List<AppFunc> pMenuList) {
		(this.menuBar = new MenuBar()).setId("Home");
		Menu mProje = new Menu(BaseConstants.getString("FrmHome.1"));
		this.menuBar.getMenus().add(mProje);
		final MenuItem mProje2 = new MenuItem(BaseConstants.getString("Login"));
		mProje2.setDisable(app.getUser() != null);
		mProje2.setOnAction(
				event -> show("0", ".Login", BaseConstants.getString("Login"), (IDataEntity) null, app.getBundle()));
		mProje.getItems().add(mProje2);
		mProje.getItems().add(new SeparatorMenuItem());
		final MenuItem mProje3 = new MenuItem(BaseConstants.getString("ChangePassword"));
		mProje3.setDisable(app.getUser() == null);
		mProje3.setOnAction(event -> show("1", ".ChangePassword", BaseConstants.getString("ChangePassword"),
				app.getUser(), app.getBundle()));
		mProje.getItems().add(mProje3);
		mProje.getItems().add(new SeparatorMenuItem());
		final MenuItem mProje4 = new MenuItem(BaseConstants.getString("FrmHome.8"));
		mProje4.setOnAction(event -> app.exit());
		mProje.getItems().add(mProje4);
		if (pMenuList != null) {
			for (int dI = 0; dI < pMenuList.size(); ++dI) {
				final AppFunc de = pMenuList.get(dI);
				if (de.isStatusEnabled() && de.getLevel() == 2) {
					mProje = new Menu(de.getName());
					menuBar.getMenus().add(mProje);
					fillSubMenu(pMenuList, mProje, de.getId(), dI);
				}
			}
		}
		final Menu mProje5 = new Menu(BaseConstants.getString("FrmHome.2"));
		mProje5.setAccelerator(KeyCombination.keyCombination("F1"));
		this.menuBar.getMenus().add(mProje5);
		final MenuItem mProje6 = new MenuItem(BaseConstants.getString("FrmHome.2.Config"));
		mProje6.setOnAction(event -> show("2", ".Config", BaseConstants.getString("FrmHome.2.Config"),
				(IDataEntity) null, (ResourceBundle) null));
		mProje5.getItems().add(mProje6);
		mProje5.getItems().add(new SeparatorMenuItem());
		final MenuItem mProje7 = new MenuItem(BaseConstants.getString("FrmHome.2.Help"));
		mProje7.setOnAction(event -> {
			final String url = app.getHelpUrl() + "/yardim.html";
			dataEntity = new App();
			dataEntity.set("url", url);
			show(Home.this.id, ".Empty", (String) null, dataEntity, (ResourceBundle) null);
		});
		mProje5.getItems().add(mProje7);
		mProje5.getItems().add(new SeparatorMenuItem());
		final MenuItem mProje8 = new MenuItem(BaseConstants.getString("FrmHome.2.About"));
		mProje8.setOnAction(event -> show("4", ".About", BaseConstants.getString("FrmHome.2.About"), (IDataEntity) null,
				(ResourceBundle) null));
		mProje5.getItems().add(mProje8);
		if (app.getUser() != null) {
			setUserLabel(app.getUser().getEmail() + ", " + app.getUser().getFullName());
		}
		this.statusBar.setAlignment(Pos.BOTTOM_LEFT);
		this.root.setTop((Node) this.menuBar);
		this.root.setBottom((Node) this.statusBar);
	}

	private void fillSubMenu(final List<AppFunc> pListe, final Menu pMenu, final String pUstkod,
			final int pAltSinir) {
		for (int dI = pAltSinir; dI < pListe.size(); ++dI) {
			final AppFunc de = pListe.get(dI);
			if (de.isStatusEnabled() && pUstkod.equals(de.getParentId())) {
				MenuItem mProje;
				if (!de.isLeaf()) {
					mProje = new Menu(de.getName());
					this.fillSubMenu(pListe, (Menu) mProje, de.getId(), dI);
				} else {
					mProje = new MenuItem(de.getName());
					mProje.setOnAction(event -> show(de.getId(), de.getUrl(), de.getId(), (IDataEntity) null,
							(ResourceBundle) null));
				}
				if (!BaseConstants.isEmpty(pMenu.getItems())) {
					pMenu.getItems().add(new SeparatorMenuItem());
				}
				pMenu.getItems().add(mProje);
			}
		}
	}

	public static void setLocalConfig(final String pFormat) {
		if (!StringTool.isNull(pFormat)) {
			Home.localConfig = pFormat;
			final String[] splits = Home.localConfig.split("_");
			formatCurrency = NumberFormat.getCurrencyInstance(new Locale(splits[0], splits[1]));
		}
	}

	@Override
	public byte[] getImage(final String fileName) {
		byte[] b = null;
		try (InputStream is = new BufferedInputStream(Home.class.getClassLoader().getResourceAsStream(fileName));) {
			b = new byte[is.available()];
			is.read(b);
		} catch (IOException e) {
			app.logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return b;
	}

	public void initialize(final URL pLocation, final ResourceBundle pResources) {
		app.logger.log(Level.INFO, "url : " + pLocation);
		this.root.setBottom((Node) this.statusBar);
	}

	public void synchronize(final boolean pToForm, final Object[] pAdditionalParams) {
	}

	public String getHelpFileName() {
		return app.getHelpFileName();
	}

	public void setStatusLabel(final String pLabel) {
		this.statusLabel1.setText(pLabel);
	}

	public void setUserLabel(final String pLabel) {
		this.statusLabel2.setText(pLabel);
	}

	public void setDisableMainMenu(final boolean pDisable) {
		if (this.menuBar != null) {
			this.menuBar.setDisable(pDisable);
		}
	}
}