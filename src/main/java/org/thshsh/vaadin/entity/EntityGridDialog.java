package org.thshsh.vaadin.entity;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

@SuppressWarnings("serial")
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
	
	/*public EntityGridDialog(Class<T> c, Class<? extends Component> ev,Class<? extends EntityGrid<T, ID>> entityList) {
		super();
		LOGGER.info("creating entities dialog for {}",c);
		this.entityGridClass = entityList;
		this.entityClass = c;
		this.entityView = ev;
	}
	
	public EntityGridDialog(Class<T> c, Class<? extends Component> ev,EntityGrid<T,ID> entitiesList) {
		LOGGER.info("creating entities dialog for {}",c);
		//entitiesList = new EntitiesList<T,ID>(c, ev, this,FilterMode.Example);
		this.entityGrid = entitiesList;
	}*/


	
	@PostConstruct
	public void postConstruct() {
		this.setHeight("600px");
		if(entityGrid == null) entityGrid = createEntityGrid();
		entityGrid.setHeight("100%");
		add(entityGrid);
	}
	
	public EntityGrid<T,ID> createEntityGrid(){
		return appContext.getBean(entityGridClass);
	}

	/*public T getFilterEntity() {
		return entitiesList.filterEntity;
	}

	public void clickNew(ClickEvent<Button> click) {
		entitiesList.clickNew(click);
	}

	public T createFilterEntity() {
		return entitiesList.createFilterEntity();
	}

	public void addButtonColumn(HorizontalLayout buttons, T e) {
		entitiesList.addButtonColumn(buttons, e);

	}

	public void clickEdit(ClickEvent<Button> click,T entity) {
		entitiesList.clickEdit(click, entity);
	}

	public DataProvider<T,?> createDataProvider(){
		return entitiesList.createDataProvider();
	}

	public void changeFilter(String text) {
		entitiesList.changeFilter(text);
	}

	public void refresh() {
		entitiesList.refresh();
	}

	public List<QuerySortOrder> getDefaultSortOrder() {
		return entitiesList.getDefaultSortOrder();
	}

	public void updateCount() {
		entitiesList.updateCount();
	}


	@Override
	public void delete(T t) {
		//entitiesList.delete(t);
	}

	public abstract void setupColumns(Grid<T> grid);

	public abstract void setFilter(String text);

	public abstract void clearFilter();

	public abstract ExampleFilterRepository<T,ID> getRepository();


	@Override
	public ID getEntityId(T entity) {
		if(entity instanceof IdedEntity) return (ID) ((IdedEntity)entity).getId();
		else return null;
	}*/


}
