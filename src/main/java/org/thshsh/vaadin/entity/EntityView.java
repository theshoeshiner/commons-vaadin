package org.thshsh.vaadin.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

		} else {

		}

		entityForm = createEntityForm();
		entityForm.setWidthFull();
		add(entityForm);
		
		
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
	
	@PostConstruct
	public void postConstruct() {
		
	}
	

}
