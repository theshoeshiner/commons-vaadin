package org.thshsh.vaadin;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.ValueProvider;


@SuppressWarnings("serial")
public class ImageRenderer<Source> extends ComponentRenderer<Image,Source> {
	
	String width;
	String height;
	
	public ImageRenderer(ValueProvider<Source,String> urlProvider,ValueProvider<Source,String> altProvider,String width,String height) {
		super(source -> {
			Image image = new Image(urlProvider.apply(source), altProvider!=null? altProvider.apply(source):StringUtils.EMPTY);
			image.setWidth(width);
			image.setHeight(height);
			return image;
		});
		this.width = width;
		this.height = height;
	}
	
	/*
	 * public RouterLinkRenderer(Class<? extends
	 * com.vaadin.flow.component.Component> view,String idParam,
	 * ValueProvider<Source, String> nameProvider, ValueProvider<Source, ?>
	 * idProvider) { super(source -> { RouterLink rl = new
	 * RouterLink(nameProvider.apply(source),view);
	 * rl.setQueryParameters(QueryParameters.simple(new
	 * SingletonMap<>(idParam,idProvider.apply(source).toString()))); return rl; });
	 * 
	 * }
	 */
	
}
