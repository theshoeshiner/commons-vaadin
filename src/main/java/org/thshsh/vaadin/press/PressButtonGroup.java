package org.thshsh.vaadin.press;

import java.util.stream.Stream;

import com.vaadin.flow.component.HasStyle;

public interface PressButtonGroup<T extends HasStyle> {
	
	String CSS_GROUP_CLASS = "group-press-button";
	String CSS_FIRST_CLASS = "first";
	
	Stream<T> getItems();
	
}
