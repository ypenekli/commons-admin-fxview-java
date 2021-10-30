package com.yp.core.fxview.admin.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.yp.admin.data.Export;
import com.yp.admin.model.ExportModel;
import com.yp.core.BaseConstants;
import com.yp.core.db.DbConnInfo;
import com.yp.core.db.IExport;
import com.yp.core.db.OnExportListener;
import com.yp.core.db.OnExportListener.PHASE;
import com.yp.core.entity.IResult;
import com.yp.core.tools.DateTime;

import javafx.concurrent.Service;
import javafx.concurrent.Task;


public class DbExport {

	private ConcurrentHashMap<String, Service<IResult<IExport>>> exports;
	private int taskCount;

	public DbExport(int pTaskCount) {
		super();
		taskCount = pTaskCount;
		exports = new ConcurrentHashMap<>(new HashMap<>(taskCount));
	}

	private static final String FORMATED_EXPORT_MESSAGE1 = "TABLO TOPLAMI/TAMAMLANAN :%s/%s, %s ";

	public void export(DbConnInfo pTarget, Export pVs, OnExportListener proceedListener, int maxConn) {
		if (pVs != null) {
			Service<IResult<IExport>> aktar = new Service<IResult<IExport>>() {
				@Override
				protected Task<IResult<IExport>> createTask() {
					return new Task<IResult<IExport>>() {
						@Override
						protected IResult<IExport> call() throws Exception {
							return new ExportModel().exportDb(pTarget, pVs, proceedListener);
						}
					};
				}

				@Override
				protected void failed() {
					super.failed();
					pVs.setMessages(getMessage());
					exports.remove(pVs.getExportId());
					int remaining = exports.size();
					proceedListener.onProceed(PHASE.FAILS_ALL, (double) remaining, taskCount,
							String.format(FORMATED_EXPORT_MESSAGE1, taskCount, taskCount - remaining, getMessage()));
					wakeThread();
				}

				@Override
				protected void succeeded() {
					super.succeeded();
					IResult<IExport> res = this.getValue();
					pVs.setMessages(res.getMessage());
					pVs.setEndDatetimeDb(DateTime.dbNow());
					exports.remove(pVs.getExportId());
					int remaining = exports.size();
					proceedListener.onProceed(PHASE.ENDS_ALL, (double) remaining, taskCount,
							String.format(FORMATED_EXPORT_MESSAGE1, taskCount, taskCount - remaining, getMessage()));
					wakeThread();
				}
			};
			processThread(pVs, aktar, maxConn);
		}
	}

	private void wakeThread() {
		for (Iterator<Service<IResult<IExport>>> iterator = exports.values().iterator(); iterator.hasNext();) {
			Service<IResult<IExport>> s = iterator.next();
			if (!s.isRunning()) {
				s.start();
				break;
			}
		}
	}

	private synchronized void processThread(Export pVs, Service<IResult<IExport>> aktar, int maxConn) {
		exports.put(pVs.getExportId(), aktar);
		if (exports.size() < maxConn)
			aktar.start();
	}

	public void cancelExport(List<Export> list) {
		if (!BaseConstants.isEmpty(list)) {
			for (Export vs : list) {
				Service<IResult<IExport>> s = exports.get(vs.getExportId());
				if (s != null && s.isRunning())
					s.cancel();
			}
		}

	}
}
