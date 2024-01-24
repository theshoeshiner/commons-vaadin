package org.thshsh.vaadin.entity;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.binder.Binder;

public class EntityFormUtils {
	
	public static void checkForChangesAndConfirm(Binder<?> binder, Callable<Void> save, Consumer<Boolean> leave ) {
		checkForChangesAndConfirm(binder, save, null, leave);
	}
	
	public static void checkForChangesAndConfirm(Boolean hasChanges, Callable<Void> save, Consumer<Boolean> leave ) {
		checkForChangesAndConfirm(hasChanges, save, null, leave);
	}

	public static void checkForChangesAndConfirm(Binder<?> binder, Callable<Void> save, Consumer<Boolean> dialog, Consumer<Boolean> leave ) {
		checkForChangesAndConfirm(binder.hasChanges(),save,dialog,leave);
	}
	
	public static void checkForChangesAndConfirm(Boolean hasChanges, Callable<Void> save, Consumer<Boolean> dialog, Consumer<Boolean> leave ) {
		if(hasChanges) {
			if(dialog!=null)dialog.accept(true);
			ConfirmDialog cd = new ConfirmDialog(null, "You have unsaved changes.", VaadinIcon.QUESTION_CIRCLE.create());
			cd.withButton("Discard", VaadinIcon.TRASH.create(), () -> {
				leave.accept(true);
			});
			
			cd.withButton("Stay", VaadinIcon.ARROW_BACKWARD.create(), () -> {
				leave.accept(false);
			})
			.withVariants(ButtonVariant.LUMO_PRIMARY)
			;
			
			cd.withButton("Save", VaadinIcon.CHECK.create(), () -> {
				try {
					save.call();
					leave.accept(true);
				} 
				catch (Exception e) {
					leave.accept(false);
				}
			});
			cd.open();
		}
		else {
			if(dialog!=null)dialog.accept(false);
			leave.accept(true);
		}
	}

}
