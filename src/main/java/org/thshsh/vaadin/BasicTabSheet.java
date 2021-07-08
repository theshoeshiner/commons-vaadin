package org.thshsh.vaadin;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

@SuppressWarnings("serial")
@CssImport("basic-tab-sheet.css")
public class BasicTabSheet extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(BasicTabSheet.class);

	public static final String INVISIBLE_CLASS = "invisible";

	Map<Tab,Component> tabComponents;
	VerticalLayout contentLayout;
	Tabs tabs;

	public BasicTabSheet() {
		super();
		this.addClassName("tab-sheet");
		tabComponents = new HashMap<>();
		tabs = new Tabs();
		contentLayout = new VerticalLayout();
		contentLayout.addClassName("tab-sheet-content");
		contentLayout.setSizeFull();

		/*
		 * Tab balTab = new Tab("Balances"); VerticalLayout balancesLayout =
		 * createBalancesTab();
		 *
		 * Tab allTab = new Tab("Allocations"); VerticalLayout allLayout =
		 * createAllocationsTab();
		 *
		 * Tab funcTab = new Tab("Functions"); VerticalLayout funcLayout =
		 * createFunctionsTab();
		 *
		 * tabsToPages = new HashMap<>(); tabsToPages.put(main,mainLayout);
		 * tabsToPages.put(balTab,balancesLayout); tabsToPages.put(allTab,allLayout);
		 * tabsToPages.put(funcTab,funcLayout);
		 *
		 * Tabs tabs = new Tabs(main,balTab,allTab,funcTab); pages = new
		 * VerticalLayout(mainLayout,balancesLayout,allLayout,funcLayout);
		 * pages.setHeight("100%");
		 *
		 * tabs.addSelectedChangeListener(e -> { tabsToPages.values().forEach(page ->
		 * page.setVisible(false)); Component selectedPage =
		 * tabsToPages.get(tabs.getSelectedTab()); selectedPage.setVisible(true); });
		 *
		 * add(tabs, pages);
		 */

		 tabs.addSelectedChangeListener(e -> {
			 tabComponents.values().forEach(page -> {
				 setVisible(page, false);
			 });
		     Component selectedPage = tabComponents.get(tabs.getSelectedTab());
		     this.setVisible(selectedPage, true);
		    });

		  add(tabs, contentLayout);

	}

	protected void setVisible(Component c, Boolean visible) {
		 c.setVisible(visible);
		 if(c instanceof HasStyle) {
			 HasStyle hs = (HasStyle) c;
			 if(visible) hs.removeClassName(INVISIBLE_CLASS);
			 else hs.addClassName(INVISIBLE_CLASS);
		 }
	}

	public void replaceTab(Tab tab, Component component) {
		Component old = tabComponents.get(tab);
		tabComponents.put(tab, component);
		contentLayout.replace(old, component);
	}

	public Tab addTab(String tab, Component component) {
		Tab t = new Tab(tab);
		addTab(t,component);
		return t;
	}


	public Tab addTab(Tab tab, Component component) {

		tabComponents.put(tab, component);
		tabs.add(tab);
		LOGGER.info("tab {} selected: {}",tab.getLabel(),tab.isSelected());
		if(!tab.isSelected()) {
			//component.setVisible(false);
			setVisible(component, false);
		}

		contentLayout.add(component);

		return tab;
	}

	public Map<Tab, Component> getTabComponents() {
		return tabComponents;
	}

	public void setTabComponents(Map<Tab, Component> tabComponents) {
		this.tabComponents = tabComponents;
	}

	public VerticalLayout getContentLayout() {
		return contentLayout;
	}

	public void setContentLayout(VerticalLayout contents) {
		this.contentLayout = contents;
	}

	public Tabs getTabs() {
		return tabs;
	}

	public void setTabs(Tabs tabs) {
		this.tabs = tabs;
	}



}
