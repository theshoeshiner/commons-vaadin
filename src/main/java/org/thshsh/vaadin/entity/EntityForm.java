package org.thshsh.vaadin.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.thshsh.text.CaseUtils;
import org.thshsh.vaadin.NestedOrderedLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

@SuppressWarnings("serial")

public abstract class EntityForm<T,ID extends Serializable> extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityForm.class);
	
	public static final String CLASS = "entity-form";

	//Class<? extends com.vaadin.flow.component.Component> parentView;
	protected ID entityId;
	protected T entity;
	protected Binder<T> binder;
	protected Boolean create = false;
	protected NestedOrderedLayout<?> formLayout;
	protected Boolean saved = false;
	protected String createText = "Create";
	protected String editText = "Edit";
	protected String cancelText = "Cancel";
	protected Button cancel;
	protected Button save;
	protected String saveText = "Save";
	protected Set<LeaveListener> leaveListeners = new HashSet<>();
	protected Boolean persist = true;
	protected HorizontalLayout buttons;
	protected Span title;
	protected Boolean loadFromId = true;
	protected Boolean confirm = true;
	protected HorizontalLayout titleLayout;
	protected Boolean disableSaveUntilChange = false;
	protected Boolean leaveOnSave = true;
	protected Boolean notifyOnSave = true;
	protected String header;
	
	protected EntityDescriptor<T, ID> descriptor;
	protected JpaRepository<T, ID> repository;

	public EntityForm(T entity){
		this(entity,false);
	}
	
	public EntityForm(T entity, Boolean load){
		this.entity = entity;
		this.loadFromId = load;
	}
	
	//TODO FIXME the boolean argument is a hack so that we can autowire via this constructor without type erasure errors
	public EntityForm(ID id,Boolean loadFromId){
		this.entityId = id;
		this.loadFromId = loadFromId;
	}


	@PostConstruct
	public void postConstruct() {

		this.addClassName(CLASS);
				
		this.addClassNames(CaseUtils.toKebabCase(descriptor.getEntityClass().getSimpleName())+"-entity-form","entity-form");

	    if(entity!=null) {
	    	entityId = descriptor.getEntityId(entity);
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

	    if(!create && entity == null) throw new EntityNotFoundException(descriptor.getEntityTypeName()+" Not Found: "+entityId);
	    
	    titleLayout = new HorizontalLayout();
	    titleLayout.setWidthFull();
	    titleLayout.addClassName("title-layout");
	    add(titleLayout);
	    
	    header = createHeaderText();
	    
	    title = new Span(header);
		title.addClassName("h2");
		titleLayout.add(title);

	    binder = (Binder<T>) new Binder<>(descriptor.getEntityClass());

	    formLayout = new NestedOrderedLayout<>();
	    formLayout.addClassName("form-layout");
	    this.add(formLayout);

	    setupForm();

	    binder.readBean(entity);

	    buttons = formLayout.startHorizontalLayout();
	    buttons.addClassName("buttons");
		buttons.setWidthFull();
		buttons.setJustifyContentMode(JustifyContentMode.END);

		save = new Button(saveText);
		save.addClickListener(click -> saveAndLeave());
		buttons.add(save);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		cancel = new Button(cancelText);
		buttons.add(cancel);
		cancel.addClickListener(click -> confirmLeave());
		
		if(disableSaveUntilChange) {
			save.setEnabled(false);
			binder.addValueChangeListener(change -> {
				save.setEnabled(true);
			});
		}
		

	}




	protected abstract void setupForm();

	protected void saveAndLeave() {
		try {
			save();
			//TODO do we need to actually confirm here? since we have already saved the form
			if(leaveOnSave) confirmLeave();
			else {
				if(notifyOnSave) {
					
					Notification n = Notification.show(descriptor.getEntityTypeName()+ " Saved", 750, Position.TOP_END);
					n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
				}
			}
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
		if(confirm) this.confirmLeave(null,null);
		else leave();
	}
	
	protected void leave() {
		leaveListeners.forEach(LeaveListener::leave);
	}
	
	protected T loadEntity() {
		if(getRepository()!=null) return getRepository().findById(entityId).get();
		else return entity;
	}


	protected T createEntity() {
		try {
			return descriptor.getEntityClass().getConstructor().newInstance();
		}
    	catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
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
		if(disableSaveUntilChange) save.setEnabled(false);
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

	public String getEntityName() {
		return descriptor.getEntityName(entity);
	}
	
	protected String createHeaderText() {
		if(create) {
			return createText +" "+getEntityTypeName();
		}
		else {
			return editText +" "+getEntityTypeName()+": "+getEntityName();
		}
	}
	
	public String getEntityTypeName() {
		return descriptor.getEntityTypeName();
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

	public Button getCancel() {
		return cancel;
	}

	public Boolean getConfirm() {
		return confirm;
	}

	public HorizontalLayout getTitleLayout() {
		return titleLayout;
	}

	public EntityDescriptor<T, ID> getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(EntityDescriptor<T, ID> descriptor) {
		this.descriptor = descriptor;
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
	
	public Binder<T> getBinder() {
		return binder;
	}

	public JpaRepository<T, ID> getRepository() {
		return repository;
	}

	public void setRepository(JpaRepository<T, ID> repository) {
		this.repository = repository;
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
