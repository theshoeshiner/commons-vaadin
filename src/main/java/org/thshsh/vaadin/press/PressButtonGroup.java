package org.thshsh.vaadin.press;

import java.util.stream.Stream;

import com.vaadin.flow.component.HasStyle;

public interface PressButtonGroup<T extends HasStyle> {
	
	public static final String CSS_GROUP_CLASS = "group-press-button";
	public static final String CSS_FIRST_CLASS = "first";
	
	public Stream<T> getItems();
	
}
