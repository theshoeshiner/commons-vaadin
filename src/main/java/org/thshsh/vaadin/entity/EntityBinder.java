package org.thshsh.vaadin.entity;

import java.util.Collection;

import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertySet;

@SuppressWarnings("serial")
public class EntityBinder<T> extends Binder<T> {

	public EntityBinder() {
		super();
	}

	public EntityBinder(Class<T> beanType, boolean scanNestedDefinitions) {
		super(beanType, scanNestedDefinitions);
	}

	public EntityBinder(Class<T> beanType) {
		super(beanType);
	}

	public EntityBinder(PropertySet<T> propertySet) {
		super(propertySet);
	}

	@Override
	public Collection<BindingImpl<T, ?, ?>> getBindings() {
		return super.getBindings();
	}
	
}
