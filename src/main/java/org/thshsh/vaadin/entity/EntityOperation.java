package org.thshsh.vaadin.entity;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.ValidationResult;

public class EntityOperation<T> {
	

	protected String name;
	protected Boolean singular;
	protected Boolean all;
	protected Consumer<Collection<T>> operation;
	
	protected IconFactory icon;
	protected Boolean confirm;
	protected Boolean hide;
	protected Boolean display;
	
	protected Function<T,String> nameFunction;
	protected Function<T,IconFactory> iconFunction;
	protected Function<T,Boolean> enabledFunction;
	protected Function<T,Boolean> hideFunction;
	protected Function<T,Boolean> displayFunction;
	protected Function<Collection<T>,ValidationResult> checkFunction;
	
	public EntityOperation() {}
	
	public EntityOperation(Boolean singular,VaadinIcon icon, String name,Consumer<Collection<T>> operation) {
		super();
		this.singular = singular;
		this.withIcon(icon);
		this.name = name;
		this.operation = operation;
	}
	

	public static <T> EntityOperation<T> create(){
		return new EntityOperation<T>();
	}
	
	public static <T> EntityOperation<T> singular(VaadinIcon icon, String name,  Consumer<T> operation){
		return new EntityOperation<T>(true,icon, name,  (c) -> {
			operation.accept(c.iterator().next());
		});
	}
	

	public static <T> EntityOperation<T> collective(VaadinIcon icon, String name,Consumer<Collection<T>> operation){
		return new EntityOperation<T>(false,icon, name,operation);
	}

	
	public IconFactory getIcon(T t) {
		if(icon != null) return icon;
		else if(iconFunction != null) return iconFunction.apply(t);
		else return null;
	}
	
	public Icon createIcon(T t) {
		IconFactory fact = getIcon(t);
		return fact != null ? fact.create() : null;
	}

	public EntityOperation<T> withIcon(IconFactory icon) {
		this.icon = icon;
		return this;
	}
	
	public EntityOperation<T> withIconFunction(Function<T,IconFactory> f) {
		this.iconFunction = f;
		return this;
	}
	
	

	public String getName() {
		return name;
	}

	public EntityOperation<T> withName(String name) {
		this.name = name;
		return this;
	}

	public Boolean getSingular() {
		return singular;
	}
	
	public boolean isSingular() {
		return Boolean.TRUE.equals(singular);
	}

	public EntityOperation<T> withSingular(Boolean singular) {
		this.singular = singular;
		return this;
	}
	

	public Function<Collection<T>, ValidationResult> getCheckFunction() {
		return checkFunction;
	}

	public EntityOperation<T> withCheckFunction(Function<Collection<T>, ValidationResult> checkFunction) {
		this.checkFunction = checkFunction;
		return this;
	}

	public Consumer<Collection<T>> getOperation() {
		return operation;
	}

	public EntityOperation<T> withCollectiveOperation(Consumer<Collection<T>> operation) {
		this.operation = operation;
		this.singular = false;
		return this;
	}
	
	public EntityOperation<T> withSingularOperation(Consumer<T> operation) {
		this.operation = (c) -> {
			operation.accept(c.iterator().next());
		};
		this.singular = true;
		return this;
	}
	
	
	public boolean isAll() {
		return Boolean.TRUE.equals(all);
	}

	public EntityOperation<T> withAll(Boolean all) {
		this.all = all;
		return this;
	}

	public Boolean isConfirm() {
		return Boolean.TRUE.equals(confirm);
	}
	
	public Boolean getConfirm() {
		return confirm;
	}

	public EntityOperation<T> withConfirm(Boolean confirm) {
		this.confirm = confirm;
		return this;
	}

	public Function<T, Boolean> getEnabledFunction() {
		return enabledFunction;
	}

	public EntityOperation<T> withEnabledFunction(Function<T, Boolean> enabledFunction) {
		this.enabledFunction = enabledFunction;
		return this;
	}
	
	public EntityOperation<T> withHideFunction(Function<T, Boolean> hideFunction) {
		this.hideFunction = hideFunction;
		return this;
	}

	public boolean isHide() {
		return Boolean.TRUE.equals(hide);
	}
	
	public boolean isHide(T e) {
		if(hide != null) return hide;
		if(hideFunction != null) return hideFunction.apply(e);
		else return false;
	}

	public EntityOperation<T> withHide(Boolean hide) {
		return this.withHideFunction(t -> hide);
	}
	

	public EntityOperation<T> withDisplayFunction(Function<T, Boolean> displayFunction) {
        this.displayFunction = displayFunction;
        return this;
    }

    public boolean isDisplay() {
        return Boolean.TRUE.equals(display);
    }
    
    public boolean isDisplay(T e) {
        if(display != null) return display;
        if(displayFunction != null) return displayFunction.apply(e);
        else return false;
    }

    public EntityOperation<T> withDisplay(Boolean display) {
        this.display = display;
        return this;
    }
	
	
	public Boolean getEnabled(T e) {
		return enabledFunction == null || enabledFunction.apply(e);
	}
	

}
