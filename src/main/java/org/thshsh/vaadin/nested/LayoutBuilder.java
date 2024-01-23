package org.thshsh.vaadin.nested;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.thshsh.vaadin.tabsheet.BasicTab;
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
import com.vaadin.flow.dom.Element;

import lombok.extern.slf4j.Slf4j;

/**
 * Keeps a pointer to a current component in a hierarchy of components managed by the {@link org.thshsh.vaadin.nested.LayoutBuilderLayout}
 */
@Slf4j
public class LayoutBuilder implements HasComponents {


	private static final long serialVersionUID = -7096103580482128650L;
	
	protected List<Class<? extends Component>> sectionableComponentClasses = List.of(BasicTabSheet.class,Accordion.class,DetailsVerticalLayout.class);
	
	protected LayoutBuilderLayout mainLayout;
	protected Component currentComponent;
	protected Component currentSection;
	protected Boolean inSectionable = false;
	
	public LayoutBuilder() {}
	
	public LayoutBuilder(LayoutBuilderLayout nestedOrderedLayout) {
		mainLayout = nestedOrderedLayout;
		currentComponent = mainLayout;
	}
	
	
	public LayoutBuilder(LayoutBuilderLayout nestedLayout, Component currentComponent, Component currentSection) {
		this.mainLayout = nestedLayout;
		this.currentComponent = currentComponent;
		this.currentSection = currentSection;
	}

	
	public LayoutBuilderLayout getNestedOrderedLayout() {
		return mainLayout;
	}
	

	public <T extends Component> T pushComponent(T newComponent,String name) {
		LOGGER.debug("addLayout: {} name: {}",newComponent,name);
		if(currentComponent instanceof HasComponents) {
			((HasComponents)currentComponent).add(newComponent);
		}
		addComponentInternal(newComponent,name);
		this.currentComponent = newComponent;
		inSectionable = isSectionableComponent(currentComponent);
		return newComponent;
	}
	
	
	
	

	public LayoutBuilderLayout getLayout() {
		return mainLayout;
	}

	public void setMainLayout(LayoutBuilderLayout nestedLayout) {
		this.mainLayout = nestedLayout;
	}

	
	public HorizontalLayout startHorizontalLayout() {
		return startHorizontalLayout(null);
	}
	
	
	public HorizontalLayout startHorizontalLayout(String name) {
		HorizontalLayout newLayout = createHorizontalLayout();
		pushComponent(newLayout,name);
		return newLayout;
	}
	
	
	public VerticalLayout startVerticalLayout() {
		return startVerticalLayout(null);
	}
	
	
	public VerticalLayout startVerticalLayout(String name) {
		VerticalLayout newLayout = createVerticalLayout();
		pushComponent(newLayout,name);
		return newLayout;
	}
	

	public Accordion startAccordion() {
		return startAccordion(null);
	}

	public Accordion startAccordion(String name) {
		Accordion accordion = createAccordion();
		pushComponent(accordion,name);
		return accordion;
	}
	

	public DetailsVerticalLayout startDetails() {
		return startDetails(null);
	}
	
	public DetailsVerticalLayout startDetails(String name) {
		DetailsVerticalLayout d = createDetailsLayout();
		pushComponent(d,name);
		return d;
	}
	
	public BasicTabSheet startBasicTabSheet() {
		return startBasicTabSheet(null);
	}
	
	public BasicTabSheet startBasicTabSheet(String name) {
		BasicTabSheet newLayout = createBasicTabSheet();
		pushComponent(newLayout,name);
		return newLayout;
	}
	
	public Component createSummaryComponent(String text) {
		if(text == null) return new Span();
		else return new Span(text);
	}

	public Component startSection(Integer index,String name,String text,Component newLayout) {
		return startSection(index, name,createSummaryComponent(text),newLayout);
	}
	
	public Component startSection(Integer index,String name,Component sumComponent,Component newLayout) {
		return startSection(index,name,sumComponent,newLayout,(Consumer<LayoutBuilder>) null);
	}

	public Component startSection(Integer index,String name,Component sumComponent,Component newLayout, Consumer<LayoutBuilder> contentCreator) {
		return startSection(index, name, sumComponent, newLayout, contentCreator != null ? builder -> {
			contentCreator.accept(builder);
			return null;
		} : null);
	}
	
	public Component startSection(Integer index,String name,Component sumComponent,Component sectionContent, Function<LayoutBuilder, Component> contentFunction) {
		
		//NOTE both the content and contentCreator must be passed to this method, otherwise the LayoutBuilder clone does not have a component to use as its current
		
		if(inSectionable) {

			if(sumComponent == null) sumComponent = new Span();
		
			Component newSection;
			
			//TODO make this work with accordion and details as well
			LayoutBuilder lazyBuilder = this.clone();
			lazyBuilder.currentComponent = sectionContent;
			
			if(currentComponent instanceof BasicTabSheet) {
				BasicTabSheet tabSheet = (BasicTabSheet) currentComponent;
				Function<BasicTab,Component> asyncSupplier = null;
				if(contentFunction != null) {
					asyncSupplier = (tab) -> {
						lazyBuilder.currentSection = tab; 
						return contentFunction.apply(lazyBuilder);
					};
				}
				newSection = tabSheet.addTab(sumComponent, null, asyncSupplier,(Component) sectionContent,index);
			}
			else if(currentComponent instanceof Accordion) {
				Accordion ac = (Accordion) currentComponent;
				AccordionPanel panel;
				panel = ac.add(null,sectionContent);
				panel.setSummary(sumComponent);
				newSection = panel;
			}
			else if(currentComponent instanceof DetailsVerticalLayout) {
				DetailsVerticalLayout details = (DetailsVerticalLayout) currentComponent;
				newSection = new Details(sumComponent, sectionContent);
				details.add(newSection);
			}
			else throw new IllegalArgumentException("Current Layout cannot have sections");
			
			
			//mark new section as child of current section
			mainLayout.componentSectionMap.put(newSection, currentSection);
			//mark new section as child of current component
			mainLayout.componentParentMap.put(newSection, currentComponent);
			
			currentSection = newSection;
			lazyBuilder.currentSection = newSection;
			if(sectionContent != null) {
				addComponentInternal(sectionContent,name);
				currentComponent = sectionContent;
			}
			return newSection;
		
		}
		else throw new IllegalArgumentException("Current Layout cannot have sections");
		
	}
	

	
	public Component startVerticalLayoutSection(String text) {
		return startVerticalLayoutSection(text, null);
	}
	
	public Component startVerticalLayoutSection(String text,String name) {
		return startVerticalLayoutSection(text,(Integer)null,name);
	}
	
	public Component startVerticalLayoutSection(String text,Integer index,String name) {
		return startSection(index,name,text,createVerticalLayout());
	}
	
	public Component startHorizontalLayoutSection(String text) {
		return startHorizontalLayoutSection(text, null);
	}
	
	public Component startHorizontalLayoutSection(String text,String name) {
		return startHorizontalLayoutSection(text,(Integer)null,name);
	}
	
	public Component startHorizontalLayoutSection(String text,Integer index,String name) {
		return startSection(index,name,text,createHorizontalLayout());
	}
	
	
	public Component startSection(Component text,Component newLayout) {
		return startSection(null, null,text,newLayout);
	}
	
	
	public Component startSection(String text,Component newLayout) {
		return startSection(text, newLayout,null);
	}
	
	
	public Component startSection(String text,Component newLayout,String name) {
		return startSection(null,name,text,newLayout);
	}
	
	/**
	 * End the current section regardless of where we are in the hierarchy
	 */
	
	
	public void endSection() {
		if(currentSection == null) throw new IllegalStateException("No current section");
	    Component sectionParent = mainLayout.componentParentMap.get(currentSection);
	    setCurrentComponent(sectionParent);
		
	}
	
	/**
	 * Ends layouts and sections
	 */
	
	public void endComponent() {
		if(currentComponent == mainLayout) throw new IllegalStateException("Cannot end root component");
		Component parentComponent = mainLayout.componentParentMap.get(currentComponent);
		setCurrentComponent(parentComponent);
	}
	
	
	public void endComponent(String name) {
		if(!mainLayout.componentNameMap.containsKey(name)) throw new IllegalArgumentException("Named Component Not Found: "+name);
		Component comp = mainLayout.componentNameMap.get(name);
		Component parent = mainLayout.componentParentMap.get(comp);
		setCurrentComponent(parent);
	}
	
	
	public void endComponent(Component component) {
	    if(!mainLayout.componentParentMap.containsKey(component) && component != mainLayout) throw new IllegalArgumentException("Component not present in hierarchy");
        Component parent = mainLayout.componentParentMap.get(component);
        setCurrentComponent(parent);
    }
	
	
	public void setCurrentComponent(String name) {
		if(!mainLayout.componentNameMap.containsKey(name)) throw new IllegalArgumentException("Named Component Not Found: "+name);
		Component comp = mainLayout.componentNameMap.get(name);
		setCurrentComponent(comp);
	}
	
	
	public void setCurrentComponent(Component t) {
		if(!mainLayout.componentParentMap.containsKey(t) && t != mainLayout) throw new IllegalArgumentException("Component not present in hierarchy");
		this.currentComponent = t;
		inSectionable = isSectionableComponent(currentComponent);
		this.currentSection = mainLayout.componentSectionMap.get(t);
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

	
	public Component getCurrentComponent() {
		return currentComponent;
	}

	
	public Component getCurrentSection() {
		return currentSection;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void add(Collection<Component> components) {
		

		if (currentComponent instanceof HasComponents) {
			((HasComponents) currentComponent).add(components);
		}
		
		for(Component c : components) {

			if (c instanceof FocusNotifier && c instanceof HasStyle) {
				FocusNotifier fn = (FocusNotifier) c;
				fn.addFocusListener(focus -> {
					((HasStyle) c).addClassName("focus");
				});
			}
			
			if (c instanceof BlurNotifier && c instanceof HasStyle) {
				BlurNotifier fn = (BlurNotifier) c;
				fn.addBlurListener(blur -> {
					((HasStyle) c).removeClassName("focus");
				});
			}

		}
	}
	/*	
		@Override
		public void add(Component... cs) {
			for (Component c : cs)
				add(c);
		}*/
	
	protected Boolean isSectionableComponent(Component component) {
		for(Class<? extends Component> c : sectionableComponentClasses) {
			if(c.isAssignableFrom(component.getClass())) return true;
		}
		return false;
	}
	
	/**
	 * Pushes layout onto hierarchy but DOES NOT add to component tree
	 * 
	 * @param layout
	 */
	protected void addComponentInternal(Component layout, String name) {
		LOGGER.debug("pushLayout: {}", layout);

		mainLayout.componentParentMap.put(layout, currentComponent);
		if (name != null) {
			mainLayout.componentNameMap.put(name, layout);
			if (layout instanceof HasStyle) {
				HasStyle hs = (HasStyle) layout;
				hs.addClassName("nested-ordered-layout-" + name);
			}
		}
		if (currentSection != null) {
			LOGGER.debug("setting section parent: {}", currentSection);
			mainLayout.componentSectionMap.put(layout, currentSection);
		}
	}
	
	/**
	 * TODO are these implemented correctly?
	 */

	@Override
	public Element getElement() {
		return currentComponent.getElement();
	}


	@Override
	public void remove(Collection<Component> components) {
		if(currentComponent instanceof HasComponents) {
			((HasComponents)currentComponent).remove(components);
		}
	}

	@Override
	public void removeAll() {
		if(currentComponent instanceof HasComponents) {
			((HasComponents)currentComponent).removeAll();
		}
	}
	
	public LayoutBuilder clone() {
		return new LayoutBuilder(mainLayout, currentComponent, currentSection);
	}
}