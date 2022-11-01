package org.thshsh.vaadin;

import java.time.Duration;

import org.thshsh.text.DurationFormatter;

import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;


@SuppressWarnings("serial")
public class DurationRenderer<Source> extends TextRenderer<Source> { 
	
	public DurationRenderer(ValueProvider<Source, Duration> valueProvider,DurationFormatter format) {
		super(source -> {
			Duration duration = valueProvider.apply(source);
			if(duration != null) {
				return format.format(duration);
			}
			else return "";
		});

	}
	
}