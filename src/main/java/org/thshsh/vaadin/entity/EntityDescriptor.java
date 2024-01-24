package org.thshsh.vaadin.entity;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.atteo.evo.inflector.English;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityDescriptor<T, ID extends Serializable> {

	private Class<T> entityClass;
	private String entityTypeName;
	private String entityTypeNamePlural;
	
	public EntityDescriptor(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	@PostConstruct
	public void postConstruct() {
		if(this.entityTypeName == null) this.entityTypeName = entityClass.getSimpleName();
		if(this.entityTypeNamePlural == null) this.entityTypeNamePlural = English.plural(entityTypeName);
	}
	
	public ID getEntityId(T e) {
		throw new UnsupportedOperationException("EntityDescriptor must be subclassed to support this");
	}


	public String getEntityName(T entity) {
		return getEntityTypeName();
	}

	public void setEntityTypeNamePlural(String entityTypeNamePlural) {
		this.entityTypeNamePlural = entityTypeNamePlural;
	}
	
	@SuppressWarnings("unchecked")
	public ID createEntityId(String s) {
		return (ID)s;
	}
	
}
