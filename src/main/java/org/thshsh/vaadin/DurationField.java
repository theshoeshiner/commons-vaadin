package org.thshsh.vaadin;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

@SuppressWarnings("serial")
public class DurationField extends CustomField<Duration> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(DurationField.class);

	String label;
	TextField field;
	PrettyTime prettyTime;
	//String format = prettyTime.format(entity.getSilentTill());
	//silentField.setValue(format);
	
	public DurationField(String label) {
		this.label = label;
		init();
	}
	
	protected void init() {
		field = new TextField(label);
		field.setReadOnly(true);
		prettyTime = new PrettyTime(LocalDateTime.now());
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.setAlignItems(Alignment.BASELINE);
		//layout.setJustifyContentMode(JustifyContentMode.CENTER);
		layout.setPadding(false);
		layout.setMargin(false);
		this.add(layout);
		
		layout.add(field);
		
		layout.add(createButton("Clear", null));
		layout.add(createButton("1h", "PT1H"));
		layout.add(createButton("8h", "PT8H"));
		layout.add(createButton("1d", "P1D"));
		layout.add(createButton("3d", "P3D"));
	}
	
	protected Button createButton(String label,String period) {
		Button b = new Button(label);
		b.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		b.addClickListener(click -> {
			setValue(period);
		});
		return b;
	}
	
	public void setValue(String durationFormat) {
		if(durationFormat==null) {
			this.setValue((Duration)null);
		}
		else {
			Duration duration = Duration.parse(durationFormat);
			LOGGER.info("setValue: {} = {}",durationFormat,duration);
			this.setValue(duration);
		}
	}
	
	@Override
	protected Duration generateModelValue() {
		LOGGER.info("generateModelValue: {}",getValue());
		return getValue();
	}

	@Override
	protected void setPresentationValue(Duration newPresentationValue) {
		LOGGER.info("setPresentationValue: {}",newPresentationValue);
		this.setValue(newPresentationValue);
		if(newPresentationValue == null) {
			field.setValue(StringUtils.EMPTY);
		}
		else {
			Instant instant = prettyTime.getReference().plus(newPresentationValue);
			String format = prettyTime.format(instant);
			LOGGER.info("format: {}",format);
			field.setValue(format);
		}
	}

	@Override
	public void setValue(Duration value) {
		LOGGER.info("setValue: {}",value);
		super.setValue(value);
	}

	@Override
	public Duration getValue() {
		LOGGER.info("getValue: {}",super.getValue());
		return super.getValue();
	}
	
	

}
