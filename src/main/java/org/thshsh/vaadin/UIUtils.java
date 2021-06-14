package org.thshsh.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;

/**
 * 
 * @author TheShoeShiner
 *
 */
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

	public static void setTitle(HasElement he, String text){
		setElementProperty(he, "title", text);
	}
	
	public static void setElementProperty(HasElement he,String prop, String text){
		he.getElement().setProperty(prop,text);
	}
	
	public static void setElementAttribute(HasElement he,String prop, String text){
		he.getElement().setAttribute(prop, text);
	}
	
	/**
	 * This stops an event from propagating to outer elements. Useful when a click element is inside another clickable element
	 * @param he
	 * @param event
	 */
	public static void stopEventPropagation(HasElement he, String event) {
		he.getElement().addEventListener(event, click -> {}).addEventData("event.stopPropagation()");
	}
	
	public static void stopClickPropagation(HasElement he) {
		stopEventPropagation(he, "click");
	}
}
