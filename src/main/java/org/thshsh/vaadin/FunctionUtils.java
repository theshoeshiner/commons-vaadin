package org.thshsh.vaadin;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.function.ValueProvider;

public class FunctionUtils {
	
	public static <A,B,Z> ValueProvider<A,Z> nestedValue(Function<A, B> fstart, Function<B,Z> fend) {
		return nestedValueDefault(fstart, fend, (Z)null);
	}
	
	//TODO not sure if this works
	public static <A,B> ItemLabelGenerator<A> nesteditemLabelGenerator(Function<A, B> fstart, Function<B,String> fend) {
		return (ItemLabelGenerator<A>) nestedValueDefault(fstart, fend, (String)null);
	}

	public static <A,B,Z> ValueProvider<A,Z> nestedValueDefault(Function<A, B> fstart, Function<B,Z> fend, Z nullValue) {
		return (t) -> {
			B r = fstart.apply(t);
			if(r == null) return nullValue;
			return fend.apply(r);
		};
	}


	public static <A,B,Z> com.vaadin.flow.data.binder.Setter<A,Z> nestedSetter(Function<A, B> getter, BiConsumer<B,Z> setter) {
		return (t,v) -> {
			B entity = getter.apply(t);
			if(entity != null) setter.accept(entity,v);
		};
	}


	public static <A,B,C,Z> ValueProvider<A,Z> nestedValue(Function<A, B> fstart, Function<B, C> f1,Function<C,Z> fend) {
		return FunctionUtils.nestedValue(FunctionUtils.nestedValue(fstart, f1), fend);
	}

	public static <A,B,C,D,Z> ValueProvider<A,Z> nestedValue(Function<A, B> fstart, Function<B, C> f1,Function<C, D> f2,Function<D,Z> fend) {
		return FunctionUtils.nestedValue(FunctionUtils.nestedValue(FunctionUtils.nestedValue(fstart, f1), f2),fend);
	}



}
