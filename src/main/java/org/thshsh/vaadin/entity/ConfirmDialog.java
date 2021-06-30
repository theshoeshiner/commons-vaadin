package org.thshsh.vaadin.entity;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@SuppressWarnings("serial")
@CssImport("./styles/confirm-dialog.css")
public class ConfirmDialog extends Dialog {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ConfirmDialog.class);
	
	Icon icon;
	String header;
	String question;
	
	String yesText = "Yes";
	VaadinIcon yesIcon = VaadinIcon.CHECK;
	
	String noText = "No";
	VaadinIcon noIcon = VaadinIcon.ARROW_BACKWARD;
	
	HorizontalLayout buttonLayout;
	List<ButtonConfig> buttons;
	
	VerticalLayout mainLayout;
	HorizontalLayout captionLayout;
	
	String[] classNames;
	
	Span caption;
	
	public ConfirmDialog(String header,String q, Icon i) {
		this.header = header;
		this.icon = i;
		this.question = q;
		this.buttons = new ArrayList<>();
	}
	
	public ConfirmDialog(String header,String q) {
		this(header,q,VaadinIcon.QUESTION_CIRCLE.create());
		
	}
	
	public void setModal(boolean b) {
		this.setCloseOnEsc(!b);
		this.setCloseOnOutsideClick(!b);
	}
	
	public ButtonConfig withButton(String text, Icon icon, Runnable r) {
		ButtonConfig bc = ButtonConfig.create().with(text,icon, r);
		buttons.add(bc);
		return bc;
	}
	
	public ButtonConfig withYesButton() {
		ButtonConfig bc = ButtonConfig.create().with(yesText, yesIcon.create(), null);
		buttons.add(bc);
		return bc;
	}
	
	public ButtonConfig withNoButton() {
		ButtonConfig bc = ButtonConfig.create().with(noText, noIcon.create(), null);
		buttons.add(bc);
		return bc;
	}
	
	public void withClassNames(String... c) {
		this.classNames = c;
	}


	@Override
	protected void onAttach(AttachEvent attachEvent) {
	
		mainLayout = new VerticalLayout();
		mainLayout.addClassName("confirm-dialog");
		mainLayout.setMargin(false);
		mainLayout.setPadding(false);
		mainLayout.setSpacing(true);
		
		if(classNames!=null) mainLayout.addClassNames(classNames);
		
		add(mainLayout);
		
		if(header != null) {
			H3 h3 = new H3(header);
			mainLayout.add(h3);
		}
		
		captionLayout = new HorizontalLayout();
		captionLayout.setAlignItems(Alignment.CENTER);
		mainLayout.add(captionLayout);
		captionLayout.setMargin(false);
		captionLayout.setPadding(false);
		if(icon != null) {
			icon.setSize("2em");
			captionLayout.add(icon);
		}
		
		caption = new Span(question);
		caption.setMaxWidth("400px");
		captionLayout.add(caption);
		
		buttonLayout = new HorizontalLayout();
		buttonLayout.setWidthFull();
		buttonLayout.setSpacing(true);
		//buttonLayout.setJustifyContentMode(JustifyContentMode.END);
		buttonLayout.setJustifyContentMode(JustifyContentMode.END);
		mainLayout.add(buttonLayout);
		buttonLayout.setAlignItems(Alignment.CENTER);
		
		for(ButtonConfig config : buttons) {
			Button b = new Button();
			if(config.text!=null) b.setText(config.text);
			if(config.icon!=null) {
				LOGGER.info("setting icon: {} for {}",config.icon,config.text);
				b.setIcon(config.icon);
			}
			if(config.variants!=null) b.addThemeVariants(config.variants);
			if(config.classNames!=null) b.addClassNames(config.classNames);
			b.addClickListener(click -> {
				clickedButton(click,config,b);
			});
			buttonLayout.add(b); 
		}
		
		super.onAttach(attachEvent);
	}
	
	protected void clickedButton(ClickEvent<Button> event, ButtonConfig config, Button b) {
		if(config.runnable!=null) config.runnable.run();
		if(config.close) this.close();
	}
	
	public static class ButtonConfig {
		
		String text;
		Icon icon;
		Runnable runnable;
		Boolean close = true;
		ButtonVariant[] variants;
		String[] classNames;
		
		public static ButtonConfig create() {
			return new ButtonConfig();
		}
		
		public ButtonConfig with(Runnable r) {
			this.runnable = r;
			return this;
		}
		
		public ButtonConfig with(String t,Icon i,Runnable r) {
			this.text = t;
			this.icon = i;
			this.runnable = r;
			return this;
		}
		
		public ButtonConfig with(Icon i,Runnable r) {
			this.icon = i;
			this.runnable = r;
			return this;
		}
		
		public ButtonConfig withVariants(ButtonVariant... vars) {
			this.variants = vars;
			return this;
		}
		
		public ButtonConfig withClassNames(String... c) {
			this.classNames = c;
			return this;
		}
		
		public ButtonConfig withClose(Boolean c) {
			this.close = c;
			return this;
		}
		
		public ButtonConfig withText(String t) {
			this.text = t;
			return this;
		}
		
		public ButtonConfig withIcon(Icon i) {
			this.icon = i;
			return this;
		}
		
		public ButtonConfig withRunnable(Runnable r) {
			this.runnable = r;
			return this;
		}
	}

	public VerticalLayout getMainLayout() {
		return mainLayout;
	}

	public HorizontalLayout getCaptionLayout() {
		return captionLayout;
	}

	public HorizontalLayout getButtonLayout() {
		return buttonLayout;
	}
	
	

}
