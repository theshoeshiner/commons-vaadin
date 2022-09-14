package org.thshsh.vaadin;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;

@SuppressWarnings("serial")
public class BooleanRenderer<Source> extends ComponentRenderer<com.vaadin.flow.component.Component,Source> {
	
	
	public BooleanRenderer(VaadinIcon icon, ValueProvider<Source, Boolean> valProvider) {
		this(icon,null,icon,null,valProvider);
	}
	
	public BooleanRenderer(VaadinIcon trueIcon,VaadinIcon falseIcon,VaadinIcon nullIcon,String className, ValueProvider<Source, Boolean> valProvider) {
		super(source -> {
			Icon i = null;
			Boolean b = valProvider.apply(source);
			if(b == null && nullIcon != null) return nullIcon.create();
			if(Boolean.TRUE.equals(b)) {
				if(trueIcon != null) {
					i = trueIcon.create();
				}
			}
			else {
				if(falseIcon != null) {
					i = falseIcon.create();
				}
			}
			if(i!=null && className!=null)i.addClassName(className);
			
			if(i != null) return i;
			else return new Span();
		});

	}

	
}