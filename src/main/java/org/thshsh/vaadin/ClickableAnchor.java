package org.thshsh.vaadin;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.AbstractStreamResource;

@SuppressWarnings("serial")
@CssImport("clickable-anchor.css")
public class ClickableAnchor extends Anchor implements ClickNotifier<ClickableAnchor> {

	public static final String CLASS ="clickable-anchor";
	
	public ClickableAnchor() {
		super();
		this.getElement().removeAttribute("href");
		this.addClassName(CLASS);
	}

	public ClickableAnchor(AbstractStreamResource href, String text) {
		super(href, text);
		this.getElement().removeAttribute("href");
		this.addClassName(CLASS);
	}

	public ClickableAnchor(String href, Component... components) {
		super(href, components);
		this.getElement().removeAttribute("href");
		this.addClassName(CLASS);
	}

	public ClickableAnchor(String href, String text) {
		super(href, text);
		this.getElement().removeAttribute("href");
		this.addClassName(CLASS);
	}

	
	
}
