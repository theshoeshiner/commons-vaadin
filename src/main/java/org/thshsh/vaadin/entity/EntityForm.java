package org.thshsh.vaadin.entity;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.thshsh.vaadin.NestedOrderedLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

@SuppressWarnings("serial")

public abstract class EntityForm<T,ID> extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityView.class);

	//public static final String ID_PARAM = "id";

	//@Autowired
	//Breadcrumbs breadcrumbs;

	//Class<? extends com.vaadin.flow.component.Component> parentView;
	protected Class<T> entityClass;
	protected ID entityId;
	protected T entity;
	protected Binder<T> binder;
	protected Boolean create = false;
	protected NestedOrderedLayout<?> formLayout;
	protected String entityName;
	protected Boolean saved = false;
	protected String createText = "Create";
	protected String editText = "Edit";
	protected Button cancel;
	protected Button save;
	protected Set<CloseListener> closeListeners = new HashSet<>();
	protected Boolean persist = true;

	public EntityForm(Class<T> eClass,T entity){
		this.entityClass = eClass;
		this.entity = entity;
	}

	@PostConstruct
	public void postConstruct() {

		if(entityName == null) entityName = entityClass.getSimpleName();

	    if(entity!=null) {
	    	entityId = getEntityId(entity);
	    	entity = loadEntity();
	    	LOGGER.info("Got entity with id: {} = {}",entityId,entity);
	    }
	    else {
	    	create = true;
			entity = createEntity();
	    }

	    Span title = new Span(((create)?createText:editText)+" "+entityName);
		title.addClassName("h2");

		add(title);
	    
	    binder = new Binder<>(entityClass);

	    formLayout = new NestedOrderedLayout<>();
	    this.add(formLayout);

	    setupForm();

	    binder.readBean(entity);

	    HorizontalLayout buttons = formLayout.startHorizontalLayout();
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.END);

		save = new Button("Save");
		save.addClickListener(click -> saveAndLeave());
		buttons.add(save);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		cancel = new Button("Cancel");
		buttons.add(cancel);
		cancel.addClickListener(click -> close());


		//setupBreadcumbs();

		/*
		 * if(create) { breadcrumbs.addBreadcrumb("New " + entityName, null); } else {
		 * breadcrumbs.addBreadcrumb(getEntityLabel(), null); }
		 */
	}


	/*
	 * protected void postConstruct(JpaRepository<T, Long> repository) {
	 * //this.repository = repository; LOGGER.info("post construct");
	 *
	 * // if(entityName == null) entityName = entityClass.getSimpleName();
	 * //if(entityNamePlural == null) entityNamePlural = English.plural(entityName);
	 *
	 *
	 * }
	 */

	protected abstract JpaRepository<T, ID> getRepository();

	protected abstract void setupForm();

	//protected abstract void setupBreadcumbs();

	//protected abstract String getEntityName();

	/*protected void clickSave() {
		try {
			bind();
			persist();
			saveListeners.forEach(SaveListener::saved);
		}
		catch (ValidationException e) {
			LOGGER.info("Form Validation Failed",e);
	
		}
	
	}*/

	protected void saveAndLeave() {
		try {
			save();
			close();
		} 
		catch (ValidationException e) {
			LOGGER.info("Form Validation Failed",e);
			
		}
		
	}
	


	public void close() {
		LOGGER.info("close action");
		EntityFormUtils.checkForChangesAndConfirm(binder, () -> {
			this.save();
			return null;
		}, leave -> {
			LOGGER.info("leave: {}",leave);
			if(leave) {
				closeListeners.forEach(CloseListener::close);
			}
			else {
				//do nothing
			}
		});

	}
	
	protected T loadEntity() {
		return getRepository().findById(entityId).get();
	}
	
	protected abstract ID getEntityId(T e);

	protected T createEntity() {
		try {
			return entityClass.newInstance();
		}
    	catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public void setEntity(T e) {
		if(e == null) e = createEntity();
		binder.readBean(e);
		this.entity = e;
	}


	protected void save() throws ValidationException {
		bind();
		persist();
	}
	

	protected void bind() throws ValidationException {
		binder.writeBean(entity);
	}

	protected void persist() {
		if(persist && getRepository()!=null)getRepository().save(entity);
		LOGGER.info("Saved entity: {}",entity);
		this.saved = true;
	}

	public Boolean getSaved() {
		return saved;
	}

	

	public T getEntity() {
		return entity;
	}

	public Boolean getPersist() {
		return persist;
	}

	public void setPersist(Boolean persist) {
		this.persist = persist;
	}

	protected Set<SaveListener> saveListeners = new HashSet<>();
	
	public void addSaveListener(SaveListener sl) {
		saveListeners.add(sl);
	}
	
	public void addCloseListener(CloseListener sl) {
		closeListeners.add(sl);
	}

	public static interface SaveListener {
		public void saved();
	}
	
	public static interface CloseListener {
		public void close();
	}

}
