package com.yp.core.fxview.data;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.yp.core.entity.DataEntity;
import com.yp.core.ref.IReference;
import com.yp.core.tools.DateTime;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public abstract class PropertyWrapper extends DataEntity {

	private static final long serialVersionUID = 983248983559284948L;

	public PropertyWrapper(DataEntity pDe) {
		super();
		load(pDe);
	}

	public class BooleanProperty extends SimpleBooleanProperty {

		private String fieldName;
		private Object trueValue = Boolean.TRUE;
		private Object falseValue = Boolean.FALSE;

		public BooleanProperty(String pAlanAdi, Boolean pBoolean) {
			super(pBoolean);
			fieldName = pAlanAdi;
		}

		public BooleanProperty(String pFieldName, Boolean pValue, final Object pTrueValue, final Object pFalseValue) {
			super(pValue);
			fieldName = pFieldName;
			trueValue = pTrueValue;
			falseValue = pFalseValue;
		}

		@Override
		public void set(boolean pBoolean) {
			super.set(pBoolean);
			PropertyWrapper.this.set(fieldName, pBoolean ? trueValue : falseValue);
		}

		public Object getTrueValue() {
			return trueValue;
		}

		public Object getFalseValue() {
			return falseValue;
		}
	}

	public class StringProperty extends SimpleStringProperty {

		private String fieldName;
		private boolean isChanged;
		private CheckString checkString;

		public StringProperty(String pFieldName, String pValue) {
			super(pValue);
			fieldName = pFieldName;
			isChanged = true;
		}

		public StringProperty(String pFieldName, String pValue, CheckString pCheckString) {
			this(pFieldName, pValue);
			checkString = pCheckString;
		}

		public StringProperty(String pFieldName, String pValue, boolean pDegisti) {
			this(pFieldName, pValue);
			isChanged = pDegisti;
		}

		@Override
		public void set(String pValue) {
			String value = checkString != null ? checkString.getChecked(pValue) : pValue;
			super.set(value);
			PropertyWrapper.this.setField(fieldName, value, isChanged);
		}

	}

	public interface CheckString {
		String getChecked(String pValue);
	}

	public class DoubleProperty extends SimpleDoubleProperty {

		private String fieldName;
		private Double nullValue;
		private boolean isChanged;

		public DoubleProperty(String pFieldName, Double pValue, Double pNullValue, boolean pChanged) {
			super();
			fieldName = pFieldName;
			nullValue = pNullValue;
			isChanged = pChanged;
			setValue(pValue);
		}

		public DoubleProperty(String pFieldName, Double pValue, Double pNullValue) {
			this(pFieldName, pValue, pNullValue, true);
		}

		@Override
		public void setValue(Number pValue) {
			if (pValue == null)
				pValue = nullValue;
			set(pValue.doubleValue());
		}

		@Override
		public void set(double pValue) {
			super.set(pValue);
			PropertyWrapper.this.setField(fieldName, pValue, isChanged);
		}
	}

	public class BigDecimalProperty extends SimpleDoubleProperty {

		private String fieldName;
		private BigDecimal nullValue;
		private boolean isChanged;

		public BigDecimalProperty(String pFieldName, BigDecimal pValue, BigDecimal pNullValue, boolean pChanged) {
			super();
			fieldName = pFieldName;
			nullValue = pNullValue;
			isChanged = pChanged;
			setValue(pValue);
		}

		public BigDecimalProperty(String pFieldName, BigDecimal pValue, BigDecimal pNullValue) {
			this(pFieldName, pValue, pNullValue, true);
		}

		@Override
		public void setValue(Number pValue) {
			if (pValue == null)
				pValue = nullValue;
			set(pValue.doubleValue());
		}

		@Override
		public void set(double pValue) {
			super.set(pValue);
			PropertyWrapper.this.setField(fieldName, BigDecimal.valueOf(pValue), isChanged);
		}
	}

	public class IntegerProperty extends SimpleIntegerProperty {

		private String fieldName;
		private Integer nullValue;
		private boolean isChanged;

		public IntegerProperty(String pFieldName, Integer pValue, Integer pNullValue, boolean pChanged) {
			super();
			fieldName = pFieldName;
			nullValue = pNullValue;
			isChanged = pChanged;
			setValue(pValue);
		}

		public IntegerProperty(String pFieldName, Integer pValue, Integer pNullValue) {
			this(pFieldName, pValue, pNullValue, true);
		}

		@Override
		public void setValue(Number pValue) {
			if (pValue == null)
				pValue = nullValue;
			set(pValue.intValue());
		}

		@Override
		public void set(int pValue) {
			super.set(pValue);
			PropertyWrapper.this.setField(fieldName, pValue, isChanged);
		}
	}

	public class DateProperty extends SimpleObjectProperty<LocalDate> {

		private String fieldName;

		public DateProperty(String pFieldName, LocalDate pValue) {
			super();
			fieldName = pFieldName;
			set(pValue);
		}

		@Override
		public void setValue(LocalDate pValue) {
			if (pValue != null) {
				super.set(pValue);
				PropertyWrapper.this.setField(fieldName, DateTime.asDbDate(pValue), true);
			} else {
				super.set(null);
				PropertyWrapper.this.setField(fieldName, BigDecimal.ZERO, true);
			}
		}
	}

	public class RefProperty<V> extends SimpleObjectProperty<IReference<V>> {
		private String fieldName;
		private IReference<V> nullValue;

		public RefProperty(String pFieldName, IReference<V> pValue, IReference<V> pNullValue) {
			super();
			fieldName = pFieldName;
			nullValue = pNullValue;
			setValue(pValue);
		}

		@Override
		public void set(IReference<V> pValue) {
			super.set(pValue);
			PropertyWrapper.this.setField(fieldName, pValue.getKey(), true);
		}

		@Override
		public void setValue(IReference<V> pValue) {
			if (pValue == null)
				pValue = nullValue;
			set(pValue);
		}
	}
}
