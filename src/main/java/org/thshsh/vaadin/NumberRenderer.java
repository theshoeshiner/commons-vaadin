package org.thshsh.vaadin;

import java.text.NumberFormat;
import java.util.Locale;

import com.vaadin.flow.function.ValueProvider;

@SuppressWarnings("serial")
public class NumberRenderer<SOURCE> extends com.vaadin.flow.data.renderer.NumberRenderer<SOURCE>{

	Boolean zeroAsNull = false;
	
	public NumberRenderer(ValueProvider<SOURCE, Number> valueProvider, Locale locale) {
		super(valueProvider, locale);
	}

	public NumberRenderer(ValueProvider<SOURCE, Number> valueProvider, NumberFormat numberFormat, String nullRepresentation,Boolean zeroAsNull) {
		super(valueProvider, numberFormat, nullRepresentation);
		this.zeroAsNull = zeroAsNull;
	}

	public NumberRenderer(ValueProvider<SOURCE, Number> valueProvider, NumberFormat numberFormat) {
		super(valueProvider, numberFormat);
	}

	public NumberRenderer(ValueProvider<SOURCE, Number> valueProvider, String formatString, Locale locale, String nullRepresentation,Boolean zeroAsNull) {
		super(valueProvider, formatString, locale, nullRepresentation);
		this.zeroAsNull = zeroAsNull;
	}

	public NumberRenderer(ValueProvider<SOURCE, Number> valueProvider, String formatString, Locale locale) {
		super(valueProvider, formatString, locale);
	}

	public NumberRenderer(ValueProvider<SOURCE, Number> valueProvider, String formatString) {
		super(valueProvider, formatString);
	}

	 @Override
	 protected String getFormattedValue(Number value) {
		 if(value != null && zeroAsNull && value.intValue()==0) value = null;
		 return super.getFormattedValue(value);
	 }
	
	
}
