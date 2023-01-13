package org.thshsh.vaadin.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.thshsh.text.CaseUtils;
import org.thshsh.vaadin.form.FormLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
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
	protected EntityBinder<T> binder;
	protected Boolean create = false;
	protected FormLayout formLayout;
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
	protected TitleSpan title;
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
	    
	    title = new TitleSpan();
	    
	    refreshHeaderText();
		title.addClassName("h2");
		titleLayout.add(title);

	    binder = createBinder();

	    formLayout = new FormLayout(binder);
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
			if(leaveOnSave) {
				//TODO do we need to actually confirm here? We have already saved the form therefore there should be no changes
				//and the dialog should never open
				confirmLeave();
			}
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
	
	protected EntityBinder<T> createBinder(){
		return new EntityBinder<>(descriptor.getEntityClass());
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
		try {
			binder.writeBean(entity);
		} 
		catch (ValidationException e) {
			formLayout.handleValidationException(e);
			throw e;
		}
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
	
	public void refreshHeaderText() {

		if(create && StringUtils.isNotEmpty(createText)) {
			title.actionSpan.setText(createText+StringUtils.SPACE);
		}
		else if(StringUtils.isNotEmpty(editText)) {
			title.actionSpan.setText(editText+StringUtils.SPACE);
		}
		else title.actionSpan.removeAll();
		
		title.typeSpan.setText(getEntityTypeName());
		
		String entityName = getEntityName();
		if(StringUtils.isNotBlank(entityName)) {
			title.typeSpan.setText(title.typeSpan.getText()+": ");
			title.nameSpan.setText(entityName);
		}
	}
	
	/*protected Component createHeaderComponent() {
		Span header = new Span();
		header.addClassName("title");
		Span actionSpan = new Span();
		actionSpan.addClassName("action");
		header.add(actionSpan);
		if(create) {
			if(StringUtils.isNotEmpty(createText)) {
				actionSpan.setText(createText+StringUtils.SPACE);
			}
		}
		else {
			if(StringUtils.isNotEmpty(editText)) {
				actionSpan.setText(editText+StringUtils.SPACE);
			}
		}
		
		Span labelSpan = new Span();
		labelSpan.addClassName("label");
		header.add(labelSpan);
		
		Span typeSpan = new Span();
		typeSpan.addClassName("type");
		labelSpan.add(typeSpan);
		typeSpan.setText(getEntityTypeName());
		
		
		//sb.append(getEntityTypeName());
		String entityName = getEntityName();
		if(StringUtils.isNotBlank(entityName)) {
			Span nameSpan = new Span();
			nameSpan.addClassName("name");
			typeSpan.setText(typeSpan.getText()+": ");
			//sb.append(": ");
			nameSpan.setText(entityName);
			labelSpan.add(nameSpan);
		}
		return header;
	}*/
	
	/*protected String createHeaderText() {
		StringBuilder sb = new StringBuilder();
		if(create) {
			if(StringUtils.isNotEmpty(createText)) {
				sb.append(createText);
				sb.append(StringUtils.SPACE);
			}
		}
		else {
			if(StringUtils.isNotEmpty(editText)) {
				sb.append(editText);
				sb.append(StringUtils.SPACE);
			}
			
		}
		sb.append(getEntityTypeName());
		String entityName = getEntityName();
		if(StringUtils.isNotBlank(getEntityName())) {
			sb.append(": ");
			sb.append(entityName);
		}
		return sb.toString();
	}*/
	
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

	public FormLayout getFormLayout() {
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
	
	public static final class TitleSpan extends Span {
		
		Span actionSpan;
		Span labelSpan;
		Span typeSpan;
		Span nameSpan;
		
		public TitleSpan() {
			
			addClassName("title");
			actionSpan = new Span();
			actionSpan.addClassName("action");
		    add(actionSpan);
			/*if(create) {
				if(StringUtils.isNotEmpty(createText)) {
					actionSpan.setText(createText+StringUtils.SPACE);
				}
			}
			else {
				if(StringUtils.isNotEmpty(editText)) {
					actionSpan.setText(editText+StringUtils.SPACE);
				}
			}*/
			labelSpan = new Span();
			labelSpan.addClassName("label");
		    add(labelSpan);
			
			typeSpan = new Span();
			typeSpan.addClassName("type");
			labelSpan.add(typeSpan);
			//typeSpan.setText(getEntityTypeName());

			//sb.append(getEntityTypeName());
			//String entityName = getEntityName();
			//if(StringUtils.isNotBlank(entityName)) {
			nameSpan = new Span();
			nameSpan.addClassName("name");
			//typeSpan.setText(typeSpan.getText()+": ");
			//sb.append(": ");
			//nameSpan.setText(entityName);
			labelSpan.add(nameSpan);
			//}
			
		}
	}

}
