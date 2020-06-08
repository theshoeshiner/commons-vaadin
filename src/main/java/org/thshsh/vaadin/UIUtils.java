package org.thshsh.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

public class UIUtils {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(UIUtils.class);	
	
	@SuppressWarnings("unchecked")
	public static <T> T getFirstComponent(Component parent,Class<T> c){
		
		return (T) parent.getChildren()
				.filter(component -> {
					LOGGER.info("component: {}",component);
					return c.isAssignableFrom(component.getClass());
				})
				.findFirst().orElse(null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getFirstComponent(Class<T> c){
		
		return (T) UI.getCurrent().getChildren()
				.filter(component -> {
					LOGGER.info("component: {}",component);
					return c.isAssignableFrom(component.getClass());
				})
				.findFirst().orElse(null);
	}

}
