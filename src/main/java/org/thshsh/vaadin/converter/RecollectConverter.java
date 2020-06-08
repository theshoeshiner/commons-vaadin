package org.thshsh.vaadin.converter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/**
 * Converter that just creates a new collection. Useful for when components return unmodifiable collections
 */
public class RecollectConverter<C extends Collection<E>,E> implements Converter<C,C> {

	private static final long serialVersionUID = -9040417792226361429L;
	
	Class<? extends C> classs;
	
	public RecollectConverter(Class<? extends C> c) {
		this.classs = c;
	}

	@Override
	public Result<C> convertToModel(C value, ValueContext context) {
		try {
			C newin = classs.newInstance();
			newin.addAll(value);
			return Result.ok(newin);
		} 
		catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		} 
	}

	@Override
	public C convertToPresentation(C value, ValueContext context) {
		return value;
	}

	@SuppressWarnings("unchecked")
	public static <T> RecollectConverter<Set<T>,T> forSet(){
		return new RecollectConverter<Set<T>, T>((Class<? extends Set<T>>) HashSet.class);
		
	}

}
