package org.thshsh.vaadin.press;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;

@SuppressWarnings("serial")
@CssImport(value = "press-button-group.css")
@CssImport(value = "press-button-group-vaadin-checkbox.css",themeFor = "vaadin-checkbox")
public class PressButtonGroup<T> extends CheckboxGroup<T> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PressButtonGroup.class);
	
	public static final String CSS_CLASS = "press-button";
	
	public PressButtonGroup() {
		super();
		addClassName(CSS_CLASS);
		setRenderer(new ComponentRenderer<com.vaadin.flow.component.Component, T>((f) -> {
			return new Button(getItemLabelGenerator().apply(f));
		}));
		
	}
	
	

	  public PressButtonGroup(String label) {
		super(label);
		addClassName(CSS_CLASS);
		setRenderer(new ComponentRenderer<com.vaadin.flow.component.Component, T>((f) -> {
			return new Button(getItemLabelGenerator().apply(f));
		}));
	}

	public Stream<Checkbox> getCheckboxItems() {
	        return getChildren().filter(Checkbox.class::isInstance)
	                .map(child -> (Checkbox) child);
	 }

	@SuppressWarnings("deprecation")
	@Override
	    public void setDataProvider(DataProvider<T, ?> dataProvider) {
		  super.setDataProvider(dataProvider);

		  getCheckboxItems().forEach(cb -> {
			  ((Checkbox)cb).addClassName(CSS_CLASS);
		  });
		  
			/* 
			   this.dataProvider.set(dataProvider);
			   DataViewUtils.removeComponentFilterAndSortComparator(this);
			   reset();
			
			   if (dataProviderListenerRegistration != null) {
			       dataProviderListenerRegistration.remove();
			   }
			   dataProviderListenerRegistration = dataProvider
			           .addDataProviderListener(event -> {
			               if (event instanceof DataChangeEvent.DataRefreshEvent) {
			                   T otherItem = ((DataChangeEvent.DataRefreshEvent<T>) event)
			                           .getItem();
			                   this.getCheckboxItems()
			                           .filter(item -> Objects.equals(
			                                   getItemId(item.item),
			                                   getItemId(otherItem)))
			                           .findFirst().ifPresent(this::updateCheckbox);
			               } else {
			                   reset();
			               }
			           });*/
	    }
	
}
