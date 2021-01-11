package org.thshsh.vaadin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;


/**
 * 
 * @author TheShoeShiner
 *
 * @param <S>
 */
@SuppressWarnings("serial")
@Tag(Tag.DIV)
public class GridField<S> extends 
	AbstractField<GridField<S>, Collection<S>> {

	public static final Logger LOGGER = LoggerFactory.getLogger(GridField.class);

	Grid<S> grid;
	ListDataProvider<S> dataProvider;
	BiFunction<S, S, Boolean> equalsMethod;
	
	public GridField(Grid<S> grid) {
		super(null);
		this.grid = grid;
		
		this.grid.addClassName("grid-field");

	}


	@Override
	public Collection<S> getValue() {
		if (dataProvider == null) return null;
		return (Collection<S>) dataProvider.getItems();
	}

	

	public void removeItem(S item) {
		Collection<S> value = getValue();
		if(value == null) value = new ArrayList<S>();
		else value = new ArrayList<S>(value);
		value.remove(item);
		this.setValue(value);
	}
	
	public void addItem(S item) {
		Collection<S> value = getValue();
		if(value == null) value = new ArrayList<S>();
		else value = new ArrayList<S>(value);
		value.add(item);
		this.setValue(value);
	}
	
	public void addItems(Iterable<S> items) {
		items.forEach(item -> addItem(item));
	}

	public void removeItems(Iterable<S> items) {
		items.forEach(item -> removeItem(item));
	}
	
	public Grid<S> getGrid() {
		return grid;
	}

	public ListDataProvider<S> getDataProvider() {
		return getOrCreateDataProvider();
	}

	public void setEqualsMethod(BiFunction<S, S, Boolean> eq) {
		this.equalsMethod = eq;
	}

	@Override
	protected void setPresentationValue(Collection<S> values) {
		//clone the value the first time around?
			LOGGER.trace("setPresentationValue: {}",values);
			
			if (dataProvider == null) {
				LOGGER.debug("Cloning value");
				//create a shallow clone of the objects
				List<S> clones = new ArrayList<>();
				values.forEach(obj -> clones.add(ObjectUtils.cloneIfPossible(obj)));
				values = clones;
			}
			ListDataProvider<S> dataProvider = getOrCreateDataProvider();
			
			dataProvider.getItems().clear();
			dataProvider.getItems().addAll(values);
	}
	
	protected ListDataProvider<S> getOrCreateDataProvider() {
		if (dataProvider == null) {
			dataProvider = new ListDataProvider<S>(new ArrayList<S>());
			grid.setItems(dataProvider);
		}
		return dataProvider;
	}

	@Override
	protected boolean valueEquals(Collection<S> value1, Collection<S> value2) {
		boolean diff = super.valueEquals(value1, value2);
		LOGGER.debug("valueEquals {}",diff);
		if(!diff && this.equalsMethod != null) {
			Iterator<S> newValues = value1.iterator();
			Iterator<S> curValues = value2.iterator();
			for(;newValues.hasNext();) {
				S newV = newValues.next();
				S curV = curValues.next();
				diff = this.equalsMethod.apply(curV, newV);
				if(!diff) {
					break;
				}
			}
		}
		return diff;
	}
	
	

	

}
