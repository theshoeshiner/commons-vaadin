package org.thshsh.vaadin;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.data.binder.Binder.Binding;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.function.ValueProvider;

public class BinderUtils {
	
	public static <T> void setEnabled(Iterable<Binding<T,?>> bindings, Boolean enabled) {
		setEnabled(StreamSupport.stream(bindings.spliterator(), false),enabled);
	}
	
	public static <T> void setEnabled(Stream<Binding<T,?>> bindings, Boolean enabled) {
		bindings.forEach(binding ->  {
			if(binding.getField() instanceof HasEnabled) {
				HasEnabled he = (HasEnabled) binding.getField();
				he.setEnabled(enabled);
			}
		});
	}
	

	public static <B extends Binding<T,?>, T> Optional<Binding<T, ?>> getBinding(Iterable<B> bindings, HasValue<?,?> field) {
		for(B b : bindings) {
			if(b.getField().equals(field)) return Optional.of(b);
		}
		return Optional.empty();
	}

    public static <A,B,Z> ValueProvider<A,Z> nestedValue(Function<A, B> fstart, Function<B,Z> fend) {
    	return BinderUtils.nestedValueDefault(fstart, fend, (Z)null);
    }

    public static <A,B,Z> ValueProvider<A,Z> nestedValueDefault(Function<A, B> fstart, Function<B,Z> fend, Z nullValue) {
    	return (t) -> {
    		B r = fstart.apply(t);
    		if(r == null) return nullValue;
    		return fend.apply(r);
    	};
    }

    public static <A,B,Z> Setter<A,Z> nestedSetter(Function<A, B> getter, BiConsumer<B,Z> setter) {
    	return (t,v) -> {
    		B entity = getter.apply(t);
    		if(entity != null) setter.accept(entity,v);
    	};
    }

    public static <A,B,C,Z> ValueProvider<A,Z> nestedValue(Function<A, B> fstart, Function<B, C> f1,Function<C,Z> fend) {
    	return nestedValue(nestedValue(fstart, f1), fend);
    }

    public static <A,B,C,D,Z> ValueProvider<A,Z> nestedValue(Function<A, B> fstart, Function<B, C> f1,Function<C, D> f2,Function<D,Z> fend) {
    	return nestedValue(nestedValue(nestedValue(fstart, f1), f2),fend);
    }

    //TODO not sure if this works
    public static <A,B> ItemLabelGenerator<A> nesteditemLabelGenerator(Function<A, B> fstart, Function<B,String> fend) {
    	return (ItemLabelGenerator<A>) nestedValueDefault(fstart, fend, (String)null);
    }

}
