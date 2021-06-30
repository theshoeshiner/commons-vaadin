package org.thshsh.vaadin.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.thshsh.vaadin.NestedOrderedLayout;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;

@SuppressWarnings("serial")
public abstract class EntityView<T, ID extends Serializable> extends VerticalLayout implements HasUrlParameter<String> {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityView.class);

	public static final String ID_PARAM = "id";

	@Autowired
	protected ApplicationContext appContext;

	protected String entityIdString;
	protected ID entityId;

	/*Class<? extends com.vaadin.flow.component.Component> parentView;
	JpaRepository<T, Long> repository;
	Class<T> entityClass;
	Long entityId;
	T entity;
	Binder<T> binder;
	Boolean create = false;
	NestedOrderedLayout<?> formLayout;
	String entityName;*/

	protected Class<? extends EntityForm<T, ID>> entityFormClass;
	protected EntityForm<T, ID> entityForm;

	public EntityView(Class<? extends EntityForm<T, ID>> formClass) {
		this.entityFormClass = formClass;
		//this.parentView = view;
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {

		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map<String, List<String>> parametersMap = queryParameters.getParameters();
		if (parametersMap.containsKey(ID_PARAM)) {
			entityIdString = parametersMap.get(ID_PARAM).get(0);
			entityId = createEntityId(entityIdString);
			//entityId = Long.valueOf(parametersMap.get(ID_PARAM).get(0));
			//loadEntity();
			//LOGGER.info("Got entity with id: {} = {}",entityId,entity);
		} else {
			//create = true;
			//entity = createEntity();
		}

		entityForm = createEntityForm();
		entityForm.setWidthFull();
		add(entityForm);
		
		/*binder = new Binder<>(entityClass);
		
		// breadcrumbs.resetBreadcrumbs()
		//.addBreadcrumb("Home", HomeView.class);
		//.addBreadcrumb("Flows", FlowsView.class);
		
		if(create) breadcrumbs.addBreadcrumb("New "+entityName,null);
		else {
			//TODO
		}
		
		formLayout = new NestedOrderedLayout<>();
		this.add(formLayout);
		
		setupForm();
		
		binder.readBean(entity);
		
		HorizontalLayout buttons = formLayout.startHorizontalLayout();
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.END);
		
		Button save = new Button("Save");
		save.addClickListener(click -> save());
		buttons.add(save);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		
		Button cancel = new Button("Cancel");
		buttons.add(cancel);
		cancel.addClickListener(click -> {
			leave();
		});
		
		
		setupBreadcumbs();
		
		if(create) {
			breadcrumbs.addBreadcrumb("New " + entityName, null);
		}
		else {
			breadcrumbs.addBreadcrumb(getEntityLabel(), null);
		}*/
	}

	@SuppressWarnings("unchecked")
	protected ID createEntityId(String s) {
		return (ID) s;
	}
	
	protected EntityForm<T, ID> createEntityForm() {
		return appContext.getBean(entityFormClass,entityId);
	}

	public EntityForm<T, ID> getEntityForm() {
		return entityForm;
	}
	
	/*protected void postConstruct(JpaRepository<T, Long> repository) {
		this.repository = repository;
		LOGGER.info("post construct");
		
		if(entityName == null) entityName = entityClass.getSimpleName();
		//if(entityNamePlural == null) entityNamePlural = English.plural(entityName);
		
		
	}
	
	protected abstract void setupForm();
	
	protected abstract void setupBreadcumbs(); 
	
	protected abstract String getEntityLabel();
	
	protected void save() {
		try {
			bind();
			persist();
			leave();
		} 
		catch (ValidationException e) {
			LOGGER.info("Form Validation Failed",e);
			
		}
		
	}
	
	protected void loadEntity() {
		entity = repository.findById(entityId).get();
	}
	
	protected T createEntity() {
		try {
			return entityClass.newInstance();
		} 
		catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	protected void leave() {
		if(parentView != null) {
			UI.getCurrent().navigate(parentView);
		}
	}
	
	protected void bind() throws ValidationException {
		binder.writeBean(entity);
	}
	
	protected void persist() {
		repository.save(entity);
		LOGGER.info("Saved entity: {}",entity);
	}*/

}
