package org.thshsh.vaadin.entity;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.dialog.Dialog;

@SuppressWarnings("serial")
@Component
@Scope("prototype")
public abstract class EntityDialog<T,ID extends Serializable> extends Dialog {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityDialog.class);

	@Autowired
	protected ApplicationContext appContext;
	
	protected Class<? extends EntityForm<T, ID>> entityFormClass;
	protected EntityForm<T, ID> entityForm;
	protected T entity;
	protected ID entityId;

	public EntityDialog(Class<? extends EntityForm<T, ID>> formClass) {
		this.entityFormClass = formClass;
	}
	
	public EntityDialog(Class<? extends EntityForm<T, ID>> formClass, T entity) {
		this.entityFormClass = formClass;
		this.entity = entity;
	}
	
	public EntityDialog(Class<? extends EntityForm<T, ID>> formClass, ID entityId) {
		this.entityFormClass = formClass;
		this.entityId = entityId;
	}
	
	@PostConstruct
	public void postConstruct() {
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		entityForm = createEntityForm();
		entityForm.addLeaveListener(() -> {
			LOGGER.debug("close listener");
			this.close();
		});
		entityForm.setWidthFull();
		add(entityForm);
	}
	
	protected EntityForm<T, ID> createEntityForm() {
		if(entity != null) return appContext.getBean(entityFormClass,entity);
		else return appContext.getBean(entityFormClass,entityId);
	}

	public EntityForm<T, ID> getEntityForm() {
		return entityForm;
	}
	
	public T getEntity() {
		return getEntityForm().getEntity();
	}
	
}
