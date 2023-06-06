package org.thshsh.vaadin.progress;

public abstract class ProgressReporter {

	public abstract void increment(double val);
	public abstract void increment(double val,String message);
	
	public abstract void message(String message);
	
	public abstract void incrementMax(double val);
}
