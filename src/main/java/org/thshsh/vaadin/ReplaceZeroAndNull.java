package org.thshsh.vaadin;

import com.vaadin.flow.function.ValueProvider;

@SuppressWarnings("serial")
public class ReplaceZeroAndNull<A> implements ValueProvider<A,Object> {
	
	ValueProvider<A, Number> internal;
	String replace;

	public ReplaceZeroAndNull(ValueProvider<A, Number> internal,String r) {
		super();
		this.internal = internal;
		this.replace = r;
	}

	@Override
	public Object apply(A source) {
		Number n = internal.apply(source);
		if(n == null  || n.intValue() == 0) return replace;
		else return n;
	}
	
	public static <A> ReplaceZeroAndNull<A> create(ValueProvider<A, Number> i,String replace) {
		return new ReplaceZeroAndNull<A>(i,replace);
	}
	
}