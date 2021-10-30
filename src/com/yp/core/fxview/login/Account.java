package com.yp.core.fxview.login;

import java.time.LocalDate;

import com.yp.admin.data.User;
import com.yp.core.ref.IReference;
import com.yp.core.tools.DateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Account extends User {

	private static final long serialVersionUID = 8220209306935217022L;
	private transient SimpleStringProperty password2 = new SimpleStringProperty("");

	public Account(User pUser) {
		super(pUser);
	}

	public Account() {
		super(-1);
	}

	private transient StringProperty emailProperty;

	public StringProperty emailProperty() {
		if (emailProperty == null)
			emailProperty = new SimpleStringProperty(getEmail()) {
				@Override
				public void set(String pValue) {
					super.set(pValue);
					setEmail(pValue);
				}
			};
		return emailProperty;
	}

	private transient StringProperty nameProperty;

	public StringProperty nameProperty() {
		if (nameProperty == null)
			nameProperty = new SimpleStringProperty(getName()) {
				@Override
				public void set(String pValue) {
					super.set(pValue);
					setName(pValue);
				}
			};
		return nameProperty;
	}

	private transient StringProperty surnameProperty;

	public StringProperty surnameProperty() {
		if (surnameProperty == null)
			surnameProperty = new SimpleStringProperty(getSurname()) {
				@Override
				public void set(String pValue) {
					super.set(pValue);
					setSurname(pValue);
				}
			};
		return surnameProperty;
	}

	private transient ObjectProperty<IReference<Integer>> homeCityProperty;

	public ObjectProperty<IReference<Integer>> homeCityProperty() {
		if (homeCityProperty == null)
			homeCityProperty = new SimpleObjectProperty<IReference<Integer>>(getHomeCityRef()) {
				@Override
				public void set(IReference<Integer> pValue) {
					super.set(pValue);
					setHomeCity(pValue.getKey());
				}
			};
		return homeCityProperty;
	}

	private transient StringProperty phoneProperty;

	public StringProperty phoneProperty() {
		if (phoneProperty == null)
			phoneProperty = new SimpleStringProperty(getMobilePhoneNu()) {
				@Override
				public void set(String pValue) {
					super.set(pValue);
					setPhoneno2(pValue);
				}
			};
		return phoneProperty;
	}

	private transient StringProperty password1Property;

	public StringProperty password1Property() {
		if (password1Property == null)
			password1Property = new SimpleStringProperty(getPassword()) {
				@Override
				public void set(String pValue) {
					super.set(pValue);
					setPassword(pValue);
				}
			};
		return password1Property;
	}

	public SimpleStringProperty password2Property() {
		return password2;
	}

	public String getPassword2() {
		return password2.get();
	}

	public void setPassword2(String pPassword) {
		password2.set(pPassword);
	}

	public boolean isPasswordsDiffer() {
		return !getPassword().equals(getPassword2());
	}

	private transient StringProperty addressProperty;

	public StringProperty addressProperty() {
		if (addressProperty == null)
			addressProperty = new SimpleStringProperty(getHomeAddress()) {
				@Override
				public void set(String pValue) {
					super.set(pValue);
					setHomeAddress(pValue);
				}
			};
		return addressProperty;
	}

	private transient ObjectProperty<LocalDate> birthdayProperty;

	public ObjectProperty<LocalDate> birthdayProperty() {
		if (birthdayProperty == null)
			birthdayProperty = new SimpleObjectProperty<LocalDate>(DateTime.asLocalDate(getBirthDate())) {
				@Override
				public void set(LocalDate pValue) {
					super.set(pValue);
					setBirthDateDb(DateTime.asDbDate(pValue));
				}
			};
		return birthdayProperty;
	}

}
