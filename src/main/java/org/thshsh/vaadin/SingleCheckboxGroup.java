package org.thshsh.vaadin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.customfield.CustomField;

@SuppressWarnings("serial")
public class SingleCheckboxGroup extends CustomField<Boolean> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(SingleCheckboxGroup.class);

	CheckboxGroup<String> group;
	String name;
	Set<String> trueSet;
	
	public SingleCheckboxGroup(String name) {
		super();
		
		this.name = name;
		group = new CheckboxGroup<>();
		//group.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
		group.setItems(name);
		//group.setHelperText("Automatically detects and sets allocation for this Currency");
		
		trueSet = new HashSet<>();
		trueSet.add(name);
		
		this.add(group);
		
		this.setHelperText("");
	}

	@Override
	protected Boolean generateModelValue() {
		return group.getValue().contains(name);
	}

	@Override
	protected void setPresentationValue(Boolean newPresentationValue) {
		if(Boolean.TRUE.equals(newPresentationValue)) group.setValue(trueSet);
		else group.setValue(Collections.emptySet());
	}

	public boolean isTrue() {
		return Boolean.TRUE.equals(super.getValue());
	}
	
}
