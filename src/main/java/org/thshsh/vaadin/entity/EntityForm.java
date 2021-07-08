package org.thshsh.vaadin.entity;

import java.io.Serializable;
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

public abstract class EntityForm<T,ID extends Serializable> extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityView.class);

	//Class<? extends com.vaadin.flow.component.Component> parentView;
	protected Class<T> entityClass;
	protected ID entityId;
	protected T entity;
	protected Binder<T> binder;
	protected Boolean create = false;
	protected NestedOrderedLayout<?> formLayout;
	protected String entityTypeName;
	protected Boolean saved = false;
	protected String createText = "Create";
	protected String editText = "Edit";
	protected Button cancel;
	protected Button save;
	protected String saveText = "Save";
	protected Set<LeaveListener> leaveListeners = new HashSet<>();
	protected Boolean persist = true;
	protected HorizontalLayout buttons;
	protected Span title;
	protected Boolean loadFromId = true;

	public EntityForm(Class<T> eClass,T entity){
		this.entityClass = eClass;
		this.entity = entity;
	}

	public EntityForm(Class<T> eClass,T entity, Boolean load){
		this.entityClass = eClass;
		this.entity = entity;
		this.loadFromId = load;
	}
	
	//TODO FIXME the boolean argument is a hack so that we can autowire via this constructor without type erasure errors
	public EntityForm(Class<T> eClass,ID id,Boolean loadFromId){
		this.entityClass = eClass;
		this.entityId = id;
		this.loadFromId = loadFromId;
	}


	@PostConstruct
	public void postConstruct() {

		if(entityTypeName == null) entityTypeName = entityClass.getSimpleName();

	    if(entity!=null) {
	    	entityId = getEntityId(entity);
	    	if(loadFromId) entity = loadEntity();
	    	LOGGER.debug("Got entity with id: {} = {}",entityId,entity);
	    }
	    else if(entityId != null) {
	    	if(loadFromId) entity = loadEntity();
	    }
	    else {
	    	create = true;
			entity = createEntity();
	    }

	    title = new Span(((create)?createText:editText)+" "+entityTypeName);
		title.addClassName("h2");

		add(title);

	    binder = new Binder<>(entityClass);

	    formLayout = new NestedOrderedLayout<>();
	    this.add(formLayout);

	    setupForm();

	    binder.readBean(entity);

	    buttons = formLayout.startHorizontalLayout();
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.END);

		save = new Button(saveText);
		save.addClickListener(click -> saveAndLeave());
		buttons.add(save);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		cancel = new Button("Cancel");
		buttons.add(cancel);
		cancel.addClickListener(click -> confirmLeave());

	}



	protected abstract JpaRepository<T, ID> getRepository();

	protected abstract void setupForm();

	protected void saveAndLeave() {
		try {
			save();
			//TODO do we need to actually confirm here? since we have already saved the form
			confirmLeave();
		}
		catch (ValidationException e) {
			LOGGER.debug("Form Validation Failed",e);
		}

	}


	/**
	 * This method checks for any form changes before proceeding with leaving the form
	 * The override listener allows you to override the standard leavelisteners
	 */
	public void confirmLeave(LeaveListener leaveOverride, StayListener stay) {
		EntityFormUtils.checkForChangesAndConfirm(binder, () -> {
			//user chose to save
			this.save();
			return null;
		}, leave -> {
			LOGGER.debug("leave: {}",leave);
			if(leave) {
				if(leaveOverride!=null) leaveOverride.leave();
				else leave();
			}
			else {
				//user chose to stay or there was an exception saving
				if(stay != null) stay.stay();
			}
		});

	}

	public void confirmLeave() {
		this.confirmLeave(null,null);
	}
	
	protected void leave() {
		leaveListeners.forEach(LeaveListener::leave);
	}
	
	protected T loadEntity() {
		if(getRepository()!=null) return getRepository().findById(entityId).get();
		else return entity;
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
		LOGGER.debug("Saved entity: {}",entity);
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

	public Boolean isCreate() {
		return create;
	}
	

	public String getEntityTypeName() {
		return entityTypeName;
	}

	protected Set<SaveListener> saveListeners = new HashSet<>();

	public void addSaveListener(SaveListener sl) {
		saveListeners.add(sl);
	}

	public void addLeaveListener(LeaveListener sl) {
		leaveListeners.add(sl);
	}

	public NestedOrderedLayout<?> getFormLayout() {
		return formLayout;
	}

	public HorizontalLayout getButtons() {
		return buttons;
	}

	public Span getTitle() {
		return title;
	}

	public static interface SaveListener {
		public void saved();
	}

	public static interface LeaveListener {
		public void leave();
	}
	

	public static interface StayListener {
		public void stay();
	}

}
