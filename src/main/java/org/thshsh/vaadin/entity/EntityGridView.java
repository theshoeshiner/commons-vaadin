package org.thshsh.vaadin.entity;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.QuerySortOrder;

@SuppressWarnings("serial")
public abstract class EntityGridView<T,ID extends Serializable> extends VerticalLayout  {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityGridView.class);

	@Autowired
	protected ApplicationContext appContext;

	protected  Class<? extends EntityGrid<T, ID>> entityGridClass;
	protected EntityGrid<T,ID> entityGrid;


	public EntityGridView(Class<? extends EntityGrid<T, ID>> entityList) {
		super();
		this.entityGridClass = entityList;
	}

	public EntityGrid<T,ID> createEntityGrid(){
		return appContext.getBean(entityGridClass);
	}

	@PostConstruct
	public void postConstruct() {
		this.setHeight("100%");
		if(entityGrid == null) entityGrid = createEntityGrid();
		entityGrid.setHeight("100%");
		add(entityGrid);
	}


}
