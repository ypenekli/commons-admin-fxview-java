package com.yp.core.fxview.admin;

import java.io.IOException;
import java.util.List;

import com.yp.Root;
import com.yp.admin.Constants;
import com.yp.admin.data.Common;
import com.yp.admin.model.CommonModel;
import com.yp.admin.model.AppFuncModel;
import com.yp.admin.model.AppModel;
import com.yp.admin.model.ExportModel;
import com.yp.core.fxview.AForm;

import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;

public abstract class RootPage extends AForm {
	protected static final String ADD_NEW;
	protected static final String UPDATE;
	protected static final String VIEW;
	protected static final String LISTING;
	protected static final String REPORT = "rpr.do";
	private CommonModel commonModel;
	private AppModel appModel;
	private AppFuncModel appFuncModel;
	private ExportModel exportModel;
	protected List<Common> cityList;
	protected List<Common> districtList;

	static {
		ADD_NEW = Constants.getString("Addnew");
		UPDATE = Constants.getString("Update");
		VIEW = Constants.getString("View");
		LISTING = Constants.getString("Listing");
	}

	public CommonModel getCommonModel() {
		if (commonModel == null) {
			commonModel = new CommonModel();
		}
		return commonModel;
	}

	public AppModel getAppModel() {
		if (appModel == null) {
			appModel = new AppModel();
		}
		return appModel;
	}

	public AppFuncModel getAppFuncModel() {
		if (appFuncModel == null) {
			appFuncModel = new AppFuncModel();
		}
		return appFuncModel;
	}

	public ExportModel getExportModel() {
		if (exportModel == null) {
			exportModel = new ExportModel();
		}
		return exportModel;
	}

	public List<Common> getCityList() {
		if (cityList == null) {
			cityList = getCommonModel().findByParent(Common.PARENT_ID_CITY_TR);
		}
		return cityList;
	}

	public void showProgress(@SuppressWarnings("rawtypes") final Worker task) {
		app.getHome().progressBar.visibleProperty().bind(task.runningProperty());
		this.self.disableProperty().bind(task.runningProperty());
	}

	public void showProgress(final boolean show) {
		app.getHome().progressBar.setVisible(show);
		app.getHome().progressBar.setProgress(0.0);
		this.self.setDisable(show);
	}

	public void updateProgress(final long taskCount) {
		if (taskCount > 0) {
			Double progres = 1d / taskCount;
			progres += app.getHome().progressBar.getProgress();
			boolean hide = progres > 0.98;
			if (hide)
				progres = 0.0;

			app.getHome().progressBar.setProgress(progres);
			app.getHome().progressBar.setVisible(!hide);
		} else
			app.getHome().progressBar.setProgress(0.0);

	}

	public void bindProgress(Task<?> progres) {
		app.getHome().progressBar.setProgress(0);
		app.getHome().progressBar.setVisible(true);
		app.getHome().progressBar.progressProperty().unbind();
		app.getHome().progressBar.progressProperty().bind(progres.progressProperty());
	}
	
	public class ToolBar{
		public ToolBar() throws IOException {
	        FXMLLoader loader = new FXMLLoader(Root.class.getResource("core/fxview/ToolBar.fxml"));
	        loader.setRoot(this);
	        loader.setController(this);
	        loader.load();
	    }
	}
}