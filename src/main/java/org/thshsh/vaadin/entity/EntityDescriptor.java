package org.thshsh.vaadin.entity;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.atteo.evo.inflector.English;

public class EntityDescriptor<T, ID extends Serializable> {

	protected Class<T> entityClass;
	protected String entityTypeName;
	protected String entityTypeNamePlural;
	
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

	public Class<T> getEntityClass() {
		return entityClass;
	}

	public String getEntityTypeName() {
		return entityTypeName;
	}

	public String getEntityTypeNamePlural() {
		return entityTypeNamePlural;
	}

	public String getEntityName(T entity) {
		return entityTypeName;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public void setEntityTypeName(String entityTypeName) {
		this.entityTypeName = entityTypeName;
	}

	public void setEntityTypeNamePlural(String entityTypeNamePlural) {
		this.entityTypeNamePlural = entityTypeNamePlural;
	}
	
	@SuppressWarnings("unchecked")
	public ID createEntityId(String s) {
		return (ID)s;
	}
	
}
