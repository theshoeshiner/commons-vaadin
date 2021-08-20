package org.thshsh.vaadin.entity;

@SuppressWarnings("serial")
public class EntityNotFoundException extends IllegalArgumentException {

	public EntityNotFoundException() {
		super();
	}

	public EntityNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public EntityNotFoundException(String s) {
		super(s);
	}

	public EntityNotFoundException(Throwable cause) {
		super(cause);
	}

}
