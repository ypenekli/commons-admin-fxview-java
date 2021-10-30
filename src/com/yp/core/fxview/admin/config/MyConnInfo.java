package com.yp.core.fxview.admin.config;

import java.util.ArrayList;
import java.util.List;

import com.yp.core.BaseConstants;
import com.yp.core.db.DbConnInfo;
import com.yp.core.db.DbHandler;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MyConnInfo extends DbConnInfo {

	public MyConnInfo(String pKey, String pValue) {
		super(pKey, pValue);
	}

	public MyConnInfo(DbConnInfo pConnConf) {
		super(pConnConf.getKey(), pConnConf.getValue());
		dbDriver = pConnConf.getDbDriver();
		dbPassword = pConnConf.getDbPassword();
		dbSeperator = pConnConf.getDbSeperator();
		dbUrl = pConnConf.getDbUrl();
		dbUser = pConnConf.getDbUser();
		defaultDb = pConnConf.isDefaultDb();
	}

	private StringProperty dbDriver1;

	public StringProperty dbDriverProperty() {
		if (dbDriver1 == null)
			dbDriver1 = new SimpleStringProperty(dbDriver) {
				@Override
				public void set(String pValue) {
					dbDriver = pValue;
					super.set(pValue);
				}
			};
		return dbDriver1;
	}

	private StringProperty dbPassword1;

	public StringProperty dbPasswordProperty() {
		if (dbPassword1 == null)
			dbPassword1 = new SimpleStringProperty(dbPassword) {
				@Override
				public void set(String pValue) {
					dbPassword = DbHandler.encrypt(pValue);
					super.set(dbPassword);
				}

				@Override
				public String get() {
					return DbHandler.decrypt(super.get());
				}
			};
		return dbPassword1;
	}

	private StringProperty dbSeperator1;

	public StringProperty dbSeperatorProperty() {
		if (dbSeperator1 == null)
			dbSeperator1 = new SimpleStringProperty(dbSeperator) {
				@Override
				public void set(String pValue) {
					dbSeperator = pValue;
					super.set(pValue);
				}
			};
		return dbSeperator1;
	}

	private StringProperty dbUrl1;

	public StringProperty dbUrlProperty() {
		if (dbUrl1 == null)
			dbUrl1 = new SimpleStringProperty(dbUrl) {
				@Override
				public void set(String pValue) {
					dbUrl = pValue;
					super.set(pValue);
				}
			};
		return dbUrl1;
	}

	private StringProperty dbUser1;

	public StringProperty dbUserProperty() {
		if (dbUser1 == null)
			dbUser1 = new SimpleStringProperty(dbUser) {
				@Override
				public void set(String pValue) {
					dbUser = pValue;
					super.set(pValue);
				}
			};
		return dbUser1;
	}

	private BooleanProperty defaultDb1;

	public BooleanProperty defaultDbProperty() {
		if (defaultDb1 == null)
			defaultDb1 = new SimpleBooleanProperty(defaultDb) {
				@Override
				public void set(boolean pValue) {
					defaultDb = pValue;
					super.set(pValue);
				}
			};
		return defaultDb1;
	}

	@Override
	public boolean equals(final Object obj) {
		return super.equals(obj);
	}

	public static List<MyConnInfo> loadList(List<DbConnInfo> pList) {
		List<MyConnInfo> list = new ArrayList<>();
		if (!BaseConstants.isEmpty(pList)) {
			pList.forEach(e -> list.add(new MyConnInfo(e)));
		}
		return list;
	}
}
