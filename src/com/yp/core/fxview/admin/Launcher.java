package com.yp.core.fxview.admin;

import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yp.admin.Constants;
import com.yp.admin.data.Common;
import com.yp.admin.data.GroupAppFunc;
import com.yp.admin.data.GroupUser;
import com.yp.admin.data.Group;
import com.yp.admin.data.AppFunc;
import com.yp.admin.data.App;
import com.yp.admin.data.User;
import com.yp.admin.model.GroupModel;
import com.yp.admin.model.UserModel;
import com.yp.core.BaseConstants;
import com.yp.core.db.DbHandler;
import com.yp.core.entity.DataEntity;
import com.yp.core.entity.IDataEntity;
import com.yp.core.fxview.ALauncher;
import com.yp.core.log.MyLogger;
import com.yp.core.user.IUser;

public class Launcher extends ALauncher {

	private static final ResourceBundle configBundle;

	static {
		configBundle = ResourceBundle.getBundle("admin.Config");
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	protected List<Group> findRootMenuList(IUser pUser) {
		return new GroupModel().findAppGroupList(pUser.getId(), getApplicationId());
	}

	public String getFormUrl(String key) {
		return configBundle.getString(key);
	}

	@Override
	public ResourceBundle getBundle() {
		return Constants.bundleMessage;
	}

	@Override
	public String getStringConstant(String key) {
		return BaseConstants.getString(key);
	}

	protected final IDataEntity[] commonTables = new DataEntity[] { new App(), new AppFunc(), new Group(),
			new GroupAppFunc(), new Common(), new User(), new GroupUser(), };

	@Override
	public void createDb() {
		System.out.println("derby home :" + BaseConstants.getRootAddress());
		System.setProperty("derby.system.home", BaseConstants.getRootAddress());
		if (isCreateDb) {
			try {
				UserModel model = new UserModel();
				String createSql = "/sql/creates/common.sql";

				String server = BaseConstants.getConfig(BaseConstants.SERVER);
				String dbDriver = BaseConstants.getConfig(server + DbHandler.DB_DRIVER);

				if (dbDriver != null && dbDriver.startsWith("com.mysql.jdbc.Driver")) {
					createSql = "/sql/creates/common_mysql.sql";
				}

				model.executeSQLfromResourceFile(createSql);

				model.exportXlsToDb("/data/common.xls", commonTables);
			} catch (Exception e) {
				Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, null, e);
			}

		}
	}

}
