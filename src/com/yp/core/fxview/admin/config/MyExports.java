package com.yp.core.fxview.admin.config;

import com.yp.admin.data.Export;
import com.yp.core.db.OnExportListener.PHASE;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MyExports extends Export {

	private static final long serialVersionUID = -8489243477921541495L;

	public MyExports() {
		super();
	}

	public MyExports(Export pDe) {
		load(pDe);
	}

	private transient SimpleDoubleProperty progresProperty = new SimpleDoubleProperty(0.0);

	public SimpleDoubleProperty progresProperty() {
		return progresProperty;
	}

	private transient StringProperty groupCodeProperty;

	public StringProperty groupCodeProperty() {
		if (groupCodeProperty == null)
			groupCodeProperty = new SimpleStringProperty(getGroupCode()) {
				@Override
				public void set(String pValue) {
					super.set(pValue);
					setGroupCode(pValue);
				}
			};
		return groupCodeProperty;
	}

	private transient StringProperty queryProperty;

	public StringProperty queryProperty() {
		if (queryProperty == null)
			queryProperty = new SimpleStringProperty(getQuery()) {
				@Override
				public void set(String pValue) {
					super.set(pValue);
					setQuery(pValue);
				}
			};
		return queryProperty;
	}

	private transient StringProperty targetTableProperty;

	public StringProperty targetTableProperty() {
		if (targetTableProperty == null)
			targetTableProperty = new SimpleStringProperty(getTargetTable()) {
				@Override
				public void set(String pValue) {
					super.set(pValue);
					setTargetTable(pValue);
				}
			};
		return targetTableProperty;
	}

	private transient IntegerProperty idxProperty;

	public IntegerProperty idxProperty() {
		if (idxProperty == null)
			idxProperty = new SimpleIntegerProperty(getIdx()) {
				@Override
				public void set(int pValue) {
					super.set(pValue);
					setIdx(pValue);
				}
			};
		return idxProperty;
	}

	private transient BooleanProperty deleteTargetTableRowsProperty;

	public BooleanProperty deleteTargetTableRowsProperty() {
		if (deleteTargetTableRowsProperty == null)
			deleteTargetTableRowsProperty = new SimpleBooleanProperty(isDeleteTargetTableRows()) {
				@Override
				public void set(boolean pValue) {
					super.set(pValue);
					setDeleteTargetTableRows(pValue);
				}
			};
		return deleteTargetTableRowsProperty;
	}

	@Override
	public void onProceed(PHASE phase, Double progress, int count, String message) {
		if (phase != PHASE.ENDS_ALL && phase != PHASE.FAILS_ALL) {
			super.onProceed(phase, progress, count, message);
			if (phase == PHASE.FAILS)
				progresProperty().set(0.0);
			else
				progresProperty().set(progress);
		}
	}

}
