package com.yp.core.fxview.login;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.yp.admin.data.Group;
import com.yp.admin.data.User;
import com.yp.admin.model.AppFuncModel;
import com.yp.core.BaseConstants;
import com.yp.core.entity.IResult;
import com.yp.core.fxview.AForm;
import com.yp.core.log.MyLogger;
import com.yp.core.ref.IReference;
import com.yp.core.tools.StringTool;
import com.yp.core.user.IUser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

public class LogIn extends AForm {
	@FXML
	private PasswordField txtPasword;
	@FXML
	private TextField txtUser;
	@FXML
	private Label lblMessage, lblSendPwd;
	@FXML
	private Button btnLogin;
	@FXML
	private Hyperlink lnkAddAccount, lnkSendPwd;
	@FXML
	private TableView<IReference<Integer>> tRoots;
	@FXML
	private TableColumn<IReference<Integer>, Button> stnUrl;
	@FXML
	private VBox rootMenuList;

	public static final String USER_NAME = "USER.NAME";

	@Override
	public void initialize(URL pLocation, ResourceBundle pResources) {
		String email = BaseConstants.getConfig(USER_NAME);
		if (!StringTool.isNull(email)) {
			txtUser.setText(email);
			txtUser.setFocusTraversable(false);
			txtPasword.requestFocus();
		}
	}

	private void genereteRootMenu(List<Group> pRootMenuList) {
		if (pRootMenuList != null) {
			for (Group root : pRootMenuList) {
				VBox box = new VBox();
				Hyperlink hRoot = new Hyperlink(root.getMenuLabel());
				hRoot.setTooltip(new Tooltip(root.getMenuTooltip()));
				hRoot.setStyle("-fx-font-size: 18.0;-fx-font-weight: bold;-fx-text-fill: blue;");
				hRoot.setOnAction(event -> logIn(root));
				hRoot.setText(root.getAppName());
				box.getChildren().add(hRoot);

				Label label = new Label();
				label.setText(String.format("\t(%s)", root.getName()));
				label.setStyle("-fx-font-style: italic;");
				box.getChildren().add(label);
				box.setAlignment(Pos.BASELINE_LEFT);
				rootMenuList.getChildren().add(box);
			}
		}

	}

	private void logIn(Group pRoot) {
		if (app.checkApplicationConfig(pRoot))
			((User) app.getUser()).setGroupId(pRoot.getId());
		app.setMenuList(new AppFuncModel().findGroupAppFuncs(pRoot.getId(), pRoot.getAppId()));

		app.showStartup();
	}

	private boolean checkEmail(String pEmail) {
		String email = BaseConstants.getConfig(USER_NAME);
		return !StringTool.isNull(email) && pEmail.equals(email);
	}

	@FXML
	protected void logIn(ActionEvent event) {
		message = "";
		try {
			if (!StringTool.isNull(txtUser.getText())) {
				if (!checkEmail(txtUser.getText())) {
					BaseConstants.setConfig(USER_NAME, txtUser.getText());
					BaseConstants.saveConfig();
				}
				try {
					String appId = app.getApplicationId();
					String clientIp = InetAddress.getLocalHost().getHostName();
					IResult<IUser> res = null;
					res = getUserModel().logIn(txtUser.getText(), txtPasword.getText(), appId, clientIp);
					if (res != null) {
						message = res.getMessage();
						if (res.isSuccess()) {
							btnLogin.setVisible(false);
							lnkAddAccount.setVisible(false);

							lnkSendPwd.setVisible(false);
							lblSendPwd.setVisible(false);

							IUser user = res.getData();
							app.setUser(user);
							if (app.getRootMenuList().size() > 1)
								genereteRootMenu(app.getRootMenuList());
							else
								logIn(app.getRootMenuList().get(0));
						} else if (BaseConstants.ERRORCODE_NO_USER == res.getErrorcode()) {
							lnkAddAccount.setVisible(true);

							lnkSendPwd.setVisible(false);
							lblSendPwd.setVisible(false);
						} else if (BaseConstants.ERRORCODE_WRONG_PASS == res.getErrorcode()) {
							lnkAddAccount.setVisible(false);

							lnkSendPwd.setVisible(true);
							lblSendPwd.setVisible(true);
						}
					}
				} catch (IOException e) {
					Logger.getLogger(MyLogger.NAME).log(Level.SEVERE, e.getMessage(), e);
				}
			}
		} finally {
			app.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);

		}
		lblMessage.setText(message);
	}

	@Override
	public String getHelpFileName() {
		return null;
	}

	@Override
	public void synchronize(boolean pToForm, Object[] pAdditionalParams) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(ActionEvent arg0) {
		show("3", ".AddAccount", BaseConstants.getString("AddAccount.Header"), null, BaseConstants.BUNDLE_MESSAGE);
	}

	public void sendPassword(ActionEvent arg0) {
		System.out.println("sendpassword");
		User userTo = new User();
		userTo.setEmail(txtUser.getText());
		userTo.accept();
		IResult<String> res = getUserModel().sendPasswordMail(app.getApplicationName(), userTo, null);

		addMessage(BaseConstants.getString("SendPassword.ToEmail"), res.getMessage());

		getAlert().showAndWait();
	}
}
