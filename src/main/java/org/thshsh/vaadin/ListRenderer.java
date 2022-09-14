package org.thshsh.vaadin;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;

/**
 * Renderer that just calls toString and separates values with a separator
 */
@SuppressWarnings("serial")
public class ListRenderer<V,SOURCE,U extends List<V>> extends TextRenderer<SOURCE> {

	
	public static final String DEFAULT_SEPARATOR = ", ";
	

	public ListRenderer(ValueProvider<SOURCE, U> valueProvider) {
		this(valueProvider,DEFAULT_SEPARATOR);
	}
	
	public ListRenderer(ValueProvider<SOURCE, U> valueProvider,String separator) {
		super(source -> {
			List<V> list = valueProvider.apply(source);
			String l = list.stream().map(V::toString).collect(Collectors.joining(separator));
			return l;
		});

	}
	

}