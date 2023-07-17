package org.thshsh.vaadin.datetimezonepicker;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dependency.CssImport;

@Tag("vaadin-date-time-zone-picker")
@CssImport(value = "./date-time-zone-picker.css")
public class DateTimeZonePicker extends CustomField<ZonedDateTime> {
	
	private static final long serialVersionUID = 6974579067346116914L;
	
	static LinkedList<String> allZones = new LinkedList<>(ZoneId.getAvailableZoneIds());
	static {
		Collections.sort(allZones);
	}
	static LinkedList<String> geographicZones = new LinkedList<>(allZones);
	static {
		geographicZones.removeIf(s -> {
			return !s.matches("(Africa|America|Asia|Atlantic|Australia|Europe|Indian|Pacific).*?");
		});
	}
	
	DateTimePicker dateTimeField;
	ComboBox<String> zoneField;
	LinkedList<String> zones;
	
	public DateTimeZonePicker(String name) {
		this(name,true);
	}
	
	public DateTimeZonePicker(String name,Boolean geo) {
		dateTimeField = new DateTimePicker(name);
		add(dateTimeField);
		zoneField = new ComboBox<>();
		zoneField.setAllowCustomValue(true);
		add(zoneField);
		zones = geo ? geographicZones : allZones;
		zoneField.setItems(zones);

	}
	
	public void setPreferredZoneIds(List<String> main) {
		zones.removeAll(main);
		zones.addAll(0, main);
		zoneField.setItems(zones);
	}

	@Override
	protected ZonedDateTime generateModelValue() {
		if(dateTimeField.isEmpty() || zoneField.isEmpty()) return null;
		else return dateTimeField.getValue().atZone(ZoneId.of(zoneField.getValue()));
	}

	@Override
	protected void setPresentationValue(ZonedDateTime newPresentationValue) {
		dateTimeField.setValue(newPresentationValue.toLocalDateTime());
		zoneField.setValue(newPresentationValue.getZone().getId());
	}
	
	public void setZoneId(ZoneId zid) {
		zoneField.setValue(zid.getId());
	}
	
	public void setZoneId(String zid) {
		zoneField.setValue(zid);
	}

}
