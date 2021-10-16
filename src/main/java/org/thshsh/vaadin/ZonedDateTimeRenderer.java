package org.thshsh.vaadin;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;

@SuppressWarnings("serial")
public class ZonedDateTimeRenderer<Source> extends TextRenderer<Source> { 

	public ZonedDateTimeRenderer(ValueProvider<Source, ZonedDateTime> valueProvider,DateTimeFormatter format) {
		super(source -> {
			ZonedDateTime timestamp = valueProvider.apply(source);
			if(timestamp != null) {
				return format.format(timestamp);
			}
			else return "";
		});

	}
	
}
