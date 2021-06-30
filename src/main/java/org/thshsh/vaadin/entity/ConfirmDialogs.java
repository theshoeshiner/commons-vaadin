package org.thshsh.vaadin.entity;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;

//TODO allow apps to override this
public class ConfirmDialogs {

	public static ConfirmDialog deleteDialog(String name,Runnable r) {
		ConfirmDialog cd = new ConfirmDialog(null,"Delete "+name+" ?",VaadinIcon.TRASH.create());
		cd
		.withYesButton()
		//.withIcon(VaadinIcon.TRASH.create())
		.withVariants(ButtonVariant.LUMO_PRIMARY)
		
		.with(VaadinIcon.TRASH.create(),r);
		cd.withNoButton();
		//.withIcon(null);
		//cd.open(); 
		return cd;
	}
	
}
