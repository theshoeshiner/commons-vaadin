package org.thshsh.vaadin;

import java.util.function.Function;

import com.vaadin.flow.function.ValueProvider;

public class FunctionUtils {

	public static <A,B,Z> ValueProvider<A,Z> nestedValue(Function<A, B> fstart, Function<B,Z> fend) {
		return (t) -> {
			B r = fstart.apply(t);
			if(r == null) return null;
			return fend.apply(r);
		};
	}

}
