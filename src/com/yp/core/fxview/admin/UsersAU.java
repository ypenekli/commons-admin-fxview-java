package com.yp.core.fxview.admin;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.yp.admin.data.Common;
import com.yp.admin.data.User;
import com.yp.core.BaseConstants;
import com.yp.core.entity.IResult;
import com.yp.core.tools.DateTime;
import com.yp.core.tools.StringTool;
import com.yp.core.user.IUser;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class UsersAU extends RootPage {
	@FXML
	private TextField txtCitizenshipNu;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtSurname;
	@FXML
	private DatePicker txtBirthdate;
	@FXML
	private ComboBox<Common> cbTitle;
	@FXML
	private ComboBox<Common> cbProfession;
	@FXML
	private ComboBox<Common> cbPosition;
	@FXML
	private DatePicker txtCheckinDate;
	@FXML
	private CheckBox onyDurumu;
	@FXML
	private DatePicker txtCheckoutDate;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtHomeNu;
	@FXML
	private TextField txtMobileNu;
	@FXML
	private TextArea txtAddress;
	@FXML
	private ComboBox<Common> cbHomeCity;
	@FXML
	private Button btnSave;
	@FXML
	private Button btnCancel;
	protected List<Common> positionList;
	protected List<Common> titleList;
	protected List<Common> professionList;

	public void fillRefrences() {
		IResult<List<Common>> res = getCommonModel().findByParent(Common.PARENT_ID_CITY_TR, null);
		cityList = res.getData();
		res = getCommonModel().findByParent(Common.PARENT_ID_POSITION, null);
		positionList = res.getData();		
		res = getCommonModel().findByParent(Common.PARENT_ID_TITLE, null);
		titleList = res.getData();;
		res = getCommonModel().findByParent(Common.PARENT_ID_PROFESSION, null);
		professionList = res.getData();;
	}

	public void initialize(URL location, ResourceBundle resources) {
		fillRefrences();
		if (!BaseConstants.isEmpty(cityList))
			cbHomeCity.setItems(FXCollections.observableArrayList(cityList));
		if (!BaseConstants.isEmpty(positionList))
			cbPosition.setItems(FXCollections.observableArrayList(positionList));
		if (!BaseConstants.isEmpty(professionList))
			cbProfession.setItems(FXCollections.observableArrayList(professionList));
		if (!BaseConstants.isEmpty(titleList))
			cbTitle.setItems(FXCollections.observableArrayList(titleList));
	}

	public String getHelpFileName() {
		return null;
	}

	public void synchronize(boolean pToForm, Object[] pAdditionalParams) {
		User user = (User) dataEntity;
		if (user != null) {
			if (pToForm) {
				txtName.setText(user.getName());
				txtAddress.setText(user.getHomeAddress());
				txtMobileNu.setText(user.getMobilePhoneNu());
				txtBirthdate.setValue(DateTime.asLocalDate(user.getBirthDate()));
				txtEmail.setText(user.getEmail());
				txtHomeNu.setText(user.getPhoneno1());
				txtCheckoutDate.setValue(DateTime.asLocalDate(user.getCheckoutDate()));
				txtCheckinDate.setValue(DateTime.asLocalDate(user.getCheckinDate()));
				txtSurname.setText(user.getSurname());
				txtCitizenshipNu.setText(StringTool.getString(user.getCitizenshipNu(), BigDecimal.ZERO));
				if (!user.isHomeCityNull()) {
					cbHomeCity.getSelectionModel().select((Common) user.getHomeCityRef());
				} else {
					cbHomeCity.getSelectionModel().clearSelection();
				}

				if (!user.isPositionNull()) {
					cbPosition.getSelectionModel().select((Common) user.getPositionRef());
				} else {
					cbPosition.getSelectionModel().clearSelection();
				}

				if (!user.isProfessionNull()) {
					cbProfession.getSelectionModel().select((Common) user.getProfessionRef());
				} else {
					cbProfession.getSelectionModel().clearSelection();
				}

				if (!user.isTitleNull()) {
					cbTitle.getSelectionModel().select((Common) user.getTitleRef());
				} else {
					cbTitle.getSelectionModel().clearSelection();
				}

				this.onyDurumu.setSelected(!user.isStatusActive());
			} else {
				user.setName(this.txtName.getText());
				user.setHomeAddress(txtAddress.getText());
				user.setPhoneno2(txtMobileNu.getText());
				user.setBirthDate(DateTime.asDate(txtBirthdate.getValue()));
				user.setEmail(txtEmail.getText());
				user.setPhoneno1(txtHomeNu.getText());
				user.setCheckoutDate(DateTime.asDate(txtCheckoutDate.getValue()));
				user.setCheckinDate(DateTime.asDate(txtCheckinDate.getValue()));
				user.setSurname(txtSurname.getText());
				user.setCitizenshipNu(StringTool.getBigDecimal(txtCitizenshipNu.getText(), BigDecimal.ZERO));
				user.setStatusActive(onyDurumu.isSelected());
				if (cbHomeCity.getSelectionModel().getSelectedIndex() > -1) {
					user.setHomeCity(cbHomeCity.getValue().getId());
				}

				if (cbPosition.getSelectionModel().getSelectedIndex() > -1) {
					user.setPosition(cbPosition.getValue().getId());
				}

				if (cbProfession.getSelectionModel().getSelectedIndex() > -1) {
					user.setProfession(cbProfession.getValue().getId());
				}

				if (cbTitle.getSelectionModel().getSelectedIndex() > -1) {
					user.setTitle(cbTitle.getValue().getId());
				}
				if (user.isNew()) {
					user.setPassword("11111.0");
				}
			}
		}

	}

	public void save(ActionEvent arg0) {
		synchronize(false, (Object[]) null);
		result = this.getUserModel().save((IUser) dataEntity, getUser());
		if (result.isSuccess()) {
			addMessage(BaseConstants.MESSAGE_INFO, result.getMessage());
		} else {
			addMessage(BaseConstants.MESSAGE_WARNING, result.getMessage());
		}

		this.getAlert().showAndWait();
	}

	public void add(ActionEvent arg0) {
		dataEntity = new User(-1);
		synchronize(true, (Object[]) null);
	}

	@Override
	public void close(ActionEvent arg0) {
		hide(arg0);
	}
}