package org.thshsh.vaadin.press;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;

@SuppressWarnings("serial")
@CssImport("press-button.css")
public class PressButton extends Button {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(PressButton.class);

	public static final String PRESS = "press";
	public static final String PRESSED = "pressed";
	public static final String UNPRESSED = "unpressed";
	
	protected Boolean pressed = false;
	protected ButtonVariant pressedVariant = ButtonVariant.LUMO_PRIMARY;
	
	public PressButton() {
		super();
		init();
	}

	public PressButton(Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(icon, clickListener);
		init();
	}

	public PressButton(Component icon) {
		super(icon);
		init();
	}

	public PressButton(String text, Component icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, icon, clickListener);
		init();
	}

	public PressButton(String text, Component icon) {
		super(text, icon);
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
		this.addClassName(UNPRESSED);
		this.addThemeName(PRESS);
	}
	
	protected void clicked(ClickEvent<Button> event) {
		pressed = !pressed;
		update();
	}

	protected void update() {
		if(pressed) {
			this.addThemeVariants(pressedVariant);
			this.addClassName(PRESSED);
			this.removeClassName(UNPRESSED);
		}
		else {
			this.removeThemeVariants(pressedVariant);
			this.removeClassName(PRESSED);
			this.addClassName(UNPRESSED);
		}
	}
	
	public void setPressed(boolean pressed) {
		if(this.pressed != pressed) {
			this.pressed = pressed;
			update();
		}
	}
	
	public Boolean getPressed() {
		return pressed;
	}

	
}
