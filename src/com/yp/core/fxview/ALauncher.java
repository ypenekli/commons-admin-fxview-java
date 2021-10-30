package com.yp.core.fxview;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.yp.admin.data.Group;
import com.yp.admin.data.AppFunc;
import com.yp.core.BaseConstants;
import com.yp.core.entity.IDataEntity;
import com.yp.core.entity.IResult;
import com.yp.core.entity.Result;
import com.yp.core.log.MyLogger;
import com.yp.core.tools.DateTime;
import com.yp.core.user.IUser;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public abstract class ALauncher extends Application {

	protected static final String BACKUPFILENAME = ".backup";
	protected static final String DBFILENAME = "data1";
	protected static final String DBDIR = "db";
	protected static final String BACKUPDIR = "backup";
	protected static boolean isCreateDb;
	protected static String updateUrl;

	protected static final String CREATEDB = "createdb";
	protected static final String URL = "--url=";
	//protected static final String PATH = "--path=";
	protected static final String PATH = "path";

	public MyLogger logger;
	protected Stage primaryStage;
	private Home home;
	public Stage WEB;
	// public HostServices HostService;

	protected IUser user;
	protected List<Group> rootMenuList;
	protected List<AppFunc> menuList;
	protected List<IDataEntity> dataList;

	protected Map<String, Pane> Forms;

	@Override
	public void init() throws Exception {
		logger = new MyLogger(BaseConstants.getRootAddress());
		Forms = new HashMap<>();
		Parameters params = getParameters();
		if (params != null) {
			params.getNamed().forEach((k, v) -> System.out.println(k + ": " + v));
			params.getUnnamed().forEach(System.out::println);

			if (params.getNamed().containsKey(PATH))
				BaseConstants.setRootAddress(params.getNamed().get(PATH));
			isCreateDb = params.getNamed().containsKey(CREATEDB);
		}

		createDb();
		restore();
		checkApplicationRelease();
		Logger.getLogger(MyLogger.NAME).log(Level.INFO, "Application is starting");
		super.init();
	}

	@Override
	public void start(Stage pPrimaryStage) throws Exception {
		primaryStage = pPrimaryStage;

		AForm.app = this;

		try {
			// HostService = this.getHostServices();

			final FXMLLoader fxmlLoader = new FXMLLoader(Home.class.getResource("FrmHome.fxml"),
					BaseConstants.BUNDLE_MESSAGE);
			final Pane root = fxmlLoader.load();
			final Home fas = fxmlLoader.getController();
			setHome(fas);
			final Scene scene = new Scene((Parent) root, 850.0, 650.0);
			scene.setFill((Paint) Color.OLDLACE);
			setUserAgentStylesheet("CASPIAN");
			primaryStage.setScene(scene);
			primaryStage.setTitle(getApplicationName());
			// primaryStage.getIcons().add(new
			// Image(this.getClass().getResourceAsStream("/com/yp/ppti/imres/ppti.png")));
			setMenuList(null);
			fas.createMenu(null);
			primaryStage.show();
			primaryStage.centerOnScreen();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}

	}

	public void showStartup() {
		home.show("0", ".Empty", null, null, getBundle());
	}
	
	protected void createDb() {
		logger.log(Level.INFO, "Create database..");
		System.setProperty("derby.system.home", BaseConstants.getRootAddress());
	}

	public void restore() {
		logger.log(Level.INFO, "Restore database..");
		String source = null;
		File fKaynak = null;
		String path = BaseConstants.getRootAddress();
		File file = new File(Paths.get(path, DBDIR, BACKUPFILENAME).toUri());
		if (file.exists() && file.canRead()) {
			fKaynak = file;
			java.util.List<String> list = null;
			try {
				list = Files.readAllLines(file.toPath());
			} catch (IOException h) {
				logger.log(Level.SEVERE, h.getMessage(), h);
			}
			if (!BaseConstants.isEmpty(list)) {
				source = list.get(0);
			}
		}

		if (source != null) {
			backup(DBFILENAME, null);
			File target = new File(Paths.get(path, DBDIR, DBFILENAME).toUri());
			File sourceFile = new File(source);
			try {
				if (target.exists()) {
					FileUtils.deleteDirectory(target);
				}
				FileUtils.copyDirectory(sourceFile, target);
				FileUtils.deleteQuietly(fKaynak);
			} catch (IOException h) {
				logger.log(Level.SEVERE, h.getMessage(), h);
			}

		}

	}

	public IResult<String> backup(String pDbName, String pBackupFileName) {
		logger.log(Level.INFO, "Backup database..");
		IResult<String> dSnc = new Result<>(true, "");

		if (pDbName == null)
			pDbName = DBFILENAME;
		if (pBackupFileName == null)
			pBackupFileName = pDbName + "-" + DateTime.dbNow().toString();
		String path = BaseConstants.getRootAddress();

		try {
			Path target = Paths.get(path, BACKUPDIR, pBackupFileName);
			Path source = Paths.get(path, DBDIR, pDbName);

			Files.deleteIfExists(target);
			FileUtils.copyDirectory(source.toFile(), target.toFile());

		} catch (IOException h) {
			dSnc.setSuccess(false);
			dSnc.setMessage(BaseConstants.getString("FrmAyar.Yedekleme.Basarisiz"));
			logger.log(Level.SEVERE, h.getMessage(), h);
		}
		if (dSnc.isSuccess())
			dSnc.setMessage(BaseConstants.getString("FrmAyar.Yedekleme.Basarili") + pBackupFileName);

		return dSnc;
	}

	public boolean checkApplicationConfig(Group pRoot) {
		logger.log(Level.INFO, "Check application config..");
		return true;
	}

	protected void checkApplicationRelease() {
		logger.log(Level.INFO, "Check application release..");
	}

	protected abstract List<Group> findRootMenuList(IUser pUser);

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void setPrimaryStage(Stage pPrimaryStage) {
		primaryStage = pPrimaryStage;
	}

	public Home getHome() {
		return home;
	}

	public void setHome(Home pHome) {
		home = pHome;
	}

	public IUser getUser() {
		return user;
	}

	public void setUser(IUser pUser) {
		user = pUser;
		rootMenuList = findRootMenuList(pUser);
	}

	public Pane getForm(String pFormId) {
		return Forms.get(pFormId);
	}

	public Pane loadForm(String pFormId, Pane pForm) {
		return Forms.put(pFormId, pForm);
	}

	public void closeAllForms() {
		Forms.values().stream().forEach((v) -> {
			if (v.getScene() != null && v.getScene().getWindow() != null && v.getScene().getWindow().isShowing()) {
				v.getScene().getWindow().hide();
			}
		});
	}

	public void killAllForms() {
		closeAllForms();
		Forms.clear();
	}

	public void exit() {
		closeAllForms();
		Platform.exit();
		System.exit(0);
	}

	public abstract ResourceBundle getBundle();

	public abstract String getStringConstant(String key);

	public abstract String getFormUrl(String key);

	public String getApplicationId() {
		return getStringConstant("Application.Id");
	}

	public String getApplicationName() {
		return getStringConstant("Application.Name");
	}

	public String getApplicationDirName() {
		return getStringConstant("Application.Dir.Name");
	}

	public String getApplicationWebUrl() {
		return getStringConstant("Application.Web.Url");
	}

	public String getRelease() {
		return getStringConstant("Release.Definition");
	}

	public String getHelpFileName() {
		return getStringConstant("Application.Help.File.Name");
	}

	public String getHelpUrl() {
		return getStringConstant("Application.Help.Url");
	}

	public String getHomeFileName() {
		return getStringConstant("Application.Home.File.Name");
	}

	public List<AppFunc> getMenuList() {
		return menuList;
	}

	public List<Group> getRootMenuList() {
		return rootMenuList;
	}

	public void setMenuList(final List<AppFunc> pMenuList) {
		menuList = pMenuList;
		Pane dP = (Pane) getPrimaryStage().getScene().getRoot();
		dP.getChildren().clear();
		if (pMenuList != null && !pMenuList.isEmpty())
			home.createMenu(menuList);
	}

	public static void main(String[] args) {
		launch(args);
	}

}
