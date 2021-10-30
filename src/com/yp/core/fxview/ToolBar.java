package com.yp.core.fxview;

import com.yp.core.BaseConstants;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ToolBar {
	private AForm form;
	
	@FXML
	private Button btnHide;

	private BooleanProperty hideClose;
	private BooleanProperty hideRefresh;
	private BooleanProperty hideAdd;
	private BooleanProperty hideSave;
	private BooleanProperty hideReport;
	private StringProperty labelSave;

	public ToolBar() {
		this.hideClose = new SimpleBooleanProperty();
		this.hideRefresh = new SimpleBooleanProperty();
		this.hideAdd = new SimpleBooleanProperty();
		this.hideSave = new SimpleBooleanProperty();
		this.hideReport = new SimpleBooleanProperty();
		this.labelSave = new SimpleStringProperty(BaseConstants.getString("Save"));
		
		btnHide.setOnAction(this::hide);
	}

	public void setRoot(AForm pForm) {
		form = pForm;
	}

	public BooleanProperty hideCloseProperty() {
		return hideClose;
	}

	public BooleanProperty hideRefreshProperty() {
		return hideRefresh;
	}

	public BooleanProperty hideAddProperty() {
		return hideAdd;
	}

	public BooleanProperty hideSaveProperty() {
		return hideSave;
	}

	public BooleanProperty hideReportProperty() {
		return hideReport;
	}

	public Boolean getHideClose() {
		return hideClose.getValue();
	}

	public void setHideClose(Boolean pGosterMe) {
		hideClose.setValue(pGosterMe);
	}

	public Boolean getHideRefresh() {
		return hideRefresh.getValue();
	}

	public void setHideRefresh(Boolean pGosterMe) {
		hideRefresh.setValue(pGosterMe);
	}

	public Boolean getHideAdd() {
		return hideAdd.getValue();
	}

	public void setHideAdd(Boolean pGosterMe) {
		hideAdd.setValue(pGosterMe);
	}

	public Boolean getHideSave() {
		return hideSave.getValue();
	}

	public void setHideSave(Boolean pGosterMe) {
		hideSave.setValue(pGosterMe);
	}

	public Boolean getHideReport() {
		return hideReport.getValue();
	}

	public void setHideReport(Boolean pHide) {
		hideReport.setValue(pHide);
	}

	public StringProperty labelSaveProperty() {
		return labelSave;
	}

	public String getLabelSave() {
		return labelSave.getValue();
	}

	public void setLabelSave(String pEtiket) {
		labelSave.setValue(pEtiket);
	}

	@FXML
	public void close(ActionEvent arg0) {
		if (form != null)
			form.close(arg0);
	}

	public void hide(final ActionEvent arg0) {
		if (form != null)
			form.hide(arg0);
	}

	@FXML
	public void cancel(ActionEvent arg0) {
		if (form != null)
			form.cancel(arg0);
	}

	@FXML
	public void refresh(ActionEvent arg0) {
		if (form != null)
			form.refresh(arg0);
	}

	@FXML
	public void add(ActionEvent arg0) {
		if (form != null)
			form.add(arg0);
	}

	@FXML
	public void save(ActionEvent arg0) {
		if (form != null)
			form.save(arg0);
	}

	@FXML
	public void report(ActionEvent arg0) {
		if (form != null)
			form.report(arg0);
	}
	
	public void setOnReport(ActionEvent arg0) {
		
	}

}
