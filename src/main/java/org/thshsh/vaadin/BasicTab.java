package org.thshsh.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")
public class BasicTab extends Tab implements ClickNotifier<BasicTab> {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(BasicTab.class);
	
	protected Component content;
	
	public BasicTab(Component content) {
		super();
		this.content = content;
	}

	public BasicTab(Component content,Component... components) {
		super(components);
		this.content = content;
	}

	public BasicTab(Component content,String label) {
		super(label);
		this.content = content;
	}
	
	public Registration addSelectedChangeListener(ComponentEventListener<BasicTabSheetSelectedChangeEvent> listener) {
        return addListener(BasicTabSheetSelectedChangeEvent.class, listener);
    }
	
	public void selectionChangeEvent(BasicTabSheetSelectedChangeEvent e) {
		this.fireEvent(e);
	}

	public Component getContent() {
		return content;
	}

	public void setContent(Component content) {
		this.content = content;
	}

}
