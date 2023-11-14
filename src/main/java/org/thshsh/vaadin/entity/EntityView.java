package org.thshsh.vaadin.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.CrudRepository;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveListener;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Location;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.shared.Registration;

@SuppressWarnings("serial")
public abstract class EntityView<T, ID extends Serializable> extends VerticalLayout implements HasUrlParameter<String> {

	public static final Logger LOGGER = LoggerFactory.getLogger(EntityView.class);

	public static final String ID_PARAM = "id";

	public static final String CLASS = "entity-view";
	
	@Autowired
	protected ApplicationContext appContext;

	protected String entityIdString;
	protected ID entityId;

	protected Class<? extends EntityForm<T, ID>> entityFormClass;
	protected Class<? extends Component> parentView;
	protected EntityForm<T, ID> entityForm;
	protected Registration leaveRegistration;
	
	protected String idParameter= ID_PARAM;
	protected Boolean useQueryParameter = true;

	protected CrudRepository<T, ID> repository;
	protected EntityDescriptor<T, ID> descriptor;

	public EntityView(Class<? extends EntityForm<T, ID>> formClass) {
		this(formClass,null);
	}
	
	public EntityView(Class<? extends EntityForm<T, ID>> formClass,Class<? extends Component> parentView) {
		this.entityFormClass = formClass;
		this.parentView = parentView;
		leaveRegistration = UI.getCurrent().addBeforeLeaveListener(new EntityViewViewChangeListener());
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
		Location location = event.getLocation();
		QueryParameters queryParameters = location.getQueryParameters();
		Map<String, List<String>> parametersMap = queryParameters.getParameters();
		if(useQueryParameter) {
			if (parametersMap.containsKey(idParameter)) {
				entityIdString = parametersMap.get(idParameter).get(0);
				entityId = descriptor.createEntityId(entityIdString);
	
			} else {
				
			}
		}
		else {
			if(StringUtils.isNotBlank(parameter)) {
				entityIdString = parameter;
				entityId = descriptor.createEntityId(entityIdString);
			}
		}
		//We need to do this in case set parameter is called a second time, which can happen due to
		//double clicks or links that lead to the same route
		if(entityForm != null) {
			remove(entityForm);
		}
		entityForm = createEntityForm();
		if(entityForm != null) {
			entityForm.getButtons().setJustifyContentMode(JustifyContentMode.START);
			entityForm.setWidthFull();
			entityForm.getButtons().setConfirm(false);
			entityForm.addLeaveListener(() -> {
				this.leave();
			});
			add(entityForm);
		}
		entityForm.addNameChangeListener(this::entityNameChange);
	}


	protected EntityForm<T, ID> createEntityForm() {
		if(entityFormClass != null) return appContext.getBean(entityFormClass,entityId);
		else return null;
	}

	public EntityForm<T, ID> getEntityForm() {
		return entityForm;
	}

	public Boolean isCreate() {
		return entityIdString == null;
	}
	
	public T getEntity() {
		if(entityForm != null) return entityForm.getEntity();
		else return loadEntity();
	}
	
	protected T loadEntity() {
		return getRepository().findById(entityId).get();
	}
	
	@PostConstruct
	public void postConstruct() {
		this.addClassName(CLASS);
	}

	protected void leave() {
		if(parentView != null) {
			UI.getCurrent().navigate(parentView);
		}
	}
	


	public CrudRepository<T, ID> getRepository() {
		return repository;
	}

	public void setRepository(CrudRepository<T, ID> repository) {
		this.repository = repository;
	}

	public EntityDescriptor<T, ID> getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(EntityDescriptor<T, ID> descriptor) {
		this.descriptor = descriptor;
	}

	protected void entityNameChange(String name) {
		UI.getCurrent().getPage().setTitle(getPageTitle(name));
	}
	
	public String getPageTitle() {
		return getPageTitle(entityForm!=null?entityForm.getEntityNameOrEmpty():null);
	}
	
	public String getPageTitle(String entityName) {
		StringBuilder sb = new StringBuilder();
		sb.append(descriptor.getEntityTypeName());
		if(entityName != null) {
			sb.append(": ");
			sb.append(entityName);
		}
		return sb.toString();
	}

	public class EntityViewViewChangeListener implements BeforeLeaveListener {

		public EntityViewViewChangeListener() {}

		@Override
		public void beforeLeave(BeforeLeaveEvent event) {

			if(entityForm != null) {
				event.postpone();
				entityForm.getButtons().confirmLeave(() -> {
					leaveRegistration.remove();
					event.getContinueNavigationAction().proceed();
				},() -> {
					//user decided to stay
				});
			}

		}

	}

}
