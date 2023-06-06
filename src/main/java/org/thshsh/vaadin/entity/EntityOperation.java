package org.thshsh.vaadin.entity;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;

public class EntityOperation<T> {
	
	protected IconFactory icon;
	protected String name;
	protected Boolean singular;
	protected Boolean all;
	protected Consumer<Collection<T>> operation;
	protected Boolean confirm;
	protected Boolean hide;
	protected Function<T,Boolean> enabledFunction;
	protected Function<T,Boolean> hideFunction;
	
	/*public EntityOperation(VaadinIcon icon, String name, Boolean confirm,Consumer<Collection<T>> operation) {
		super();
		this.icon = icon;
		this.name = name;
		this.singular = false;
		this.operation = operation;
	}
	
	public EntityOperation(VaadinIcon icon, String name,  Boolean confirm,Consumer<T> operation,Boolean b) {
		super();
		this.icon = icon;
		this.name = name;
		this.singular = true;
		this.operation = (c) -> {
			operation.accept(c.iterator().next());
		};
	}*/
	
	public EntityOperation() {}
	
	public EntityOperation(Boolean singular,VaadinIcon icon, String name,Consumer<Collection<T>> operation) {
		super();
		this.singular = singular;
		this.icon = icon;
		this.name = name;
		this.operation = operation;
	}
	
	/*public EntityOperation(Boolean singular,VaadinIcon icon, String name,Boolean confirm, Consumer<Collection<T>> operation,  Boolean hide,
			Function<T, Boolean> enabledFunction) {
		super();
		this.singular = singular;
		this.icon = icon;
		this.name = name;
		this.operation = operation;
		this.confirm = confirm;
		this.hide = hide;
		this.enabledFunction = enabledFunction;
	}*/
	
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

	
	public IconFactory getIcon() {
		return icon;
	}

	public EntityOperation<T> withIcon(IconFactory icon) {
		this.icon = icon;
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
		this.hide = hide;
		return this;
	}
	
	
	public Boolean getEnabled(T e) {
		return enabledFunction == null || enabledFunction.apply(e);
	}
	
}
