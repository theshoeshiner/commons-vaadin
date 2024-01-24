package org.thshsh.vaadin.tabsheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.jchristophe.SortableConfig;
import org.vaadin.jchristophe.SortableGroupStore;
import org.vaadin.jchristophe.SortableLayout;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.ThemableLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabsVariant;

@SuppressWarnings("serial")
@CssImport("./basic-tab-sheet.css")
@Tag("tab-sheet")
public class BasicTabSheet extends FlexLayout implements FlexComponent,ThemableLayout, HasStyle, HasOrderedComponents, HasSize, HasTheme {


	public static final Logger LOGGER = LoggerFactory.getLogger(BasicTabSheet.class);

	public static final String INVISIBLE_CLASS = "invisible";
	//FIXME dont need this class since we can style by tag name
	public static final String TAB_SHEET_CLASS = "tab-sheet";
	public static final String TAB_SHEET_CONTENT_CLASS = "tab-sheet-content";
	public static final String DRAG_HANDLE_CLASS = "draggable-tab";
	
	public enum Orientation {
	    Horizontal,Vertical;
	}
	
	protected SortableConfig rowsSortableConfig;
    protected SortableGroupStore rowsSortableGroupStore;
    
	protected VerticalLayout contentLayout;
	protected List<BasicTab> basicTabs;
	protected Orientation orientation;
	protected FlexLayout tabsLayout;
	protected Component tabsComponent;
	protected BasicTab selectedTab;
	protected Boolean lazyRender = true;
	

	public BasicTabSheet() {
		super();
		this.addClassName(TAB_SHEET_CLASS);

		rowsSortableConfig = new SortableConfig();
		rowsSortableConfig.setAnimation(150);
		rowsSortableConfig.setGhostClass("tabsheet-drag-ghost");
		rowsSortableConfig.setDragClass("tabsheet-drag");
		rowsSortableConfig.setChosenClass("tabsheet-drag-chosen");
		rowsSortableConfig.setSelectedClass("tabsheet-drag-selected");
		
		rowsSortableGroupStore = new SortableGroupStore();
		
		basicTabs = new ArrayList<>();
		tabsLayout = new FlexLayout();
		tabsLayout.addClassName("tabs");
		tabsComponent = tabsLayout;
		contentLayout = new VerticalLayout();
		contentLayout.setPadding(false);
		contentLayout.setMargin(false);
		contentLayout.addClassName(TAB_SHEET_CONTENT_CLASS);

		add(tabsComponent, contentLayout);

		setOrientation(Orientation.Horizontal);
	}
	
	public void setOrientation(Orientation o) {
	    if(o != orientation) {
    	   this.orientation = o;
    	    updateOrientation();
	    }
	}
	
	/**
	 * Move all components to a proper top level layout based on orientation
	 */
	protected void updateOrientation() {
        tabsLayout.setFlexDirection(this.orientation == Orientation.Horizontal ? FlexDirection.ROW : FlexDirection.COLUMN);
        this.setFlexDirection(this.orientation == Orientation.Horizontal ? FlexDirection.COLUMN : FlexDirection.ROW);
        basicTabs.forEach(bt -> bt.setOrientation(orientation));
        tabsLayout.getElement().setAttribute("orientation", this.orientation.name().toLowerCase());
    }
	
	 protected void handleTabClick(ClickEvent<BasicTab> click) {
	     
	     BasicTabSheetSelectedChangeEvent e = new BasicTabSheetSelectedChangeEvent(click,selectedTab);
	     
	     LOGGER.debug("handleSelectedChangeEvent: {}",e);
	        
	     
        BasicTab previousTab = e.getPreviousTab();
        BasicTab selectedTab = e.getSelectedTab();
        
        LOGGER.trace("from: {} to: {}",previousTab,selectedTab);
        
        if(previousTab != null) {
             //if new tab is just a button then dont fire an event to the previous tab
             if(selectedTab != null && selectedTab.hasContent()) {
                 (previousTab).selectionChangeEvent(e);
             }
         }

         if(selectedTab != null) {
        	 
        	 selectedTab.selectionChangeEvent(e);
        	 
             //only change the displayed content if the new tab has content to display
             if(!e.isPostponed() && selectedTab.hasContent()) {
                setSelectedTab(selectedTab);
             }
             else {
                //undo tab change
                 //FIXME TODO
                LOGGER.trace("undoing tab change");
                if(previousTab==null) setSelectedTab(null);
                else {
                     //int index = tabs.indexOf(event.getPreviousTab());
                     LOGGER.trace("selecting previous tab: {}",previousTab);
                     //tabs.setSelectedIndex(index);
                     setSelectedTab(previousTab);
                }
             }
         }
         
         e.setHandled(true);
	         
        //setSelectedTab(click.getSource());
     }
	
  

	/**
	 * Sets the visibility of the tabs content
	 * @param bt
	 * @param visible
	 */
	protected void setContentVisible(BasicTab bt, Boolean visible) {
		if(visible && !bt.getLoaded()) {
			//call content supplier if the content needs to be visible and hasnt been loaded yet
			Component content = bt.getContentSupplier().apply(bt);
			bt.loaded = true;
			//content can be null because the supplier may have already added it to the component tree
			if(content != null) {
				bt.content = content;
				//only add to component tree if it isnt already present somewhere
				if(content.getParent().orElse(null) == null) {
					contentLayout.add(content);
				}
			}
		}
		Component content = bt.getContent();
		if(content!=null) {
			if(lazyRender) content.setVisible(visible);
			if(content instanceof HasStyle) {
				 HasStyle hs = (HasStyle) content;
				 if(visible) hs.removeClassName(INVISIBLE_CLASS);
				 else hs.addClassName(INVISIBLE_CLASS);
			} 
		}
	}
	

	public void replaceTab(BasicTab tab, Component content) {
		Component old = tab.getContent();
		tab.content = content;
		if(!tab.isSelected()) {
			setContentVisible(tab, false);
		}
		contentLayout.replace(old, content);
	}
	
	public void removeTab(BasicTab tab) {
	    Integer newIndex = null;
	    if(selectedTab.equals(tab)) {
	        newIndex = tabsLayout.indexOf(tab);
	        if(newIndex == tabsLayout.getComponentCount()-1) newIndex--;
        }
		tabsLayout.remove(tab);
		if(tab.getContent()!=null) {
			contentLayout.remove(tab.getContent());
		}
		if(newIndex != null) {
		    BasicTab newSelected = (BasicTab) tabsLayout.getComponentAt(newIndex);
		    setSelectedTab(newSelected);
		}
	}

	public BasicTab addTab(String tab, Component content) {
		return addTab(tab,null,content,null);
	}
	
	public BasicTab addTab(String tab, Icon icon,Component content) {
        return addTab(tab,icon,content,null);
    }
	
	public BasicTab addTab(String tab, Icon icon, Component content,Integer i) {
		return addTab(new Span(tab), icon, content, i);
	}
	
	public BasicTab addTab(Component label, Component content) {
		return addTab(label,null, content,null);
	}

	public BasicTab addTab(Component label, Icon icon, Component content,Integer i) {
		return addTab(label, icon, null, content, i);
	}
	
	public BasicTab addTab(Component label, Icon icon, Function<BasicTab,Component> contentSupplier, Component content,Integer i) {
		return addTab(new BasicTab(label,icon,content,contentSupplier), i);
	}

	public BasicTab addTab(BasicTab tab) {
		return addTab(tab,(Integer)null);
	}
	
	//FIXME this probably doesnt work after dragging since the list doesnt stay up to date
	public BasicTab addTab(BasicTab tab,Integer index) {
		tab.setTabSheet(this);
		tab.addClickListener(this::handleTabClick);
		tab.setOrientation(orientation);
		if(index != null) {
			tabsLayout.addComponentAtIndex(index, tab);
			basicTabs.add(index, tab);
		}
		else {
			tabsLayout.add(tab);
			basicTabs.add(tab);
		}
		if(!tab.isSelected()) {
			setContentVisible(tab, false);
		}
		if(tab.getContent()!=null) {
			if(index != null) contentLayout.addComponentAtIndex(index, tab.getContent());
			else contentLayout.add(tab.getContent());
		}
		if(selectedTab == null && tab.getContent() != null) setSelectedTab(tab);
		return tab;
	}

	public Optional<BasicTab> getTab(Component content) {
		return basicTabs.stream().filter(bt -> bt.getContent() == content).findFirst();
	}
	
	public VerticalLayout getContentLayout() {
		return contentLayout;
	}

	public void setContentLayout(VerticalLayout contents) {
		this.contentLayout = contents;
	}
	
    public void setSelectedIndex(int selectedIndex) {
        setSelectedTab(basicTabs.get(selectedIndex));
    }
    
 
    public void setSelectedTab(BasicTab select) {
        
        if(this.selectedTab != select) {
            if(this.selectedTab != null) {
                this.selectedTab.setSelected(false);
                setContentVisible(selectedTab, false);
            }
            this.selectedTab = select;
            if(select != null) {
                select.setSelected(true);
                setContentVisible(select, true);
            }
        }

   }

    public void addThemeVariants(TabsVariant... vs) {
        for(TabsVariant v : vs) addThemeName(v.getVariantName());
    }
    
    public void removeThemeVariants(TabsVariant... vs) {
        for(TabsVariant v : vs) removeThemeName(v.getVariantName());
    }
    
    public Tab getSelectedTab() {
    	return selectedTab;
    }

	public FlexLayout getTabsLayout() {
		return tabsLayout;
	}

	public List<BasicTab> getBasicTabs(){
		return basicTabs;
	}
	
	
    
    public static String getDragHandleClass() {
        return DRAG_HANDLE_CLASS;
    }

    public boolean isDraggable() {
        return tabsComponent instanceof SortableLayout;
    }
    
    public void setDraggable(boolean draggable) {

        if(draggable != isDraggable()) {
            if(draggable) {
                int index = this.indexOf(tabsComponent);
                SortableLayout rowsSortableLayout = new SortableLayout(tabsLayout, rowsSortableConfig, rowsSortableGroupStore);
                rowsSortableLayout.setHandle(DRAG_HANDLE_CLASS);
                this.addComponentAtIndex(index, rowsSortableLayout);
                tabsComponent = rowsSortableLayout;
                
                
               // rowsSortableLayout.addSortableComponentReorderListener(reorder ->  {
                    //moveSectionTo(tabMap.get(reorder.getComponent()), sectionsLayout.getTabsLayout().indexOf(reorder.getComponent()))
                    //TODO keep track of order
                    //}
                //);
            
                
            }
        }
    }

    public SortableLayout getDragLayout() {
        return (SortableLayout) tabsComponent;
    }

	public void setLazyRender(Boolean lazyRender) {
		this.lazyRender = lazyRender;
	}
    
    
}
