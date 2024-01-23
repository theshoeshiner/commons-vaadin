package org.thshsh.vaadin.nested;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * Layout that keeps track of a state hierarchy such that one layout is always
 * the current layout and new layouts and be started/ended. This makes creating
 * complex layouts easier as there is no need to track variables for
 * intermediate layouts. This class also keeps track of tabsheets so that we can
 * identify which tab a child layout is part of. This feature is necessary for
 * sub classes like {@link org.thshsh.vaadin.form.FormLayout} which need to
 * switch tabs dynamically
 *
 * @param <T>
 */
public class LayoutBuilderLayout extends VerticalLayout {

	private static final long serialVersionUID = 3662688124561710022L;

	public static final Logger LOGGER = LoggerFactory.getLogger(LayoutBuilderLayout.class);

	//keeps track of each components parent
	protected Map<Component, Component> componentParentMap = new HashMap<>();
	//keeps a "name" for each component that can be referenced later
	protected BidiMap<String, Component> componentNameMap = new DualHashBidiMap<>();

	//keeps track of the parent accordion panel or tab for children
	protected Map<Component, Component> componentSectionMap = new HashMap<>();
	//keeps track of section parents
	//protected Map<Component, Component> sectionParentMap = new HashMap<>();
	
	public LayoutBuilderLayout() {
		super();
		this.addClassName("nested-ordered-layout");
		componentParentMap.put(this, null);
	}

	


}
