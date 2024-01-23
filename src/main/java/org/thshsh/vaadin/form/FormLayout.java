package org.thshsh.vaadin.form;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thshsh.vaadin.nested.DetailsVerticalLayout;
import org.thshsh.vaadin.nested.LayoutBuilderLayout;
import org.thshsh.vaadin.tabsheet.BasicTab;
import org.thshsh.vaadin.tabsheet.BasicTabSheet;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Binder.Binding;
import com.vaadin.flow.data.binder.BinderValidationStatusHandler;
import com.vaadin.flow.data.binder.ValidationException;

import lombok.Getter;

/**
 * An extension of the {@link org.thshsh.vaadin.nested.LayoutBuilderLayout} class that keeps track of form fields so that after validation
 * we can show the correct tab or section
 *
 */
@SuppressWarnings("serial")
public class FormLayout extends LayoutBuilderLayout {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FormLayout.class);

	//holds a mapping of fields and expandable components (tabs/accordions)
	//protected Map<Object,Component> fieldSectionMap = new HashMap<>();
	protected Map<Component,Component> errorComponentMap = new HashMap<>();
	@Getter
	protected Binder<?> binder;
	
	protected Set<HasValue<?, ?>> fieldsWithErrors = new HashSet<>();
	protected Set<Component> sectionableComponents = new HashSet<>();
	
	protected String ERROR_CSS_CLASS = "form-error";
	
	public <T> FormLayout(Binder<T> binder) {
		super();
		//this.defaultBuilder = new FormLayoutBuilder(this);
		this.addClassName("form-layout");
		this.binder = binder;
		BinderValidationStatusHandler<T> defaultValidationHandler = binder.getValidationStatusHandler();
		
		binder.setValidationStatusHandler(
            statusChange -> {
                defaultValidationHandler.statusChange(statusChange);
                	
                //remove any fields that are no longer bound
                fieldsWithErrors.removeIf(field -> binder.getFields().filter(f -> f == field).findFirst().isEmpty());
                
                statusChange
			     .getFieldValidationStatuses()
			     .forEach(status -> {
			             if (status.isError()) fieldsWithErrors.add(status.getField());
			             else fieldsWithErrors.remove(status.getField());
			         }
			     );
                
                if(fieldsWithErrors.size()>0) LOGGER.trace("fieldsWithErrors: {}",fieldsWithErrors);

                Set<Component> tabsWithErrors = fieldsWithErrors
                		.stream()
                		.flatMap(field -> getParentSections((Component)field).orElseThrow()
                				.stream())
                		.collect(Collectors.toSet());
                
                if(tabsWithErrors.size()>0) LOGGER.trace("tabsWithErrors: {}",tabsWithErrors);

                //iterate through all tabs and set error icon
                sectionableComponents
                	.stream()
                	.flatMap(ts -> {
                		if(ts instanceof BasicTabSheet) return ((BasicTabSheet)ts).getBasicTabs().stream();
                		else if(ts instanceof Accordion) return ((Accordion)ts).getChildren();
                		else if(ts instanceof DetailsVerticalLayout) return ((DetailsVerticalLayout)ts).getChildren();
                		else throw new IllegalStateException("Cant handle tab of type: "+ts.getClass());
                	})
                	.forEach(tab -> setSectionError(tab, tabsWithErrors.contains(tab)));
                
            }
       );
	}

	
	public void addToSectionMap(HasValue<?,?> comp,Component tab) {
	    LOGGER.debug("addToSectionMap: {} parent: {}",comp,tab);
	    componentSectionMap.put((Component) comp, tab);
	}
	
	public <T> void addToSectionMap(Collection<Binding<T,?>> bindings,Component parent) {
        bindings.forEach(b -> {
            addToSectionMap(b.getField(), parent);
        });
    }
	
	


	public void handleValidationException(ValidationException ve) {		
		LOGGER.debug("handleValidationException: {}",ve.getFieldValidationErrors());
		if(!ve.getFieldValidationErrors().isEmpty()) {
			HasValue<?,?> field = ve.getFieldValidationErrors().get(0).getField();
			//LOGGER.debug("Error on field: {} - {}",field instanceof HasLabel ? ((HasLabel)field).getLabel() : "",field);
			Component comp = (Component) field;
			Component section = componentSectionMap.get(comp);
			LOGGER.info("field: {} section: {}",comp,section);
			selectSections(comp);
		}
	}
	
	/**
	 * Select all parent tabs of this component
	 * @param compnent
	 */
	protected void selectSections(Component compnent) {
		LOGGER.debug("selectSections: {}",compnent);
		
		Set<Component> parents = getParentSections(compnent).orElseThrow();
		
		LOGGER.debug("parents: {}",parents);
		
		parents.forEach(ex -> {
			if(ex instanceof BasicTab) {
				BasicTab tab = (BasicTab) ex;
				tab.getTabSheet().setSelectedTab(tab);
				//selectSections(tab.getTabSheet());
			}
			else if(ex instanceof Details) {
				Details details = (Details) ex;
				details.setOpened(true);
				//selectSections(details.getParent().get());
			}
		});
		
		/*if(!componentSectionMap.containsKey(compnent)) {
			//check for parent
			compnent = compnent.getParent().get();
			if(!componentSectionMap.containsKey(compnent)) {
				throw new IllegalArgumentException("map doesnt contain component or parent: "+compnent);
			}
		}
		
		
		
		
		Component ex = componentSectionMap.get(compnent);
		LOGGER.debug("section: {}",ex);
		if(ex instanceof BasicTab) {
			BasicTab tab = (BasicTab) ex;
			tab.getTabSheet().setSelectedTab(tab);
			selectSections(tab.getTabSheet());
		}
		else if(ex instanceof Details) {
			Details details = (Details) ex;
			details.setOpened(true);
			selectSections(details.getParent().get());
		}
		*/
	}
	
	protected Optional<Set<Component>> getParentSections(Component c){
		
		LOGGER.debug("getParentSections: {}",c);
		
		if(!componentSectionMap.containsKey(c)) {
			//check for parent
			c = c.getParent().get();
			if(!componentSectionMap.containsKey(c)) {
				//throw new IllegalArgumentException("map doesnt contain component or parent: "+c);
				return Optional.empty();
			}
		}
		
		Set<Component> parents = new HashSet<Component>();
		
		if(componentSectionMap.containsKey(c)) {
			Component section = componentSectionMap.get(c);
			LOGGER.debug("section: {}",section);
			parents.add(section);
			if(section instanceof Tab) {
				getParentSections(((BasicTab)section).getTabSheet()).ifPresent(parents::addAll);
				//parents.addAll(getParentSections(((BasicTab)section).getTabSheet()));
			}
			else if(section instanceof Details) {
				getParentSections(section.getParent().get()).ifPresent(parents::addAll);
				//parents.addAll(getParentSections(section.getParent().get()));
			}
		}
		
		LOGGER.debug("parents: {}",parents);
		
		return Optional.of(parents);
	}
	
	/**
	 * adds/removes an error icon on a tab
	 * must keep track of which tabs have the icon so that we dont set it twice on the same tab
	 * @param tab
	 * @param error
	 */
	protected void setSectionError(Component comp, Boolean error) {
		
		if(!error && errorComponentMap.containsKey(comp)) {
			LOGGER.trace("setSectionError: {} = {}",comp,error);
			
			//remove error icon
			errorComponentMap.get(comp)
				.getParent()
				.ifPresent(parent -> {
					((HasComponents)parent).remove(errorComponentMap.get(comp));
				}
			);
			//remove from map
			errorComponentMap.remove(comp);
			//remove class name
			if(comp instanceof BasicTab) {
				((BasicTab) comp).removeClassName(ERROR_CSS_CLASS);
			}
			else if(comp instanceof Details && ((Details)comp).getSummary() instanceof HasStyle) {
				((HasStyle)((Details)comp).getSummary()).removeClassName(ERROR_CSS_CLASS);
			}
		}
		else if(error && !errorComponentMap.containsKey(comp)) {
			
			LOGGER.trace("setSectionError: {} = {}",comp,error);
			
			//add error icon and class name
			if(comp instanceof BasicTab) {
				BasicTab tab = (BasicTab) comp;
				Icon errorIcon = VaadinIcon.EXCLAMATION.create();
				tab.add(errorIcon);
				tab.addClassName(ERROR_CSS_CLASS);
				errorComponentMap.put(tab, errorIcon);
			}
			else if(comp instanceof Details) {
				Details panel = (Details) comp;
				Icon errorIcon = VaadinIcon.EXCLAMATION.create();
				errorComponentMap.put(panel, errorIcon);
				Component summary = panel.getSummary();
				if(summary instanceof HasComponents) ((HasComponents)summary).add(errorIcon);
				if(summary instanceof HasStyle) ((HasStyle)summary).addClassName(ERROR_CSS_CLASS);
			}
		}		
	}
	
	/*public FormLayoutBuilder getDefaultBuilder() {
		return (FormLayoutBuilder) defaultBuilder;
	}*/
}
