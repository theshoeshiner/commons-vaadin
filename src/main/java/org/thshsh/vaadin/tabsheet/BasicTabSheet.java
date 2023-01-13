package org.thshsh.vaadin.tabsheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.Tabs.SelectedChangeEvent;

@SuppressWarnings("serial")
@CssImport("basic-tab-sheet.css")
public class BasicTabSheet extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(BasicTabSheet.class);

	public static final String INVISIBLE_CLASS = "invisible";

	protected VerticalLayout contentLayout;
	protected List<BasicTab> basicTabs;
	protected Tabs tabs;

	public BasicTabSheet() {
		super();
		this.addClassName("tab-sheet");

		basicTabs = new ArrayList<>();
		tabs = new Tabs();
		contentLayout = new VerticalLayout();
		contentLayout.addClassName("tab-sheet-content");
		
		tabs.addSelectedChangeListener(this::handleSelectedChangeEvent);

		 add(tabs, contentLayout);

	}
	
	protected void handleSelectedChangeEvent(SelectedChangeEvent e) {
		
		BasicTabSheetSelectedChangeEvent event = new BasicTabSheetSelectedChangeEvent(e);

		LOGGER.debug("handleSelectedChangeEvent: {}",event);
		LOGGER.trace("from: {} to: {}",event.getPreviousTab(),event.getSelectedTab());
		
		

		 //fire change events to relevant tabs to give them a chance to postpone
		 if(e.getPreviousTab() != null) {
			 //if new tab is just a button then dont fire an event to the previous tab
			 if(e.getSelectedTab() != null && event.getSelectedTab().getContent() != null) {
				 ((BasicTab) e.getPreviousTab()).selectionChangeEvent(event);
			 }
		 }
		 if(e.getSelectedTab() != null) {
			 ((BasicTab) e.getSelectedTab()).selectionChangeEvent(event);
		 }
		

		 
		//by the time we arrive here the event may have already been postponed and continued multiple times by other listeners
		 if(event.getSelectedTab() != null) {
			 //only change the displayed content if the new tab has content to display
			 if(!event.isPostponed() && event.getSelectedTab().getContent() != null) {
				setSelectedTab((BasicTab) event.getSelectedTab());
			 }
			 else {
				//undo tab change
				 LOGGER.trace("undoing tab change");
				 if(event.getPreviousTab()==null) tabs.setSelectedTab(null);
				 else {
					 int index = tabs.indexOf(event.getPreviousTab());
					 LOGGER.trace("selecting tab: {}",index);
					 tabs.setSelectedIndex(index);
				 }
			 }
		 }
		 
		 event.setHandled(true);
	     
	}

	
	protected void setSelectedTab(BasicTab selectedTab) {
				
		 //set tab visibility
		 //TODO do we need to cycle through everything here or just manually unselect the prior tab??
		 basicTabs.forEach(page -> {
			 setVisible(page, false);
		 });
	     this.setVisible(((BasicTab)selectedTab), true);
	}

	protected void setVisible(BasicTab bt, Boolean visible) {
		Component c = bt.getContent();
		if(c!=null) {
			c.setVisible(visible);
			if(c instanceof HasStyle) {
				 HasStyle hs = (HasStyle) c;
				 if(visible) hs.removeClassName(INVISIBLE_CLASS);
				 else hs.addClassName(INVISIBLE_CLASS);
			} 
		}
	}
	

	public void replaceTab(BasicTab tab, Component content) {
		Component old = tab.getContent();
		tab.setContent(content);
		if(!tab.isSelected()) {
			setVisible(tab, false);
		}
		contentLayout.replace(old, content);
	}
	
	public void removeTab(BasicTab tab) {
		tabs.remove(tab);
		if(tab.getContent()!=null) {
			contentLayout.remove(tab.getContent());
		}
	}

	public BasicTab addTab(String tab, Component content) {
		return addTab(tab,content,null);
	}
	
	public BasicTab addTab(String tab, Component content,Integer i) {
		BasicTab t = new BasicTab(content,tab);
		addTab(t,i);
		return t;
	}
	
	public BasicTab addTab(Component tab, Component content) {
		return addTab(tab,content,null);
	}
	
	public BasicTab addTab(Component tab, Component content,Integer i) {
		BasicTab t = new BasicTab(content);
		addTab(t,i);
		t.add(tab);
		return t;
	}

	public BasicTab addTab(BasicTab tab) {
		return addTab(tab,(Integer)null);
	}
	
	public Optional<BasicTab> getTab(Component content) {
		return basicTabs.stream().filter(bt -> bt.getContent() == content).findFirst();
	}

	public BasicTab addTab(BasicTab tab,Integer index) {
		tab.setTabSheet(this);
		if(index != null) {
			tabs.addComponentAtIndex(index, tab);
			basicTabs.add(index, tab);
		}
		else {
			tabs.add(tab);
			basicTabs.add(tab);
		}
		LOGGER.debug("tab {} selected: {}",tab.getLabel(),tab.isSelected());
		if(!tab.isSelected()) {
			setVisible(tab, false);
		}
		if(tab.getContent()!=null) {
			if(index != null) contentLayout.addComponentAtIndex(index, tab.getContent());
			else contentLayout.add(tab.getContent());
		}
		return tab;
	}

	public VerticalLayout getContentLayout() {
		return contentLayout;
	}

	public void setContentLayout(VerticalLayout contents) {
		this.contentLayout = contents;
	}
	
	public void setSelectedIndex(int selectedIndex) {
		tabs.setSelectedIndex(selectedIndex);
	}

	public void setSelectedTab(Tab selectedTab) {
		tabs.setSelectedTab(selectedTab);
	}
	

	public int getSelectedIndex() {
		return tabs.getSelectedIndex();
	}

	public Tab getSelectedTab() {
		return tabs.getSelectedTab();
	}

	public Tabs getTabs() {
		return tabs;
	}

	public void setTabs(Tabs tabs) {
		this.tabs = tabs;
	}

	public List<BasicTab> getBasicTabs(){
		return basicTabs;
	}

}