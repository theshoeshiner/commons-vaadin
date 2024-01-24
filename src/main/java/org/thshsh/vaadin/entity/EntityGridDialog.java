package org.thshsh.vaadin.entity;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.flow.component.dialog.Dialog;

import lombok.Getter;


@SuppressWarnings("serial")
@Getter
public abstract class EntityGridDialog<T,ID extends Serializable> extends Dialog  {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityGridDialog.class);

	@Autowired
	protected ApplicationContext appContext;
	protected Class<? extends EntityGrid<T, ID>> entityGridClass;
	protected EntityGrid<T,ID> entityGrid;

	public EntityGridDialog(Class<? extends EntityGrid<T, ID>> entityList) {
		super();
		this.entityGridClass = entityList;
	}
	
	@PostConstruct
	public void postConstruct() {
		this.setHeight("600px");
		if(entityGrid == null) entityGrid = createEntityGrid();
		entityGrid.setHeight("100%");
		add(entityGrid);
	}
	
	protected EntityGrid<T,ID> createEntityGrid(){
		return appContext.getBean(entityGridClass);
	}

}
