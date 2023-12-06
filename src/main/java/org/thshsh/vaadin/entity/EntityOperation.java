package org.thshsh.vaadin.entity;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.data.binder.ValidationResult;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EntityOperation<T> {
	

	protected String name;
	protected String preposition;
	@Builder.Default
	protected boolean singular = false;
	@Builder.Default
	protected boolean all = false;
	protected Consumer<Collection<T>> operation;
	
	protected IconFactory icon;
	@Builder.Default
	protected boolean confirm = false;
	@Builder.Default
	protected boolean hide = false;
	@Builder.Default
	protected boolean display = true;
	
	protected Function<T,String> nameFunction;
	protected Function<T,IconFactory> iconFunction;
	protected Function<T,Boolean> enabledFunction;
	protected Function<T,Boolean> hideFunction;
	protected Function<T,Boolean> displayFunction;
	protected Function<Collection<T>,ValidationResult> checkFunction;
	
	public IconFactory getIcon(T t) {
		if(icon != null) return icon;
		else if(iconFunction != null) return iconFunction.apply(t);
		else return null;
	}
	
	public Icon createIcon(T t) {
		IconFactory fact = getIcon(t);
		return fact != null ? fact.create() : null;
	}

	public boolean isHide(T e) {
		return this.hide || (hideFunction != null && hideFunction.apply(e));
	}

    public boolean isDisplay(T e) {
        return this.display && (displayFunction == null || displayFunction.apply(e));
    }

	public boolean isEnabled(T e) {
		return enabledFunction == null || enabledFunction.apply(e);
	}
	
	public static class EntityOperationBuilder<T> {
		
		public EntityOperationBuilder<T> operation(Consumer<Collection<T>> operation) {
			this.operation = operation;
			this.singular(false);
			return this;
		}
		
		public EntityOperationBuilder<T> singularOperation(Consumer<T> operation) {
			this.operation = (c) -> {
				operation.accept(c.iterator().next());
			};
			this.singular(true);
			return this;
		}
		
	}

}
