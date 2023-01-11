package org.thshsh.vaadin.tabsheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")
public class BasicTab extends Tab implements ClickNotifier<BasicTab>, HasOrderedComponents {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(BasicTab.class);
	
	protected Component content;
	protected BasicTabSheet tabSheet;
	protected Icon icon;
	protected Span label;
	
	public BasicTab(Component content) {
		this(content,null);
	}

	public BasicTab(Component content,String label,Component... components) {
		super(components);
		this.content = content;
		this.label = new Span();
		if(label  != null) this.label.setText(label);
		add(this.label);
	}

	public BasicTab(Component content,String label) {
		super();
		this.content = content;
		this.label = new Span();
		if(label  != null) this.label.setText(label);
		add(this.label);
	}
	
	

	public BasicTabSheet getTabSheet() {
		return tabSheet;
	}

	public void setTabSheet(BasicTabSheet tabSheet) {
		this.tabSheet = tabSheet;
	}
	
	public void setIcon(Icon newIcon) {
		LOGGER.info("setIcon: {}",newIcon);
		if(newIcon == null) clearIcon();
		else {
			if(icon != null) {
				this.replace(icon, newIcon);
			}
			else {
				this.add(newIcon);
			}
			this.icon = newIcon;
		}
	}
	
	public void setLabelText(String label) {
		this.label.setText(label);
	}
	
	public void clearIcon() {
		if(this.icon != null) {
			this.remove(icon);
			this.icon = null;
		}
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BasicTab [label=");
		builder.append(label!=null?label.getText():null);
		builder.append("]");
		return builder.toString();
	}
	
	

}
