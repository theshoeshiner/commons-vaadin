package org.thshsh.vaadin;


import java.util.Objects;

import org.apache.commons.collections4.map.SingletonMap;

import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.router.internal.HasUrlParameterFormat;

@SuppressWarnings("serial")
public class RouterLinkRenderer<Source> extends ComponentRenderer<RouterLink,Source> {
	
	public RouterLinkRenderer(Class<? extends com.vaadin.flow.component.Component> view, ValueProvider<Source,?> nameProvider, ValueProvider<Source, ?> idProvider) {
		this(view,nameProvider,null,idProvider,false);
	}
	
	public RouterLinkRenderer(Class<? extends com.vaadin.flow.component.Component> view, ValueProvider<Source, ?> nameProvider,ValueProvider<Source, ?> idProvider,String paramName) {
		this(view,nameProvider,paramName,idProvider,false);
	}
	
	/**
	 * This allows constructing a link that always opens in a new tab
	 * @param view
	 * @param nameProvider
	 * @param paramName
	 * @param idProvider
	 * @param blanktarget
	 */
	public RouterLinkRenderer(Class<? extends com.vaadin.flow.component.Component> view, ValueProvider<Source, ?> nameProvider,String paramName, ValueProvider<Source, ?> idProvider,Boolean blanktarget) {
		this(view,nameProvider,paramName,idProvider,blanktarget?"_blank":null);
	}
	
	public RouterLinkRenderer(Class<? extends com.vaadin.flow.component.Component> view, ValueProvider<Source, ?> nameProvider,String paramName, ValueProvider<Source, ?> idProvider,String target) {
		super(source -> {
			RouterLink rl;
			if(paramName != null) {
				rl = new RouterLink(Objects.toString(nameProvider.apply(source)),view,null);
				rl.setQueryParameters(QueryParameters.simple(new SingletonMap<>(paramName,idProvider.apply(source).toString())));
			}
			else {
				//TODO we assume the id is the first parameter but might be nice to be able to configure this
				rl = new RouterLink(Objects.toString(nameProvider.apply(source)),view, HasUrlParameterFormat.getParameters(idProvider.apply(source).toString()));
			}
			
			if(target != null) rl.getElement().setAttribute("target", target);
			return rl;
		});

	}



	
}