package org.thshsh.vaadin.press;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.radiobutton.GeneratedVaadinRadioButton;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;

@SuppressWarnings("serial")
@CssImport(value = "press-button-group.css")
@CssImport(value = "press-button-group-vaadin-radio-button.css", themeFor = "vaadin-radio-button")
public class PressButtonRadioButtonGroup<T> extends RadioButtonGroup<T> implements PressButtonGroup<GeneratedVaadinRadioButton<?>> {

	public static final Logger LOGGER = LoggerFactory.getLogger(PressButtonRadioButtonGroup.class);

	public static final String CSS_CLASS = "press-button";
	
	protected ItemLabelGenerator<T> itemLabelGenerator;
	protected ItemIconGenerator<T> itemIconGenerator;
	protected Set<String> buttonClassNames = new HashSet<>();
	protected Set<ButtonVariant> buttonThemeVariants = new HashSet<>();

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
		this.itemLabelGenerator = itemLabelGenerator;
		resetRenderer();
	}

	public void setItemIconGenerator(ItemIconGenerator<T> itemIconGenerator) {
		this.itemIconGenerator = itemIconGenerator;
		resetRenderer();
	}

	/**
	 * FIXME the only way to refresh the buttons is by reseting the render because the refreshButtons method is private
	 */
	protected void resetRenderer() {
		setRenderer(new ComponentRenderer<com.vaadin.flow.component.Component, T>((f) -> {
			LOGGER.trace("render button: {}",f);
			Button b = new Button();
			if(itemLabelGenerator!=null) {
				b.setText(itemLabelGenerator.apply(f));
			}
			if(itemIconGenerator!=null) {
				Icon i = itemIconGenerator.apply(f);
				LOGGER.trace("icon: {}",i);
				b.setIcon(i);
			}
			b.addClassName(CSS_GROUP_CLASS);
			b.addClassNames(buttonClassNames.toArray(String[]::new));
			b.addThemeVariants(buttonThemeVariants.toArray(ButtonVariant[]::new));
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

	public interface ItemIconGenerator<T> extends SerializableFunction<T, Icon> {
	    @Override
	    Icon apply(T item);
	}
	
	public void addButtonClassNames(String... classNames) {
		buttonClassNames.addAll(Arrays.asList(classNames));
		resetRenderer();
	}
	
	public void addButtonThemeVariants(ButtonVariant... variants) {
		buttonThemeVariants.addAll(Arrays.asList(variants));
		resetRenderer();
	}
}
