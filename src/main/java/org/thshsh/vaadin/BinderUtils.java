package org.thshsh.vaadin;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.vaadin.flow.component.HasEnabled;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.Binder.Binding;

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

}
