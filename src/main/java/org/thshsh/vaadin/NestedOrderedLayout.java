package org.thshsh.vaadin;


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thshsh.vaadin.tabsheet.BasicTab;
import org.thshsh.vaadin.tabsheet.BasicTabSheet;

import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Layout that keeps track of a state hierarchy such that one layout is always the current layout and
 * new layouts and be started/ended. This makes creating complex layouts easier as there is no need to 
 * track variables for intermediate layouts. This class also keeps track of tabsheets so that we can identify 
 * which tab a child layout is part of. This feature is necessary for sub classes like {@link org.thshsh.vaadin.form.FormLayout} which
 * need to switch tabs dynamically
 *
 * @param <T>
 */
@SuppressWarnings({"serial","unchecked"})
public class NestedOrderedLayout extends VerticalLayout {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(NestedOrderedLayout.class);
	
	//protected LinkedList<T> hierarchy = new LinkedList<>();
	protected Map<HasComponents,HasComponents> layoutParentMap = new HashMap<>();
	protected HasComponents currentLayout;
	
	protected BasicTab currentTab;
	protected Map<HasComponents,BasicTab> layoutTabMap = new HashMap<>();
	
	public NestedOrderedLayout() {
		super();
		this.addClassName("nested-ordered-layout");
		//hierarchy.add((T) this);
		layoutParentMap.put(this, null);
		currentLayout =  this;
		//currentLayout = new VerticalLayout();
	}
	
	public <T extends Component & HasComponents> void addLayout(T layout) {
		currentLayout.add(layout);
		pushLayout(layout);
	}
	
	/**
	 * Pushes layout onto list but doesnt add to component tree
	 * @param layout
	 */
	public void pushLayout(HasComponents layout) {
		//hierarchy.add(layout);
		layoutParentMap.put(layout, currentLayout);
		if(currentTab != null) layoutTabMap.put(layout, currentTab);
		currentLayout = layout;
	}
	
	public HasComponents poll() {
		//T polled = hierarchy.pollLast();
		//currentLayout = hierarchy.getLast();
		HasComponents polled = currentLayout;
		currentLayout = layoutParentMap.get(currentLayout);
		return polled;
	}
	
	public HorizontalLayout startHorizontalLayout() {
		HorizontalLayout newLayout = createHorizontalLayout();
		addLayout(newLayout);
		return newLayout;
	}
	
	public VerticalLayout startVerticalLayout() {
		VerticalLayout newLayout = createVerticalLayout();
		addLayout(newLayout);
		return newLayout;
	}
	
	public Details startDetails() {
		return startDetails((Component)null);
	}
	
	public Details startDetails(String summary) {
		return startDetails(new Span(summary));
	}
	
	public Details startDetails(Component summary) {
		Details details = new Details(summary);
		VerticalLayout detailsContent = createVerticalLayout();
		details.setContent(detailsContent);
		pushLayout(detailsContent);
		return details;
	}
	
	public BasicTabSheet startBasicTabSheet() {
		BasicTabSheet newLayout = createBasicTabSheet();
		addLayout(newLayout);
		return newLayout;
	}

	public BasicTab startTab(String name,HasComponents newLayout) {
		return startTab(name, null,newLayout,null);
	}
	
	public BasicTab startTab(String name,Icon icon,HasComponents newLayout) {
		return startTab(name, icon,newLayout,null);
	}
	
	public BasicTab startTab(String name,Icon i,HasComponents newLayout,Integer index) {
		if(!(currentLayout instanceof BasicTabSheet)) throw new IllegalArgumentException("Current Layout is not a TabSheet");
		BasicTabSheet tabSheet = (BasicTabSheet) currentLayout;
		currentTab = tabSheet.addTab(name, (Component) newLayout,index);
		if(newLayout != null) pushLayout(newLayout);
		return currentTab;
	}
	
	public BasicTab startVerticalLayoutTab(String name) {
		return startVerticalLayoutTab(name,null,(Integer)null);
	}
	
	public BasicTab startVerticalLayoutTab(String name,Icon i,Integer index) {
		return startTab(name,i,createVerticalLayout(),index);
	}
	
	public BasicTabSheet createBasicTabSheet() {
		BasicTabSheet newLayout = new BasicTabSheet();
		newLayout.setMargin(false);
		newLayout.setPadding(false);
		return newLayout;
	}
	
	public HorizontalLayout createHorizontalLayout() {
		HorizontalLayout newLayout = new HorizontalLayout();
		newLayout.setMargin(false);
		newLayout.setPadding(false);
		return newLayout;
	}
	
	public VerticalLayout createVerticalLayout() {
		VerticalLayout newLayout = new VerticalLayout();
		newLayout.setMargin(false);
		newLayout.setPadding(false);
		return newLayout;
	}
	
	public void endLayout() {
		HasComponents polled = poll();
		//if we ended the tab content layout then null the currentTab value
		if(currentTab != null && currentTab.getContent() == polled) currentTab = null;
	}
	
	@Override
	public void add(Component... cs) {
		for(Component c : cs) add(c);
	}


	@SuppressWarnings("rawtypes")
	public void add(Component c) {
		if(currentLayout == this) super.add(c);
		else currentLayout.add(c);
		if(c instanceof FocusNotifier && c instanceof HasStyle) {
			FocusNotifier fn = (FocusNotifier) c;
			fn.addFocusListener(focus -> {
				//LOGGER.info("focus");
				((HasStyle)c).addClassName("focus");
			});
		}
		if(c instanceof BlurNotifier && c instanceof HasStyle) {
			BlurNotifier fn = (BlurNotifier) c;
			fn.addBlurListener(blur -> {
				//c.removeStyleName("focus");
				((HasStyle)c).removeClassName("focus");
			});
		}
	
	}

	public HasComponents getCurrentLayout() {
		return currentLayout;
	}
	
	public void setCurrentLayout(HasComponents t) {
		if(!layoutParentMap.containsKey(t) && t != this) throw new IllegalArgumentException("Layout not present in hierarchy");
		this.currentLayout = t;
		this.currentTab = layoutTabMap.get(t);
	}
	
	

}
