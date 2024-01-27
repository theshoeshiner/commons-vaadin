package org.thshsh.vaadin.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.CrudRepository;
import org.thshsh.event.SmartEventListenerSupport;
import org.thshsh.text.cases.KebabCase;
import org.thshsh.text.cases.PascalCase;
import org.thshsh.vaadin.entity.EntityFormButtons.LeaveListener;
import org.thshsh.vaadin.form.FormLayout;
import org.thshsh.vaadin.form.FormLayoutBuilder;

import com.vaadin.flow.component.HasValidation;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")

public abstract class EntityForm<T,ID extends Serializable> extends VerticalLayout {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityForm.class);
	
	public static final String CLASS = "entity-form";

	protected ID entityId;
	protected T entity;
	protected EntityBinder<T> binder;
	protected Boolean create = false;
	protected FormLayout formLayoutLayout;
	protected FormLayoutBuilder formLayout;
	protected Boolean saved = false;
	protected String createText = "Create";
	protected String editText = "Edit";
	protected String emptyEntityName = "New";
	
	protected EventListenerSupport<NameChangeListener> nameChangeListeners = SmartEventListenerSupport.create(NameChangeListener.class);
	protected Boolean persist = true;
	protected EntityFormButtons buttons;
	protected TitleSpan title;
	protected Boolean loadFromId = true;
	protected Boolean readOnly = false;
	
	protected HorizontalLayout titleLayout;
	
	protected EntityDescriptor<T, ID> descriptor;
	protected CrudRepository<T, ID> repository;
	
	protected TextField entityNameField;
	protected Registration entityNameValueChangeRegistration;

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
		this.addClassNames(KebabCase.INSTANCE.format(PascalCase.INSTANCE.parse(descriptor.getEntityClass().getSimpleName()))+"-entity-form","entity-form");

	    if(entity!=null) {
	    	entityId = descriptor.getEntityId(entity);
	    	if(loadFromId) entity = loadEntity();
	    	LOGGER.debug("reloaded entity with id: {} = {}",entityId,entity);
	    }
	    else if(entityId != null) {
	    	if(loadFromId) entity = loadEntity();
	    	LOGGER.debug("Got entity by id: {} = {}",entityId,entity);
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
	    
	    refreshHeaderText(getEntityNameOrEmpty());
		title.addClassName("h2");
		titleLayout.add(title);

	    binder = createBinder();

	    formLayoutLayout = new FormLayout(binder);
	    this.add(formLayoutLayout);
	    this.formLayout = new EntityFormLayoutBuilder<>(this, formLayoutLayout);

	    setupForm();

	    read();

	    buttons = formLayout.pushComponent(new EntityFormButtons(binder, false),null);
	    buttons.getSaveListeners().addListener(() -> this.save());
	    //buttons.getLeaveListeners().addListener(() -> this.leave());
	    buttons.getNotifyListeners().addListener(() -> {
	    	Notification n = Notification.show(descriptor.getEntityTypeName()+ " Saved", 750, Position.TOP_END);
			n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
	    });
	    
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
		binder.setReadOnly(readOnly);
		this.buttons.setReadOnly(readOnly);
	}

	protected abstract void setupForm();

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
	}

	protected void read() {
		 binder.readBean(entity);
	}

	protected void bind() throws ValidationException {
		try {
			binder.writeBean(entity);
		} 
		catch (ValidationException e) {
			formLayoutLayout.handleValidationException(e);
			if(LOGGER.isTraceEnabled()) {
				e.getValidationErrors().forEach(vr -> {
					LOGGER.trace("Validation error: {} - {}",vr.getErrorLevel(),vr.getErrorMessage());
				});
			}
			throw e;
		}
	}

	protected T persist() {
		T saveResponse = null;
		if(persist && getRepository()!=null) {
			saveResponse = getRepository().save(entity);
		}
		LOGGER.debug("Saved entity: {}",entity);
		this.saved = true;
		return saveResponse;
	}

	public Boolean getSaved() {
		return saved;
	}

	public String getEntityName() {		
		String en = descriptor.getEntityName(entity);
		if(en == null) {
			if(entityNameField != null) {
				if(!((HasValidation)entityNameField).isInvalid()){
					en = StringUtils.defaultIfBlank(entityNameField.getValue(), null);
				}
			}
		}
		return en;
	}
	
	public void refreshName() {
		String entityName = getEntityNameOrEmpty();
		refreshHeaderText(entityName);
		nameChangeListeners.fire().entityNameChange(entityName);
	}
	

	
	public void refreshHeaderText(String entityName) {

		if(create && StringUtils.isNotEmpty(createText)) {
			title.actionSpan.setText(createText+StringUtils.SPACE);
		}
		else if(StringUtils.isNotEmpty(editText)) {
			title.actionSpan.setText(editText+StringUtils.SPACE);
		}
		else title.actionSpan.removeAll();
		
		title.typeSpan.setText(getEntityTypeName());
		
		if(StringUtils.isNotBlank(entityName)) {
			title.typeSpan.setText(title.typeSpan.getText()+": ");
			title.nameSpan.setText(entityName);
		}
	}
	
	public String getEntityNameOrEmpty() {
		String entityName = getEntityName();
		if(entityName == null) entityName = emptyEntityName;
		LOGGER.info("getEntityNameOrEmpty: {}",entityName);
		return entityName;
	}

	//FIXME figure out how to allow non textfield fields
	public void setEntityNameField(TextField nameField) {
		if(this.entityNameValueChangeRegistration != null) {
			this.entityNameValueChangeRegistration.remove();
		}
		this.entityNameField = nameField;
		this.entityNameValueChangeRegistration = nameField.addValueChangeListener(change -> {
			refreshName();
		});
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

	public HorizontalLayout getTitleLayout() {
		return titleLayout;
	}

	public EntityDescriptor<T, ID> getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(EntityDescriptor<T, ID> descriptor) {
		this.descriptor = descriptor;
	}

	public void addLeaveListener(LeaveListener sl) {
		buttons.getLeaveListeners().addListener(sl);
	}

	public void addNameChangeListener(NameChangeListener sl) {
		nameChangeListeners.addListener(sl);
	}
	
	public FormLayoutBuilder getFormLayout() {
		return formLayout;
	}

	public EntityFormButtons getButtons() {
		return buttons;
	}

	public Span getTitle() {
		return title;
	}
	
	public EntityBinder<T> getBinder() {
		return binder;
	}

	public CrudRepository<T, ID> getRepository() {
		return repository;
	}

	public void setRepository(CrudRepository<T, ID> repository) {
		this.repository = repository;
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

	public interface NameChangeListener {
		void entityNameChange(String name);
	}
}
