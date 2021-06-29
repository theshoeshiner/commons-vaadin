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
public abstract class EntitiesView<T,ID extends Serializable> extends VerticalLayout  {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntitiesView.class);

	//@Autowired
	//Breadcrumbs breadcrumbs;

	@Autowired
	ApplicationContext appCtx;

	Class<T> entityClass;
	Class<? extends Component> entityView;
	Class<? extends EntityGrid<T, ID>> entityGridClass;
	EntityGrid<T,ID> entityGrid;


	public EntitiesView(Class<? extends EntityGrid<T, ID>> entityList) {
		super();
		this.entityGridClass = entityList;
	}

	public EntitiesView(Class<T> c, Class<? extends Component> ev,Class<? extends EntityGrid<T, ID>> entityList) {
		super();
		LOGGER.info("creating entities dialog for {}",c);
		this.entityGridClass = entityList;
		this.entityClass = c;
		this.entityView = ev;
	}

	public EntityGrid<T,ID> createEntityGrid(){
		LOGGER.info("createEntitiesList");
		return appCtx.getBean(entityGridClass);
	}

	@PostConstruct
	public void postConstruct() {

		this.setHeight("100%");

		if(entityGrid == null) entityGrid = createEntityGrid();

		entityGrid.setHeight("100%");
		add(entityGrid);

		//breadcrumbs
		//.resetBreadcrumbs()
		//.addBreadcrumb("Home", HomeView.class)
		//.addBreadcrumb(entitiesList.entityNamePlural, this.getClass());


	}


}
