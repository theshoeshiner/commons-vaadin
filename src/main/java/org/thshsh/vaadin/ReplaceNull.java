package org.thshsh.vaadin;

import com.vaadin.flow.function.ValueProvider;

@SuppressWarnings("serial")
public class ReplaceNull<A> implements ValueProvider<A,Object> {
	
	ValueProvider<A, Object> internal;
	String replace;

	public ReplaceNull(ValueProvider<A, Object> internal,String r) {
		super();
		this.internal = internal;
		this.replace = r;
	}

	@Override
	public Object apply(A source) {
		Object n = internal.apply(source);
		if(n == null) return replace;
		else return n;
	}
	
	public static <A> ReplaceNull<A> create(ValueProvider<A, Object> i,String replace) {
		return new ReplaceNull<A>(i,replace);
	}
	
}