package org.thshsh.vaadin.entity;

import java.util.Collection;
import java.util.function.Function;

import org.thshsh.vaadin.form.FormLayout;
import org.thshsh.vaadin.form.FormLayoutBuilder;
import org.thshsh.vaadin.nested.LayoutBuilder;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.Binder.Binding;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityFormLayoutBuilder<T> extends FormLayoutBuilder {

	private static final long serialVersionUID = -1138756399758268087L;
	
	private EntityForm<T,?> entityForm;
	
	public EntityFormLayoutBuilder(EntityForm<T,?> ef , FormLayout nestedOrderedLayout) {
		super(nestedOrderedLayout);
		this.entityForm = ef;
	}
	
	@Override
	public Component startSection(Integer index, String name, Component sumComponent, Component newLayout,Function<LayoutBuilder, Component> contentCreator) {
		Function<LayoutBuilder, Component> cc = contentCreator == null ? null : lb -> {
			
			EntityBinder<T> binder = entityForm.getBinder();
			Collection<Binding<T,?>> bindingsBefore = binder.getAllBindings();
			Component content = contentCreator.apply(lb);
			
			Collection<Binding<T,?>> leftover = binder.getAllBindings();
			leftover.removeAll(bindingsBefore);
			LOGGER.info("new bindings: {}",leftover.size());
			
			//TODO FIXME reading these bindings causes the hasChanges() method to return true, but we want it to remain false since the user hasnt actually changed anything
			entityForm.getButtons().setIgnoreChanges(true);
			for(Binding<T,?> b : leftover) {
				b.read(entityForm.entity);
			}
			entityForm.getButtons().setIgnoreChanges(false);
			return content;
		};
		return super.startSection(index, name, sumComponent, newLayout, cc);
	}

}
