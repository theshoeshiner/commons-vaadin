package org.thshsh.vaadin;

import java.time.Duration;

import org.apache.commons.lang3.time.DurationFormatUtils;

import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.function.ValueProvider;


@SuppressWarnings("serial")
public class DurationRenderer<Source> extends TextRenderer<Source> { 

	public DurationRenderer(ValueProvider<Source, Duration> valueProvider,String format) {
		super(source -> {
			Duration duration = valueProvider.apply(source);
			if(duration != null) {
				return DurationFormatUtils.formatDuration(duration.toMillis(), format);
			}
			else return "";
		});

	}
	
}
