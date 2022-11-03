package org.thshsh.vaadin;


import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.BlurNotifier;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.FocusNotifier;
import com.vaadin.flow.component.HasOrderedComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * 
 * @author TheShoeShiner
 *
 * @param <T>
 */
@SuppressWarnings({"serial","unchecked"})
public class NestedOrderedLayout<T extends Component & HasOrderedComponents> extends VerticalLayout {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(NestedOrderedLayout.class);
	
	LinkedList<T> hierarchy = new LinkedList<>();
	T currentLayout;
	
	
	public NestedOrderedLayout() {
		super();
		this.addClassName("nested-ordered-layout");
		hierarchy.add((T) this);
		currentLayout = (T) this;
		//currentLayout = new VerticalLayout();
	}
	
	public void addLayout(T layout) {
		currentLayout.add(layout);
		pushLayout(layout);
	}
	
	/**
	 * Pushes layout onto list but doesnt add to component tree
	 * @param layout
	 */
	public void pushLayout(T layout) {
		hierarchy.add(layout);
		currentLayout = layout;
	}
	
	public void poll() {
		hierarchy.pollLast();
		currentLayout = hierarchy.getLast();
	}
	
	public HorizontalLayout startHorizontalLayout() {
		HorizontalLayout newLayout = createHorizontalLayout();
		addLayout((T) newLayout);
		return newLayout;
	}
	
	public VerticalLayout startVerticalLayout() {
		VerticalLayout newLayout = createVerticalLayout();
		addLayout((T) newLayout);
		return newLayout;
	}
	
	public BasicTabSheet startBasicTabSheet() {
		BasicTabSheet newLayout = createBasicTabSheet();
		addLayout((T) newLayout);
		return newLayout;
	}

	public BasicTab startTab(String name,Component newLayout) {
		if(!(currentLayout instanceof BasicTabSheet)) throw new IllegalArgumentException("Current Layout is not a TabSheet");
		BasicTabSheet tabSheet = (BasicTabSheet) currentLayout;
		BasicTab tab = tabSheet.addTab(name, newLayout);
		if(newLayout != null) pushLayout((T) newLayout);
		return tab;
	}
	
	public BasicTab startVerticalLayoutTab(String name) {
		if(!(currentLayout instanceof BasicTabSheet)) throw new IllegalArgumentException("Current Layout is not a TabSheet");
		BasicTabSheet tabSheet = (BasicTabSheet) currentLayout;
		VerticalLayout newLayout = createVerticalLayout();
		BasicTab tab = tabSheet.addTab(name, newLayout);
		pushLayout((T) newLayout);
		return tab;
	}
	
	public BasicTabSheet createBasicTabSheet() {
		BasicTabSheet newLayout = new BasicTabSheet();
		newLayout.setMargin(false);
		newLayout.setPadding(false);
		return newLayout;
	}
	
	public HorizontalLayout createHorizontalLayout() {
		HorizontalLayout newLayout = new HorizontalLayout();
		newLayout.setMargin(false);
		newLayout.setPadding(false);
		return newLayout;
	}
	
	public VerticalLayout createVerticalLayout() {
		VerticalLayout newLayout = new VerticalLayout();
		newLayout.setMargin(false);
		newLayout.setPadding(false);
		return newLayout;
	}
	
	public void endLayout() {
		poll();
	}
	
	@Override
	public void add(Component... cs) {
		for(Component c : cs) add(c);
	}


	@SuppressWarnings("rawtypes")
	public void add(Component c) {
		if(currentLayout == this) super.add(c);
		else currentLayout.add(c);
		if(c instanceof FocusNotifier && c instanceof HasStyle) {
			FocusNotifier fn = (FocusNotifier) c;
			fn.addFocusListener(focus -> {
				//LOGGER.info("focus");
				((HasStyle)c).addClassName("focus");
			});
		}
		if(c instanceof BlurNotifier && c instanceof HasStyle) {
			BlurNotifier fn = (BlurNotifier) c;
			fn.addBlurListener(blur -> {
				//c.removeStyleName("focus");
				((HasStyle)c).removeClassName("focus");
			});
		}
	
	}

	public T getCurrentLayout() {
		return currentLayout;
	}
	
	

}
