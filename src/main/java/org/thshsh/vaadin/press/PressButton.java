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
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")
public class PressButton extends Button implements HasValue<ComponentValueChangeEvent<PressButton,Boolean>, Boolean> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PressButton.class);

	public static final String PRESS = "press";
	public static final String PRESSED = "pressed";
	public static final String UNPRESSED = "unpressed";
	public static final String INVERT = "invert";
	
	protected Boolean invertStyle = false;
	protected Boolean pressed;
	//protected ButtonVariant pressedVariant = ButtonVariant.LUMO_PRIMARY;
	protected ButtonVariant[] pressedVariants;
	protected ButtonVariant[] unpressedVariants;
	protected Component pressedIcon;
	protected Component unpressedIcon;
	protected String[] pressedClasses;
	protected String[] unpressedClasses;
	protected String pressedTooltipText;
	protected String unpressedTooltipText;
	
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
	
	public void setThemeVariants(ButtonVariant[] unpressed,ButtonVariant[] pressed) {
		this.pressedVariants = pressed;
		this.unpressedVariants = unpressed;
	}
	
	public void setTooltipText(String unpressedTooltipText,String pressedTooltipText) {
		this.pressedTooltipText = pressedTooltipText;
		this.unpressedTooltipText = unpressedTooltipText;
	}
	
	public void setPressedTooltipText(String pressedTooltipText) {
		this.pressedTooltipText = pressedTooltipText;
	}

	public void setUnpressedTooltipText(String unpressedTooltipText) {
		this.unpressedTooltipText = unpressedTooltipText;
	}
	
	protected void clicked(ClickEvent<Button> event) {
		setValue(!getPressedDefaultFalse());
		updateStyle();
	}

	public void setClassNames(String[] unpressed, String[] pressed) {
		this.pressedClasses = pressed;
		this.unpressedClasses = unpressed;
	}
	
	protected void updateStyle() {
		this.setThemeName(INVERT, invertStyle);
		
		LOGGER.trace("updatestyle: {}",pressed);
		Boolean pressed = getPressedDefaultFalse();
		
		if(pressed) {
			//this.addThemeVariants(pressedVariant);
			if(pressedIcon!=null)this.setIcon(pressedIcon);
			
			this.removeClassName(UNPRESSED);
			this.addClassName(PRESSED);
			
			if(unpressedClasses != null) this.removeClassNames(unpressedClasses);
			if(pressedClasses != null) this.addClassNames(pressedClasses);
			
			if(pressedTooltipText!=null)this.setTooltipText(pressedTooltipText);
			
			if(unpressedVariants!=null)this.removeThemeVariants(unpressedVariants);
			if(pressedVariants!=null)this.addThemeVariants(pressedVariants);
			
		}
		else {
			
			if(this.unpressedIcon!=null) this.setIcon(unpressedIcon);
			
			this.removeClassName(PRESSED);
			this.addClassName(UNPRESSED);
			
			if(pressedClasses != null) this.removeClassNames(pressedClasses);
			if(unpressedClasses != null) this.addClassNames(unpressedClasses);
			
			if(unpressedTooltipText!=null)this.setTooltipText(unpressedTooltipText);
			LOGGER.trace("setting tooltip: {}",unpressedTooltipText);
			
			if(pressedVariants!=null)this.removeThemeVariants(pressedVariants);
			if(unpressedVariants!=null)this.addThemeVariants(unpressedVariants);
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

	public boolean getPressedDefaultFalse() {
		return Boolean.TRUE.equals(getPressed());
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
		LOGGER.trace("setvalue to: {} from: {}",value,pressed);
		if(!Objects.equals(this.pressed, value)) {
			LOGGER.trace("change value");
			Boolean oldValue = this.pressed;
			this.pressed = value;
			updateStyle();
			LOGGER.trace("fire event");
			ComponentUtil.fireEvent(this, new ComponentValueChangeEvent<PressButton, Boolean>(this, this,oldValue, false));
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
	public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {}

	@Override
	public boolean isRequiredIndicatorVisible() {
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Registration addValueChangeListener(ValueChangeListener<? super ComponentValueChangeEvent<PressButton, Boolean>> listener) {
		 ComponentEventListener componentListener = event -> {
	            ComponentValueChangeEvent<PressButton, Boolean> valueChangeEvent = (ComponentValueChangeEvent<PressButton, Boolean>) event;
	            listener.valueChanged(valueChangeEvent);
		 };
		 return ComponentUtil.addListener(this,ComponentValueChangeEvent.class, componentListener);
	}

	
}
