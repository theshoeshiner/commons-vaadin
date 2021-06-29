package org.thshsh.vaadin.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.thshsh.crypt.IdedEntity;
import org.thshsh.vaadin.NestedOrderedLayout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

@SuppressWarnings("serial")
public abstract class EntityDialog<T> extends Dialog {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityDialog.class);


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
	}
}
