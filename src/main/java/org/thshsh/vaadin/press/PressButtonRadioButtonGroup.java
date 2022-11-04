package org.thshsh.vaadin.press;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.radiobutton.GeneratedVaadinRadioButton;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;

@SuppressWarnings("serial")
@CssImport(value = "press-button-group.css")
@CssImport(value = "press-button-group-vaadin-radio-button.css", themeFor = "vaadin-radio-button")
public class PressButtonRadioButtonGroup<T> extends RadioButtonGroup<T> implements PressButtonGroup<GeneratedVaadinRadioButton<?>> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PressButtonRadioButtonGroup.class);

	public static final String CSS_CLASS = "press-button";

	public PressButtonRadioButtonGroup() {
		super();
		addThemeName(CSS_CLASS);
		resetRenderer();

	}

	public PressButtonRadioButtonGroup(String label) {
		super(label);
		addThemeName(CSS_CLASS);
		resetRenderer();
	}

	@Override
	public void setItemLabelGenerator(ItemLabelGenerator<T> itemLabelGenerator) {
		super.setItemLabelGenerator(itemLabelGenerator);
		resetRenderer();
	}

	protected void resetRenderer() {
		setRenderer(new ComponentRenderer<com.vaadin.flow.component.Component, T>((f) -> {
			Button b = new Button(getItemLabelGenerator().apply(f));
			b.addClassName(CSS_GROUP_CLASS);
			return b;
		}));
	}

	public Stream<GeneratedVaadinRadioButton<?>> getItems() {

		return getChildren()
				.filter(com.vaadin.flow.component.radiobutton.GeneratedVaadinRadioButton.class::isInstance)
				.map(child -> (GeneratedVaadinRadioButton<?>) child);
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
