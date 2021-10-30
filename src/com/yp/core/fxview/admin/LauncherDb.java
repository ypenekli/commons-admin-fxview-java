package com.yp.core.fxview.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.yp.admin.data.Export;
import com.yp.admin.data.Group;
import com.yp.admin.model.ExportModel;
import com.yp.core.BaseConstants;
import com.yp.core.db.DbConnInfo;
import com.yp.core.db.OnExportListener.PHASE;
import com.yp.core.fxview.ALauncher;
import com.yp.core.fxview.admin.config.DbExport;
import com.yp.core.tools.StringTool;
import com.yp.core.user.IUser;

import javafx.application.Platform;
import javafx.stage.Stage;

public class LauncherDb extends ALauncher {
	private static final String SOURCE;
	private static final String TARGET;
	private static final String POOL_SIZE;
	
	static {
		SOURCE = "s";
		TARGET = "t";
		POOL_SIZE = "max";
	}	

	public static void main(String[] args) {
		launch(args);
	}

	private static void updateProgress(PHASE phase, double progress, int taskCount, String message) {
		System.out.println(message);
		if ((phase == PHASE.ENDS_ALL || phase == PHASE.FAILS_ALL) && progress == 0.0)
			Platform.runLater(() -> {
				Platform.exit();
				System.exit(0);
			});
	}

	@Override
	public void start(Stage primaryStage) {
		Map<String, String> params = getParameters().getNamed();
		String sourceSchema = params.get(SOURCE);
		String targetSchema = params.get(TARGET);
		String poolSize = params.get(POOL_SIZE);
		int maxPoolSize = 5;
		if (!StringTool.isNull(poolSize))
			maxPoolSize = Integer.valueOf(poolSize);

		if (!StringTool.isNull(sourceSchema) && !StringTool.isNull(targetSchema)) {
			ExportModel model = new ExportModel();
			DbConnInfo target = model.getRemoteDb();
			
			System.out.println("Source connection :" + model.getDefaultDb().getKey());
			System.out.println("Target connection :" + target.getKey());

			System.out.println("Source Schema  :" + sourceSchema);
			System.out.println("Target Schema  :" + targetSchema);
			System.out.println("Max connection :" + maxPoolSize);
			
			String[] sourceSchemaList = sourceSchema.split(BaseConstants.COMMA);
			String[] targetSchemaList = targetSchema.split(BaseConstants.COMMA);
			List<Export> taskList = new ArrayList<>();
			for (int dI = 0; dI < targetSchemaList.length; dI++) {
				taskList.addAll(model.getExportList(sourceSchemaList[dI], targetSchemaList[dI]));
			}
			if (!BaseConstants.isEmpty(taskList)) {
				int taskCount = taskList.size();
				DbExport dbexport = new DbExport(taskCount);
				for (Export vs : taskList) {
					vs.setDeleteTargetTableRows(true);
					dbexport.export(target, vs, LauncherDb::updateProgress, maxPoolSize);
				}
			}
		}
	}

	@Override
	protected List<Group> findRootMenuList(IUser pUser) {
		return null;
	}

	@Override
	public ResourceBundle getBundle() {
		return null;
	}

	@Override
	public String getStringConstant(String key) {
		return null;
	}

	@Override
	public String getFormUrl(String key) {
		return null;
	}
}
