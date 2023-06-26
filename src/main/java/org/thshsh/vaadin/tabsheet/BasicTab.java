package org.thshsh.vaadin.tabsheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")
public class BasicTab extends Tab implements ClickNotifier<BasicTab>, HasOrderedComponents {
	
	public static final String TAB_LABEL_CLASS = "tab-label";
	public static final String TAB_ICON_CLASS = "tab-icon";

	protected static final Logger LOGGER = LoggerFactory.getLogger(BasicTab.class);
	
	protected Component content;
	protected BasicTabSheet tabSheet;
	protected Icon icon;
	protected Component label;
	

	public BasicTab(String label,Icon icon,Component content,Component... components) {
	    this(new Span(label),icon,content,components);
	}
	
	public BasicTab(Component label,Icon icon,Component content,Component... components) {
		super();
		this.content = content;
		this.label = label;
		this.icon = icon;
		if(icon != null) {
            add(this.icon);
            this.icon.addClassName(TAB_ICON_CLASS);
        }
		if(label != null) {
    		if(this.label instanceof HasStyle) {
    			((HasStyle)this.label).addClassName(TAB_LABEL_CLASS);
    		}
    		add(this.label);
		}
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
			this.icon.addClassName(TAB_ICON_CLASS);
		}
	}
	
	public void setLabelText(String label) {
		if(this.label instanceof HasText) {
			((HasText)this.label).setText(label);
		}
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

	public void setOrientation(BasicTabSheet.Orientation o) {
	    this.getElement().setAttribute("orientation", o.toString().toLowerCase());
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BasicTab [label=");
		builder.append(label!=null && label instanceof HasText?((HasText)label).getText():null);
		builder.append("]");
		return builder.toString();
	}
	
	

}
