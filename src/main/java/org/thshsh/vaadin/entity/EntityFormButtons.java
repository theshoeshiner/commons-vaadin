package org.thshsh.vaadin.entity;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thshsh.event.SmartEventListenerSupport;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Getter
public class EntityFormButtons extends HorizontalLayout {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EntityFormButtons.class);
	
	protected EventListenerSupport<SaveListener> saveListeners = SmartEventListenerSupport.create(SaveListener.class);
	protected EventListenerSupport<LeaveListener> leaveListeners = SmartEventListenerSupport.create(LeaveListener.class);
	protected EventListenerSupport<NotifyListener> notifyListeners = SmartEventListenerSupport.create(NotifyListener.class);
	@Setter
	protected String saveText = "Save";
	@Setter
	protected String cancelText = "Cancel";
	@Setter
	protected Boolean leaveOnSave = true;
	@Setter
	protected Boolean notifyOnSave = true;
	@Setter
	protected Boolean confirm = true;
	@Setter
	protected Boolean readOnly = false;
	protected Boolean disableSaveUntilChange;
	@Setter
	protected Boolean ignoreChanges = false;
	protected Boolean hasChanges = false;
	protected Button save;
	protected Button cancel;
	protected Binder<?> binder;
	
	public EntityFormButtons(Binder<?> binder,Boolean ds) {
		
		this.binder = binder;
		
		this.addClassName("buttons");
		this.setWidthFull();
		this.setJustifyContentMode(JustifyContentMode.END);

		save = new Button(saveText);
		save.addClickListener(click -> saveAndLeave());
		this.add(save);
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		cancel = new Button(cancelText);
		this.add(cancel);
		cancel.addClickListener(click -> confirmLeave());
		
		binder.addValueChangeListener(change -> {
			if(!ignoreChanges) {
				hasChanges = true;
				if(disableSaveUntilChange && !readOnly) save.setEnabled(true);
			}
		});
		
		setDisableSaveUntilChange(ds);
		
	}
	
	public void setDisableSaveUntilChange(Boolean disableSaveUntilChange) {
		this.disableSaveUntilChange = disableSaveUntilChange;
		save.setEnabled(!disableSaveUntilChange);
	}

	protected void saveAndLeave() {
		try {
			save();
			if(leaveOnSave) {
				//TODO do we need to actually confirm here? We have already saved the form therefore there should be no changes
				//and the dialog should never open
				confirmLeave();
			}
			else {
				if(notifyOnSave) {
					notifyListeners.fire().saved();
					//Notification n = Notification.show(descriptor.getEntityTypeName()+ " Saved", 750, Position.TOP_END);
					//n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
				}
			}
		}
		catch (ValidationException e) {
			LOGGER.debug("Form Validation Failed",e);
		}

	}


	/**
	 * This method checks for any form changes before proceeding with leaving the form
	 * The override listener allows you to override the standard leavelisteners
	 */
	public void confirmLeave(LeaveListener leaveOverride, StayListener stay) {
		EntityFormUtils.checkForChangesAndConfirm(hasChanges, () -> {
			//user chose to save
			this.save();
			return null;
		}, leave -> {
			if(leave) {
				if(leaveOverride!=null) leaveOverride.leave();
				else leave();
			}
			else {
				//user chose to stay or there was an exception saving
				if(stay != null) stay.stay();
			}
		});

	}

	public void confirmLeave() {
		if(confirm) this.confirmLeave(null,null);
		else leave();
	}
	
	protected void leave() {
		leaveListeners.fire().leave();
	}
	
	protected void save() throws ValidationException {
		saveListeners.fire().save();
		if(disableSaveUntilChange) save.setEnabled(false);
		hasChanges = false;
	}

	public interface SaveListener {
		void save() throws ValidationException;
	}
	
	public interface LeaveListener {
		void leave();
	}
	
	public interface NotifyListener {
		void saved();
	}
	
	public interface StayListener {
		void stay();
	}
}
