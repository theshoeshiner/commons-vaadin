package org.thshsh.vaadin;


import org.apache.commons.collections4.map.SingletonMap;

import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouterLink;

@SuppressWarnings("serial")
public class RouterLinkRenderer<Source> extends ComponentRenderer<RouterLink,Source> {

	
	public RouterLinkRenderer(Class<? extends com.vaadin.flow.component.Component> view, ValueProvider<Source,String> nameProvider, ValueProvider<Source, ?> idProvider) {
		this(view,"id",nameProvider,idProvider);
	}
	
	public RouterLinkRenderer(Class<? extends com.vaadin.flow.component.Component> view,String idParam, ValueProvider<Source, String> nameProvider, ValueProvider<Source, ?> idProvider) {
		super(source -> {
			RouterLink rl = new RouterLink(nameProvider.apply(source),view);
			rl.setQueryParameters(QueryParameters.simple(new SingletonMap<>(idParam,idProvider.apply(source).toString())));
			return rl;
		});

	}



	
}