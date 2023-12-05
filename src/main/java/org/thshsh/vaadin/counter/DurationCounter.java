package org.thshsh.vaadin.counter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;

@JsModule("./duration-counter.ts")
@Tag("duration-counter")
public class DurationCounter extends Component implements HasSize, HasTheme, HasStyle {

	private static final long serialVersionUID = 341505818287780644L;

	public void setRun(boolean value) {
		getElement().setProperty("run",value);
	}
	
	public boolean getRun() {
		return getElement().getProperty("run",false);
	}
	
	/**
	 * Format is of the form: 
	 * h{?h}m{?m}s{$s}
	 * 
	 * @param value
	 */
	public void setFormat(String value) {
		getElement().setProperty("format",value);
	}
	
	public void setIncrement(Integer value) {
		getElement().setProperty("increment",value);
	}

	public void setValue(Long value) {
		getElement().setProperty("milliseconds",value);
	}
	
	public void setUp(boolean value) {
		getElement().setProperty("up",value);
	}
}

