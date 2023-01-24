package org.thshsh.vaadin.nonuifield;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

@SuppressWarnings("serial")
public class NonUiCollectionField<T> extends NonUiField<Collection<T>> {
	
	

	public NonUiCollectionField() {
		super(new ArrayList<>());
	}

	public NonUiCollectionField(Collection<T> value) {
		super(value);
	}

	@Override
	protected boolean valueEquals(Collection<T> value1, Collection<T> value2) {
		return CollectionUtils.isEqualCollection(value1, value2);
	}

	@Override
	public void setValue(Collection<T> value) {
		super.setValue(copy(value));
	}

	@Override
	public Collection<T> getValue() {
		return copy(super.getValue());
	}

	protected Collection<T> copy(Collection<T> collection){
		return new ArrayList<>(collection);
	}
	
}
