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

	public EntityDialog(Class<? extends EntityForm<T, ID>> formClass, T entity) {
		this.entityFormClass = formClass;
		this.entity = entity;
	}
	
	@PostConstruct
	public void postConstruct() {
		this.setCloseOnEsc(false);
		this.setCloseOnOutsideClick(false);
		entityForm = createEntityForm();
		entityForm.addCloseListener(() -> {
			LOGGER.info("close listener");
			this.close();
		});
		entityForm.setWidthFull();
		add(entityForm);
	}
	
	protected EntityForm<T, ID> createEntityForm() {
		return appContext.getBean(entityFormClass,entity);
	}

	public EntityForm<T, ID> getEntityForm() {
		return entityForm;
	}
	
	/*
		String createText = "Create";
		String editText = "Edit";
		T entity;
		Class<T> entityClass;
		JpaRepository<T, Long> repository;
		Boolean create = false;
		String entityLabel;
		Binder<T> binder;
		Boolean saved = false;
		String saveText = "Save";
		HorizontalLayout buttons;
	
		NestedOrderedLayout<?> formLayout;
	
		public EntityDialog(T en, Class<T> c){
			this(en,c,null);
		}
	
		public EntityDialog(T en, Class<T> c, Boolean cr){
			this.entity = en;
			this.entityClass = c;
			LOGGER.info("Constructor entity: {}",entity);
			create = cr;
			if(create == null) create = entity == null || entity.getId() == null;
	
			this.entityLabel = entityClass.getSimpleName();
			this.setCloseOnEsc(false);
			this.setCloseOnOutsideClick(false);
		}
	
		protected T createEntity() {
			try {
				entity = entityClass.newInstance();
				return entity;
			}
		catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalArgumentException(e);
			}
		}
	
		public void postConstruct(JpaRepository<T, Long> repository) {
			this.repository = repository;
	
			if(create) entity = createEntity();
	
			binder = new Binder<>(entityClass);
	
	
			Span title = new Span(((create)?createText:editText)+" "+entityLabel);
			title.addClassName("h2");
	
			add(title);
	
			formLayout = new NestedOrderedLayout<>();
			add(formLayout);
	
			setupForm();
	
			binder.readBean(entity);
	
			buttons = formLayout.startHorizontalLayout();
			buttons.setWidthFull();
	
			buttons.setJustifyContentMode(JustifyContentMode.END);
	
			Button save = new Button(saveText);
			save.addClickListener(click -> save());
			buttons.add(save);
			save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
	
			Button cancel = new Button("Cancel");
			buttons.add(cancel);
			cancel.addClickListener(click -> {
				this.close();
			});
	
		}
	
		protected void save() {
			try {
				bind();
				persist();
				this.close();
			}
			catch (ValidationException e) {
				LOGGER.info("Form Validation Failed",e);
	
			}
	
		}
	
		protected void bind() throws ValidationException {
			binder.writeBean(entity);
		}
	
		protected void persist() {
			if(repository!=null)repository.save(entity);
			this.saved = true;
			LOGGER.info("Saved entity: {}",entity);
		}
	
		protected abstract void setupForm();
	
		public T getEntity() {
			return entity;
		}
	
		public Boolean getSaved() {
			return saved;
		}*/
	
	
}
