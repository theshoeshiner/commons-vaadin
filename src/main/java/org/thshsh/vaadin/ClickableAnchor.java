package org.thshsh.vaadin;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.server.AbstractStreamResource;

@SuppressWarnings("serial")
public class ClickableAnchor extends Anchor implements ClickNotifier<ClickableAnchor> {

	public ClickableAnchor() {
		super();
	}

	public ClickableAnchor(AbstractStreamResource href, String text) {
		super(href, text);
	}

	public ClickableAnchor(String href, Component... components) {
		super(href, components);
	}

	public ClickableAnchor(String href, String text) {
		super(href, text);
	}

	
	
}
