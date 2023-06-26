package org.thshsh.vaadin.nested;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thshsh.vaadin.tabsheet.BasicTabSheet;

import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
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
	protected Map<Component,Component> componentParentMap = new HashMap<>();
	protected BidiMap<String,Component> componentNameMap = new DualHashBidiMap<>();
	protected Component currentComponent;
	
	
	
	//protected SelectableParent currentSelectableParent;
	//keeps track of the current tab or accordion panel
	protected Component currentSection;
	//keeps track of the parent accordion panel or tab for children
	protected Map<Component,Component> componentSectionMap = new HashMap<>();
	//keeps track of section parents
	protected Map<Component,Component> sectionParentMap = new HashMap<>();
	
	public NestedOrderedLayout() {
		super();
		this.addClassName("nested-ordered-layout");
		componentParentMap.put(this, null);
		currentComponent =  this;
	}
	
	public <T extends Component> T addLayout(T layout,String name) {
		if(currentComponent instanceof HasComponents) ((HasComponents)currentComponent).add(layout);
		pushLayout(layout,name);
		return layout;
	}
	
	
	/**
	 * Pushes layout onto hieratchy but DOES NOT add to component tree
	 * @param layout
	 */
	protected void pushLayout(Component layout, String name) {
		LOGGER.debug("pushLayout: {}",layout);
		componentParentMap.put(layout, currentComponent);
		if(name != null) {
			componentNameMap.put(name, layout);
			if(layout instanceof HasStyle) {
				HasStyle hs = (HasStyle) layout;
				hs.addClassName("nested-ordered-layout-"+name);
			}
		}
		if(currentSection != null) {
			LOGGER.debug("setting section parent: {}",currentSection);
			componentSectionMap.put(layout, currentSection);
		}
		currentComponent = layout;
	}
	
	/*protected Component poll() {
		Component polled = currentComponent;
		currentComponent = componentParentMap.get(currentComponent);
		return polled;
	}*/
	
	public HorizontalLayout startHorizontalLayout() {
		return startHorizontalLayout(null);
	}
	
	public HorizontalLayout startHorizontalLayout(String name) {
		HorizontalLayout newLayout = createHorizontalLayout();
		addLayout(newLayout,name);
		return newLayout;
	}
	
	public VerticalLayout startVerticalLayout() {
		return startVerticalLayout(null);
	}
	
	public VerticalLayout startVerticalLayout(String name) {
		VerticalLayout newLayout = createVerticalLayout();
		addLayout(newLayout,name);
		return newLayout;
	}
	
	public Accordion startAccordion() {
		return startAccordion(null);
	}
	
	public Accordion startAccordion(String name) {
		Accordion accordion = createAccordion();
		addLayout(accordion,name);
		return accordion;
	}
	
	public DetailsVerticalLayout startDetails() {
		return startDetails(null);
	}
	
	public DetailsVerticalLayout startDetails(String name) {
		DetailsVerticalLayout d = createDetailsLayout();
		addLayout(d,name);
		return d;
	}
	
	public BasicTabSheet startBasicTabSheet() {
		return startBasicTabSheet(null);
	}
	
	public BasicTabSheet startBasicTabSheet(String name) {
		BasicTabSheet newLayout = createBasicTabSheet();
		addLayout(newLayout,name);
		return newLayout;
	}

	public Component startSection(String text,Component newLayout,Integer index,String name) {
		return startSection(new Span(text), newLayout,index,name);
	}
	
	
	public Component startSection(Component sumComponent,Component newLayout,Integer index,String name) {
		if(sumComponent == null) sumComponent = new Span();
		
		Component newSection;
		
		if(currentComponent instanceof BasicTabSheet) {
			BasicTabSheet tabSheet = (BasicTabSheet) currentComponent;
			newSection = tabSheet.addTab(sumComponent, (Component) newLayout,index);
		}
		else if(currentComponent instanceof Accordion) {
			Accordion ac = (Accordion) currentComponent;
			AccordionPanel panel;
			panel = ac.add(null,newLayout);
			panel.setSummary(sumComponent);
			newSection = panel;

		}
		else if(currentComponent instanceof DetailsVerticalLayout) {
			DetailsVerticalLayout details = (DetailsVerticalLayout) currentComponent;
			newSection = new Details(sumComponent, newLayout);
			details.add(newSection);
		}
		else throw new IllegalArgumentException("Current Layout cannot have sections");
		
		componentSectionMap.put(newSection, currentSection);
		sectionParentMap.put(newSection, currentComponent);
		currentSection = newSection;
		if(newLayout != null) pushLayout(newLayout,name);
		return newSection;
		
	}
	
	public Component startVerticalLayoutSection(String text) {
		return startVerticalLayoutSection(text, null);
	}
	
	public Component startVerticalLayoutSection(String text,String name) {
		return startVerticalLayoutSection(text,(Integer)null,name);
	}
	
	public Component startVerticalLayoutSection(String text,Integer index,String name) {
		return startSection(text,createVerticalLayout(),index,name);
	}
	
	public Component startHorizontalLayoutSection(String text) {
		return startHorizontalLayoutSection(text, null);
	}
	
	public Component startHorizontalLayoutSection(String text,String name) {
		return startHorizontalLayoutSection(text,(Integer)null,name);
	}
	
	public Component startHorizontalLayoutSection(String text,Integer index,String name) {
		return startSection(text,createHorizontalLayout(),index,name);
	}
	
	public Component startSection(Component text,Component newLayout) {
		return startSection(text, newLayout,null,null);
	}
	
	public Component startSection(String text,Component newLayout) {
		return startSection(text, newLayout,null);
	}
	
	public Component startSection(String text,Component newLayout,String name) {
		return startSection(text,newLayout,null,name);
	}
	
	/**
	 * End the current section regardless of where we are in the hierarchy
	 */
	
	public void endSection() {
		if(currentSection == null) throw new IllegalStateException("No current section");
	    Component sectionParent = sectionParentMap.get(currentSection);
	    setCurrentComponent(sectionParent);
		
	}
	
	/**
	 * Ends layouts and sections
	 */
	public void endComponent() {
		if(currentComponent == this) throw new IllegalStateException("Cannot end root component");
		Component parentComponent = componentParentMap.get(currentComponent);
		setCurrentComponent(parentComponent);
	}
	
	public void endComponent(String name) {
		if(!componentNameMap.containsKey(name)) throw new IllegalArgumentException("Named Component Not Found: "+name);
		Component comp = componentNameMap.get(name);
		Component parent = componentParentMap.get(comp);
		setCurrentComponent(parent);
	}
	
	public void setCurrentComponent(String name) {
		if(!componentNameMap.containsKey(name)) throw new IllegalArgumentException("Named Component Not Found: "+name);
		Component comp = componentNameMap.get(name);
		setCurrentComponent(comp);
	}
	
	public void setCurrentComponent(Component t) {
		if(!componentParentMap.containsKey(t) && t != this) throw new IllegalArgumentException("Component not present in hierarchy");
		this.currentComponent = t;
		this.currentSection = componentSectionMap.get(t);
	}

	public BasicTabSheet createBasicTabSheet() {
		BasicTabSheet newLayout = new BasicTabSheet();
		newLayout.setMargin(false);
		newLayout.setPadding(false);
		return newLayout;
	}
	
	public Accordion createAccordion() {
		Accordion newLayout = new Accordion();
		return newLayout;
	}
	
	public DetailsVerticalLayout createDetailsLayout() {
		DetailsVerticalLayout newLayout = new DetailsVerticalLayout();
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
	
	
	
	/*public Component getCurrentSectionContent() {
		if(currentSection == null) return null;
		else if(currentSection instanceof BasicTab) {
			return ((BasicTab)currentSection).getContent();
		}
		else if(currentSection instanceof Details) {
			return currentSection;
		}
		else throw new IllegalArgumentException("Current Section class not handled: "+currentSection.getClass());
	}*/
	
	@Override
	public void add(Component... cs) {
		for(Component c : cs) add(c);
	}


	@SuppressWarnings("rawtypes")
	public void add(Component c) {
		if(currentComponent == this) super.add(c);
		else if(currentComponent instanceof HasComponents) ((HasComponents)currentComponent).add(c);
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

	public Component getCurrentComponent() {
		return currentComponent;
	}
	
	
	
	

}
