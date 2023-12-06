package org.thshsh.vaadin.entity;

import java.util.Locale;

import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;

public interface EntityIconFactory extends IconFactory {

	public String collectionName();
	
	public String name();
	
	public static String getName(IconFactory iconFact) {
		String collection = null;
		String name = null;
		if(iconFact instanceof VaadinIcon) {
			collection = "vaadin";
		}
		else if(iconFact instanceof EntityIconFactory) {
			collection = ((EntityIconFactory)iconFact).collectionName();
		}
		if(iconFact instanceof Enum) {
			name = ((Enum<?>)iconFact).name();
		}
		return (collection != null ? collection +":" : "") + (name != null ? name.toLowerCase(Locale.ENGLISH).replace('_', '-') : "");
	}
	
}
