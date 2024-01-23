package org.thshsh.vaadin.form;

import java.util.Collection;

import org.thshsh.vaadin.nested.DetailsVerticalLayout;
import org.thshsh.vaadin.nested.LayoutBuilder;
import org.thshsh.vaadin.nested.LayoutBuilderLayout;
import org.thshsh.vaadin.tabsheet.BasicTabSheet;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.accordion.Accordion;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormLayoutBuilder extends LayoutBuilder {

	private static final long serialVersionUID = 1798466837020730614L;

	public FormLayoutBuilder(FormLayout nestedOrderedLayout) {
		super(nestedOrderedLayout);
	}

	public FormLayoutBuilder(LayoutBuilderLayout nestedLayout, Component currentComponent, Component currentSection) {
		super(nestedLayout, currentComponent, currentSection);
	}

	@Override
	public BasicTabSheet startBasicTabSheet(String name) {
		BasicTabSheet bts = super.startBasicTabSheet(name);
		addSectionable(bts);
		return bts;
	}

	@Override
	public Accordion startAccordion(String name) {
		Accordion acc = super.startAccordion(name);
		addSectionable(acc);
		return acc;
	}
	
	@Override
	public DetailsVerticalLayout startDetails(String name) {
		DetailsVerticalLayout d = super.startDetails(name);
		addSectionable(d);
		return d;
	}
	
	
	
	

	@Override
	public void add(Collection<Component> components) {
		super.add(components);
		LOGGER.info("add: {} currentSection: {}",components,currentSection);
		if(currentSection  != null) {
			//we are under a tab
			for(Component c : components) {
				if(c instanceof HasValue) {
					//adding a new field under a tab
					LOGGER.debug("adding field: {} under section: {}",c,currentSection);
					//((FormLayout)mainLayout).fieldSectionMap.put(c, currentSection);
					((FormLayout)mainLayout).addToSectionMap((HasValue<?, ?>) c, currentSection);
				}
			}
		}

	}
	
	protected void addSectionable(Component comp) {
		LOGGER.debug("addSectionable: {}",comp);
		/*if(currentSection != null) {
			((FormLayout)nestedLayout).componentSectionMap.put(comp, currentSection);
		}*/
		((FormLayout)mainLayout).sectionableComponents.add(comp);
	}
	
	
	

	@Override
	public FormLayout getLayout() {
		return (FormLayout) super.getLayout();
	}

	public FormLayoutBuilder clone() {
		return new FormLayoutBuilder(mainLayout, currentComponent, currentSection);
	}
}
