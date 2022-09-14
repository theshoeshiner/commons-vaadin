package org.thshsh.vaadin.converter;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/**
 * Converter that just creates a new collection. Useful for when components return unmodifiable collections
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class RecollectConverter<C extends Collection,E> implements Converter<C,C> {

	private static final long serialVersionUID = -9040417792226361429L;
	
	protected Class<C> classs;
	
	public RecollectConverter(Class<C> class1) {
		this.classs = class1;
	}

	@Override
	public Result<C> convertToModel(C value, ValueContext context) {
		try {
			C newin = classs.getConstructor().newInstance();
			newin.addAll(value);
			return Result.ok(newin);
		} 
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException(e);
		} 
	}

	@Override
	public C convertToPresentation(C value, ValueContext context) {
		return value;
	}


	//NOTE for the life of me I cannot figure out how to *properly* get rid of this warning
	public static <C extends Set,E> RecollectConverter<C,E> forSet(){
		Class<C> cle =  (Class<C>) HashSet.class;
		return new RecollectConverter<C,E>(cle);
	}
	

}
