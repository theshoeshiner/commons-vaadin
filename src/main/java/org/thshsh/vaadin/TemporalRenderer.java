package org.thshsh.vaadin;

import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;

@SuppressWarnings("serial")
public class TemporalRenderer<Source> extends TextRenderer<Source> { 

	public <T extends Temporal> TemporalRenderer(ValueProvider<Source, T> valueProvider,DateTimeFormatter format) {
		super(source -> {
			T timestamp = valueProvider.apply(source);
			if(timestamp != null) {
				return format.format(timestamp);
			}
			else return "";
		});

	}
	
}
