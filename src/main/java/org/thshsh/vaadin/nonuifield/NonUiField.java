package org.thshsh.vaadin.nonuifield;

import com.vaadin.flow.component.customfield.CustomField;

/**
 * Field that renders nothing to the UI and is just used to ensure that binders write certain values in the backend
 * @author daniel.watson
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class NonUiField<T> extends CustomField<T> {

	protected T value;
	
	public NonUiField() {}
	
	public NonUiField(T value) {
		super(value);
		
	}
	
	@Override
	protected T generateModelValue() {
		return value;
	}

	@Override
	protected void setPresentationValue(T newPresentationValue) {
		this.value = newPresentationValue;
	}

	

}
