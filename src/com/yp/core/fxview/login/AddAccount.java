package com.yp.core.fxview.login;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.yp.admin.data.Common;
import com.yp.admin.data.User;
import com.yp.admin.model.CommonModel;
import com.yp.core.BaseConstants;
import com.yp.core.entity.IResult;
import com.yp.core.fxview.AForm;
import com.yp.core.ref.IReference;
import com.yp.core.tools.StringTool;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class AddAccount extends AForm {

	private Account account;

	@FXML
	private GridPane ben;
	@FXML
	private TextField txtName, txtSurname, txtEMail, txtMobilePhoneNu;
	@FXML
	private PasswordField txtPassword1, txtPassword2;
	@FXML
	private DatePicker txtBirthDate;
	@FXML
	private Label lbMessage, lbMessage1;
	@FXML
	private Button btnBackward, btnForward, bClose;
	@FXML
	private ComboBox<IReference<Integer>> cbHomeCity;
	@FXML
	private TextArea txtAddress;
	@FXML
	private TabPane page;
	@FXML
	private Tab page1, page2, page3, page4;

	private Tab[] pages;

	private int dIndeks = 0;

	static final String USER_NAME = "USER.NAME";

	@Override
	public void initialize(URL pLocation, ResourceBundle pResources) {

		pages = new Tab[] { page1, page2, page3, page4 };
		IResult<List<Common>> res = new CommonModel().findByParent(Common.PARENT_ID_CITY_TR, null);
		List<Common> list = res.getData();
		if (list != null)
			cbHomeCity.setItems(FXCollections.observableArrayList(list));
		page.getSelectionModel().clearAndSelect(dIndeks);

		String email = BaseConstants.getConfig(USER_NAME);
		if (!StringTool.isNull(email)) {
			getAccount().emailProperty().set(email);
		}
	}

	public void ileri(ActionEvent arg0) {
		if (dIndeks < 3 && sonKontrol(dIndeks)) {
			pages[dIndeks].setDisable(true);
			dIndeks += 1;
			page.getSelectionModel().clearAndSelect(dIndeks);
			pages[dIndeks].setDisable(false);
		}
	}

	public void geri(ActionEvent arg0) {
		if (dIndeks > 0) {
			pages[dIndeks].setDisable(true);
			dIndeks -= 1;
			page.getSelectionModel().clearAndSelect(dIndeks);
			pages[dIndeks].setDisable(false);
		}
	}

	private static final String HELP_URL = "/Account.html";

	@Override
	public String getHelpFileName() {
		return HELP_URL;
	}

	public Account getAccount() {
		if (account == null)
			account = new Account();
		return account;
	}

	public void setAccount(Account pAccount) {
		account = pAccount;
	}

	private boolean sonKontrol(Integer pIndeks) {
		boolean dSnc = true;
		switch (pIndeks) {
		case 0:
			String email = getAccount().emailProperty().get();
			if (StringTool.isNull(email))
				dSnc = false;
			else {
				User user = getUserModel().findByEMail(email);
				if (user != null) {
					account = new Account(user);
				}
			}
			break;

		case 1:
			if (StringTool.isNull(getAccount().getName()) || StringTool.isNull(account.getSurname())
					|| account.getBirthDate() == null || StringTool.isNull(account.getPassword())
					|| account.isPasswordsDiffer())
				dSnc = false;
			break;

		default:
			break;
		}

		return dSnc;
	}

	@Override
	public void save(ActionEvent arg0) {
		result = getUserModel().addAccount(app.getApplicationName(), getAccount(), getUser());
		message = result.getMessage();
		if (result.isSuccess()) {
			User user = (User) result.getData();
			app.setUser(user);
			app.showStartup();
		}

		addMessage(BaseConstants.getString("AddAccount.Sonuc"), message);

		getAlert().showAndWait();
	}

	@Override
	public void synchronize(boolean pMod, Object[] pEkParamDizi) {
		if (pMod) {
			txtEMail.textProperty().bindBidirectional(getAccount().emailProperty());
			txtPassword1.textProperty().bindBidirectional(getAccount().password1Property());
			txtPassword2.textProperty().bindBidirectional(getAccount().password2Property());
			txtName.textProperty().bindBidirectional(getAccount().nameProperty());
			txtSurname.textProperty().bindBidirectional(getAccount().surnameProperty());
			txtMobilePhoneNu.textProperty().bindBidirectional(getAccount().phoneProperty());
			txtBirthDate.valueProperty().bindBidirectional(getAccount().birthdayProperty());
			cbHomeCity.valueProperty().bindBidirectional(getAccount().homeCityProperty());
			txtAddress.textProperty().bindBidirectional(getAccount().addressProperty());
		} else {
			txtEMail.textProperty().unbindBidirectional(getAccount().emailProperty());
			txtPassword1.textProperty().unbindBidirectional(getAccount().password1Property());
			txtPassword2.textProperty().unbindBidirectional(getAccount().password2Property());
			txtName.textProperty().unbindBidirectional(getAccount().nameProperty());
			txtSurname.textProperty().unbindBidirectional(getAccount().surnameProperty());
			txtMobilePhoneNu.textProperty().unbindBidirectional(getAccount().phoneProperty());
			txtBirthDate.valueProperty().unbindBidirectional(getAccount().birthdayProperty());
			cbHomeCity.valueProperty().unbindBidirectional(getAccount().homeCityProperty());
			txtAddress.textProperty().unbindBidirectional(getAccount().addressProperty());
		}

	}
}
