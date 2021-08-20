package org.thshsh.vaadin.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

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

	@Autowired
	protected ApplicationContext appContext;

	protected String entityIdString;
	protected ID entityId;

	protected Class<? extends EntityForm<T, ID>> entityFormClass;
	protected Class<? extends Component> parentView;
	protected EntityForm<T, ID> entityForm;
	protected Registration leaveRegistration;

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
		if (parametersMap.containsKey(ID_PARAM)) {
			entityIdString = parametersMap.get(ID_PARAM).get(0);
			entityId = createEntityId(entityIdString);

		} else {

		}

		entityForm = createEntityForm();
		entityForm.setWidthFull();
		entityForm.addLeaveListener(() -> {
			this.leave();
		});
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

	protected void leave() {
		if(parentView != null) {
			UI.getCurrent().navigate(parentView);
		}
	}

	public class EntityViewViewChangeListener implements BeforeLeaveListener {

		public EntityViewViewChangeListener() {}

		@Override
		public void beforeLeave(BeforeLeaveEvent event) {

			if(entityForm != null) {
				LOGGER.info("beforeLeave");
				event.postpone();
				entityForm.confirmLeave(() -> {
					LOGGER.info("leave confirmed");
					leaveRegistration.remove();
					event.getContinueNavigationAction().proceed();
				},() -> {
					LOGGER.info("Stay confirmed");
					//user decided to stay
				});
			}

		}

	}

}
