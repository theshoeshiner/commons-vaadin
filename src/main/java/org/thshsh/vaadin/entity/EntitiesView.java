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
	Class<? extends EntitiesList<T, ID>> entityListClass;
	EntitiesList<T,ID> entitiesList;

	//public EntitiesView(Class<T> c, Class<? extends Component> ev,Class<? extends EntitiesList<T,ID>> elc) {
		//entitiesList = new EntitiesList<T,ID>(c, ev, this,FilterMode.Example);

	//}

	public EntitiesView(Class<? extends EntitiesList<T, ID>> entityList) {
		super();
		this.entityListClass = entityList;
	}

	public EntitiesView(Class<T> c, Class<? extends Component> ev,Class<? extends EntitiesList<T, ID>> entityList) {
		super();
		LOGGER.info("creating entities dialog for {}",c);
		this.entityListClass = entityList;
		this.entityClass = c;
		this.entityView = ev;
	}

	public EntitiesList<T,ID> createEntitiesList(){
		LOGGER.info("createEntitiesList");
		return appCtx.getBean(entityListClass);
	}

	@PostConstruct
	public void postConstruct() {

		this.setHeight("100%");

		if(entitiesList == null) entitiesList = createEntitiesList();

		entitiesList.setHeight("100%");
		add(entitiesList);

		//breadcrumbs
		//.resetBreadcrumbs()
		//.addBreadcrumb("Home", HomeView.class)
		//.addBreadcrumb(entitiesList.entityNamePlural, this.getClass());


	}

	/*public T getFilterEntity() {
		return entitiesList.filterEntity;
	}

	public void addButtonColumn(HorizontalLayout buttons, T e) {

		entitiesList.addButtonColumn(buttons, e);

	}*/


	/*public void delete(T entity) {
		getRepository().delete(entity);
		refresh();
	}

	public DataProvider<T,?> createDataProvider(){
		return entitiesList.createDataProvider();

	}

	public void clickNew(ClickEvent<Button> click) {
		entitiesList.clickNew(click);

	}

	public void clickEdit(ClickEvent<Button> click,T entity) {
		entitiesList.clickEdit(click, entity);

	}

	public T createFilterEntity() {
		return entitiesList.createFilterEntity();

	}

	public List<QuerySortOrder> getDefaultSortOrder() {
		return entitiesList.getDefaultSortOrder();
	}

	public void updateCount() {
		entitiesList.updateCount();

	}

	public void changeFilter(String text) {
		entitiesList.changeFilter(text);

	}

	public void refresh() {
		entitiesList.refresh();
	}*/



	/*@Override
	public void shortcutDetails(T e) {
		entitiesList.shortcutDetails(e);
	}

	public abstract void setupColumns(Grid<T> grid);

	@Override
	public abstract void setFilter(String text);

	@Override
	public abstract void clearFilter();

	@Override
	public ID getEntityId(T entity) {
		if(entity instanceof IdedEntity) return (ID) ((IdedEntity)entity).getId();
		else return null;
	}*/



}
