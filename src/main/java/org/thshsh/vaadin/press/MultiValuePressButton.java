package org.thshsh.vaadin.press;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thshsh.vaadin.press.PressButtonRadioButtonGroup.ItemIconGenerator;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.shared.Registration;

public class MultiValuePressButton<T> extends Button implements HasValue<ComponentValueChangeEvent<MultiValuePressButton<T>,T>, T> {

	private static final long serialVersionUID = 5640218543627433056L;

	public static final Logger LOGGER = LoggerFactory.getLogger(MultiValuePressButton.class);
	
	protected ItemLabelGenerator<T> itemLabelGenerator;
	protected ItemIconGenerator<T> itemIconGenerator;
	
	protected List<T> items;
	protected T defaultValue;
	protected T value;
	protected Integer index;
	
	
	public MultiValuePressButton() {
		super();
		init();
		
	}

	public MultiValuePressButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(icon, clickListener);
		init();
	}
	

	public MultiValuePressButton(Component icon) {
		super(icon);
		init();
	}
	
	public MultiValuePressButton(Component unpressedIcon, Component pressedIcon) {
		super(unpressedIcon);
		init();
	}

	public MultiValuePressButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, icon, clickListener);
		init();
	}

	public MultiValuePressButton(String text, Component icon) {
		super(text, icon);
		init();
	}

	public MultiValuePressButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, clickListener);
		init();
	}

	public MultiValuePressButton(String text) {
		super(text);
		init();
	}
	
	

	protected void init() {
		this.addClickListener(this::clicked);
	}
	
	
	public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
		this.itemLabelGenerator = itemLabelGenerator;
	}

	public void setItemIconGenerator(ItemIconGenerator<T> _itemIconGenerator) {
		this.itemIconGenerator = _itemIconGenerator;
		if(itemIconGenerator!=null && value != null) this.setIcon(itemIconGenerator.apply(value)); 
		this.setIcon(null);
	}
	
	public void setItems(Collection<T> its) {
		items = new ArrayList<>(its);
		if(index == null) {
			index = 0;
		}
		if(value == null || !items.contains(value)) {
			if(defaultValue == null) setValue(items.get(index),false);
			else setValue(defaultValue,false);
			
		}
		if(value != null && index == null) index = items.indexOf(value);
		LOGGER.trace("set items index/value: {} / {}",index,value);
	}
	
	
	public T getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(T _defaultItem) {
		if(_defaultItem != null && !items.contains(_defaultItem)) throw new IllegalArgumentException();
		this.defaultValue = _defaultItem;
		if(this.value == null && this.defaultValue != null) {
			setValue(defaultValue);
		}
		LOGGER.trace("setDefaultValue index/value: {}/{}",index,value);
	}

	protected void clicked(ClickEvent<Button> event) {
		this.index++;
		if(this.index == this.items.size()) index = 0;
		setValue(this.items.get(index), false);
	}

	public boolean getPressedDefaultFalse() {
		return Boolean.TRUE.equals(getValue());
	}

	@Override
	public void setValue(T _value) {
		this.setValue(_value, true);
	}
	
 
	protected void setValue(T _value,boolean updateIndex) {
		LOGGER.trace("setvalue to: {} from: {}",_value,value);
		if(_value == null) _value = this.defaultValue;
		if(!Objects.equals(this.value, _value)) {
			LOGGER.trace("change value");
			T oldValue = this.value;
			this.value = _value;
			if(updateIndex) this.index = this.items.indexOf(value);
			if(itemIconGenerator!=null) {
				this.setIcon(itemIconGenerator.apply(value));
			}
			if(itemLabelGenerator!=null) {
				this.setText(itemLabelGenerator.apply(value));
			}
			ComponentUtil.fireEvent(this, new ComponentValueChangeEvent<MultiValuePressButton<T>, T>(this, this,oldValue, false));
		}
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setEnabled(!readOnly);
	}

	@Override
	public boolean isReadOnly() {
		return !super.isEnabled();
	}

	@Override
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {}

	@Override
	public boolean isRequiredIndicatorVisible() {
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Registration addValueChangeListener(ValueChangeListener<? super ComponentValueChangeEvent<MultiValuePressButton<T>, T>> listener) {
		 ComponentEventListener componentListener = event -> {
	            ComponentValueChangeEvent<MultiValuePressButton<T>, T> valueChangeEvent = (ComponentValueChangeEvent<MultiValuePressButton<T>, T>) event;
	            listener.valueChanged(valueChangeEvent);
		 };
		 return ComponentUtil.addListener(this,ComponentValueChangeEvent.class, componentListener);
	}


}
