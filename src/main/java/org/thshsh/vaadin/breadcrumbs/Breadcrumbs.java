package org.thshsh.vaadin.breadcrumbs;

import java.util.LinkedList;
import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.router.RouterLink;

@SuppressWarnings("serial")
@CssImport("breadcrumbs.css") 
public class Breadcrumbs extends HorizontalLayout {
	
	protected List<Breadcrumb> breadcrumbs;

	protected HorizontalLayout breadcrumbsLayout;
	
	public Breadcrumbs() {
		
		breadcrumbs = new LinkedList<>();
		
		this.addClassName("breadcrumbs");
		this.setWidthFull();
		this.setPadding(true);
		
		breadcrumbsLayout = new HorizontalLayout();
		breadcrumbsLayout.addClassName("crumbs");
		breadcrumbsLayout.setMargin(false);
		breadcrumbsLayout.setAlignItems(Alignment.CENTER);
		breadcrumbsLayout.setSpacing(false);

        this.add(breadcrumbsLayout);
		
	}

	
	public Breadcrumbs resetBreadcrumbs() {
		breadcrumbsLayout.removeAll();
		this.breadcrumbs.clear();
		return this;
	}
	
	public Breadcrumbs addBreadcrumb(String text,Class<? extends com.vaadin.flow.component.Component> view,QueryParameters query,RouteParameters path) {
		Breadcrumb bc = new Breadcrumb(text, view);
		if(this.breadcrumbs.size()>0) this.addSeparator();
		breadcrumbs.add(bc);
		if(view != null) {
			RouterLink link = new RouterLink(text,view);
			breadcrumbsLayout.add(link);
			if(query != null) {
				link.setQueryParameters(query);
			}
			if(path  != null) {
				link.setRoute(view, path);
			}
		}
		else {
			Span link = new Span(text);
			breadcrumbsLayout.add(link);
		}
		return this;
	}
	
	public Breadcrumbs addBreadcrumb(String text,Class<? extends com.vaadin.flow.component.Component> view, QueryParameters qp) {
		return addBreadcrumb(text,view,qp,null);
	}

	public Breadcrumbs addBreadcrumb(String text,Class<? extends com.vaadin.flow.component.Component> view) {
		return addBreadcrumb(text,view,null,null);
	}
	
	public Breadcrumbs addBreadcrumb(String text) {
		return addBreadcrumb(text,null,null,null);
	}
	
	public void addSeparator() {
		breadcrumbsLayout.add(createSeparator());
	}
	
	public Component createSeparator() {
		Span sep = new Span("/");
		sep.addClassName("separator");
		return sep;
	}
	
	public static class Breadcrumb {
		Class<? extends com.vaadin.flow.component.Component> view;
		String text;
		public Breadcrumb(String text, Class<? extends com.vaadin.flow.component.Component> view) {
			super();
			this.text = text;
			this.view = view;
		}
		
	}
	
}
