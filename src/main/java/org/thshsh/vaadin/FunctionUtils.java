package org.thshsh.vaadin;

import java.util.function.BiConsumer;
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


	public static <A,B,Z> com.vaadin.flow.data.binder.Setter<A,Z> nestedSetter(Function<A, B> fstart, BiConsumer<B,Z> fend) {
		return (t,v) -> {
			B r = fstart.apply(t);
			fend.accept(r,v);
		};
	}

	/*
	public static <A> ValueProvider<A, ?> nestedValue(Function<A, ?> fstart, Function<Object, Object> fend) {
		return (t) -> {
			Object r = fstart.apply(t);
			if(r == null) return null;
			return fend.apply(r);
		};
	}
*/

	public static <A,B,C,Z> ValueProvider<A,Z> nestedValue(Function<A, B> fstart, Function<B, C> f1,Function<C,Z> fend) {
		return FunctionUtils.nestedValue(FunctionUtils.nestedValue(fstart, f1), fend);
	}

	public static <A,B,C,D,Z> ValueProvider<A,Z> nestedValue(Function<A, B> fstart, Function<B, C> f1,Function<C, D> f2,Function<D,Z> fend) {
		return FunctionUtils.nestedValue(FunctionUtils.nestedValue(FunctionUtils.nestedValue(fstart, f1), f2),fend);
	}

	/*
	@SuppressWarnings("unchecked")
	public static <A,Z> ValueProvider<A,Z> nestedValues(Function<A, ?> fstart, Function<Object,?>... funcs) {
		ValueProvider<A,?> vp = nestedValue(fstart, funcs[0]);
		for(int i=1;i<funcs.length;i++) {
			vp = nestedValue(vp, funcs[i]);
		}
		return (ValueProvider<A, Z>) vp;
	}
	*/


}
