package org.thshsh.vaadin.press;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")
@CssImport("press-button.css")
public class PressButton extends Button implements HasValue<ComponentValueChangeEvent<PressButton,Boolean>, Boolean> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PressButton.class);

	public static final String PRESS = "press";
	public static final String PRESSED = "pressed";
	public static final String UNPRESSED = "unpressed";
	public static final String INVERT = "invert";
	
	protected Boolean invertStyle = false;
	protected Boolean pressed = false;
	protected ButtonVariant pressedVariant = ButtonVariant.LUMO_PRIMARY;
	protected Component pressedIcon;
	protected Component unpressedIcon;
	
	//private final AbstractFieldSupport<PressButton, Boolean> fieldSupport;
	
	public PressButton() {
		super();
		init();
	}

	public PressButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(icon, clickListener);
		this.unpressedIcon = icon;
		init();
	}

	public PressButton(Component icon) {
		super(icon);
		this.unpressedIcon = icon;
		init();
	}
	
	public PressButton(Component unpressedIcon, Component pressedIcon) {
		super(unpressedIcon);
		this.pressedIcon = pressedIcon;
		this.unpressedIcon = unpressedIcon;
		init();
	}

	public PressButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, icon, clickListener);
		this.unpressedIcon = icon;
		init();
	}

	public PressButton(String text, Component icon) {
		super(text, icon);
		this.unpressedIcon = icon;
		init();
	}

	public PressButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, clickListener);
		init();
	}

	public PressButton(String text) {
		super(text);
		init();
	}
	
	protected void init() {
		this.addClickListener(this::clicked);
		this.addThemeName(PRESS);
		updateStyle();
	}
	
	protected void clicked(ClickEvent<Button> event) {
		setValue(!pressed);
		updateStyle();
	}

	protected void updateStyle() {
		this.setThemeName(INVERT, invertStyle);
		if(pressed) {
			this.addThemeVariants(pressedVariant);
			this.addClassName(PRESSED);
			this.removeClassName(UNPRESSED);
			if(pressedIcon!=null)this.setIcon(pressedIcon);
		}
		else {
			this.removeThemeVariants(pressedVariant);
			this.removeClassName(PRESSED);
			this.addClassName(UNPRESSED);
			this.setIcon(unpressedIcon);
		}
	}

	public Component getPressedIcon() {
		return pressedIcon;
	}

	public void setPressedIcon(Component pressedIcon) {
		this.pressedIcon = pressedIcon;
		this.unpressedIcon = super.getIcon();
	}

	public void setPressed(boolean pressed) {
		setValue(pressed);
	}
	
	public Boolean getPressed() {
		return getValue();
	}


	public Boolean getInvertStyle() {
		return invertStyle;
	}

	public void setInvertStyle(Boolean invertStyle) {
		this.invertStyle = invertStyle;
		this.setThemeName(INVERT, invertStyle);
	}

	@Override
	public void setValue(Boolean value) {
		LOGGER.info("setvalue to: {} from: {}",value,pressed);
		if(value == null) value = false;
		if(!Objects.equals(this.pressed, value)) {
			LOGGER.info("change value");
			this.pressed = value;
			updateStyle();
			LOGGER.info("fire event");
			ComponentUtil.fireEvent(this, new ComponentValueChangeEvent<PressButton, Boolean>(this, this, !value, false));
		}
	}

	@Override
	public Boolean getValue() {
		return pressed;
	}

	/*@Override
	public Registration addValueChangeListener(ValueChangeListener<? super ValueChangeEvent<Boolean>> listener) {
		  ComponentEventListener componentListener = event -> {
	            ComponentValueChangeEvent<PressButton, Boolean> valueChangeEvent = (ComponentValueChangeEvent<PressButton, Boolean>) event;
	            listener.valueChanged(valueChangeEvent);
	        };
	        return ComponentUtil.addListener(this,
	                ComponentValueChangeEvent.class, componentListener);
		
	
	}*/

	@Override
	public void setReadOnly(boolean readOnly) {
		super.setEnabled(!readOnly);
	}

	@Override
	public boolean isReadOnly() {
		return !super.isEnabled();
	}

	@Override
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isRequiredIndicatorVisible() {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Registration addValueChangeListener(ValueChangeListener<? super ComponentValueChangeEvent<PressButton, Boolean>> listener) {
		 ComponentEventListener componentListener = event -> {
			 	//LOGGER.info("event: {}",event);
	            ComponentValueChangeEvent<PressButton, Boolean> valueChangeEvent = (ComponentValueChangeEvent<PressButton, Boolean>) event;
	            listener.valueChanged(valueChangeEvent);
		 };
		 return ComponentUtil.addListener(this,ComponentValueChangeEvent.class, componentListener);
	}

	
}
