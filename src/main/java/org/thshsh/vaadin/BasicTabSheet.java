package org.thshsh.vaadin;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

public class BasicTabSheet extends VerticalLayout {
	
	Map<Tab,Component> tabComponents;
	VerticalLayout contents;
	Tabs tabs;
	
	public BasicTabSheet() {
		super();
		
		tabComponents = new HashMap<>();
		tabs = new Tabs();
		contents = new VerticalLayout();
		
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
			 tabComponents.values().forEach(page -> page.setVisible(false));
		        Component selectedPage = tabComponents.get(tabs.getSelectedTab());
		        selectedPage.setVisible(true);
		    });
		
		  add(tabs, contents);

	}
	
	public void addTab(Tab tab, Component component) {
		tabComponents.put(tab, component);
		tabs.add(tab);
		contents.add(component);
		component.setVisible(false);
	}

}
