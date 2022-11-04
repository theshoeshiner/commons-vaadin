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
@CssImport(value = "press-button-group-vaadin-checkbox.css", themeFor = "vaadin-checkbox")
public class PressButtonCheckboxGroup<T> extends CheckboxGroup<T> implements PressButtonGroup<Checkbox> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PressButtonCheckboxGroup.class);

	
	public static final String CSS_CLASS = "press-button";

	public PressButtonCheckboxGroup() {
		super();
		addThemeName(CSS_CLASS);
		resetRenderer();

	}

	public PressButtonCheckboxGroup(String label) {
		super(label);
		addThemeName(CSS_CLASS);
		resetRenderer();
	}

	protected void resetRenderer() {
		setRenderer(new ComponentRenderer<com.vaadin.flow.component.Component, T>((f) -> {
			Button b = new Button(getItemLabelGenerator().apply(f));
			b.addClassName(CSS_GROUP_CLASS);
			return b;
		}));
	}

	public Stream<Checkbox> getItems() {
		return getChildren()
				.filter(Checkbox.class::isInstance)
				.map(child -> (Checkbox) child);
	}

	/**
	 * We override this method because it ultimately gets called from all other
	 * setItems, etc methods
	 */
	@SuppressWarnings("deprecation")
	@Override
	public void setDataProvider(DataProvider<T, ?> dataProvider) {
		super.setDataProvider(dataProvider);
		getItems().forEach(cb -> {
			cb.addClassName(CSS_CLASS);
		});
		getItems().findFirst().ifPresent(i -> i.addClassName(CSS_FIRST_CLASS));
	}

}
