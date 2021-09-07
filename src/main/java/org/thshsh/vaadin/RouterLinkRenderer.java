package org.thshsh.vaadin;


import java.util.Objects;

import org.apache.commons.collections4.map.SingletonMap;

import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouterLink;

@SuppressWarnings("serial")
public class RouterLinkRenderer<Source> extends ComponentRenderer<RouterLink,Source> {

	
	public RouterLinkRenderer(Class<? extends com.vaadin.flow.component.Component> view, ValueProvider<Source,?> nameProvider, ValueProvider<Source, ?> idProvider) {
		this(view,nameProvider,"id",idProvider,false);
	}
	
	public RouterLinkRenderer(Class<? extends com.vaadin.flow.component.Component> view, ValueProvider<Source, ?> nameProvider,String paramName, ValueProvider<Source, ?> idProvider,Boolean blanktarget) {
		this(view,nameProvider,paramName,idProvider,blanktarget?"_blank":null);
	}
	
	public RouterLinkRenderer(Class<? extends com.vaadin.flow.component.Component> view, ValueProvider<Source, ?> nameProvider,String paramName, ValueProvider<Source, ?> idProvider,String target) {
		super(source -> {
			RouterLink rl = new RouterLink(Objects.toString(nameProvider.apply(source)),view);
			rl.setQueryParameters(QueryParameters.simple(new SingletonMap<>(paramName,idProvider.apply(source).toString())));
			if(target != null) rl.getElement().setAttribute("target", target);
			return rl;
		});

	}



	
}