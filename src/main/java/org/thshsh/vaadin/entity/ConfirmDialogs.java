package org.thshsh.vaadin.entity;

import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ConfirmDialogs {

	public static ConfirmDialog deleteDialog(String name,Runnable r) {
		ConfirmDialog cd = new ConfirmDialog(null,"Delete "+name+" ?",VaadinIcon.TRASH.create());
		cd.withYesButton()
		.withVariants(ButtonVariant.LUMO_PRIMARY)
		
		.with(null,r);
		cd.withNoButton().withIcon(null);
		//cd.open(); 
		return cd;
	}
	
}
