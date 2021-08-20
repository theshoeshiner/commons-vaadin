package org.thshsh.vaadin.entity;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

//TODO allow apps to override this
public class ConfirmDialogs {

	public static ConfirmDialog deleteDialog(String name,Runnable r) {
		ConfirmDialog cd = new ConfirmDialog(null,"Delete "+name+" ?",VaadinIcon.TRASH.create());
		cd
		.withYesButton()
		//.withIcon(VaadinIcon.TRASH.create())
		.withVariants(ButtonVariant.LUMO_PRIMARY)
		.withKey(Key.ENTER)
		.with(VaadinIcon.TRASH.create(),r);
		cd.withNoButton();
		//.withIcon(null);
		//cd.open(); 
		return cd;
	}
	
	public static ConfirmDialog messageDialog(String title,String message) {
		ConfirmDialog cd = new ConfirmDialog(title,message,VaadinIcon.INFO_CIRCLE.create());
		return cd;
	}
	
	public static ConfirmDialog yesNoDialog(String message,Runnable r) {
		return yesNoDialog(null, message, VaadinIcon.QUESTION.create(), r);
	}
	
	public static ConfirmDialog yesNoDialog(String header,String message,Icon icon,Runnable r) {
		ConfirmDialog cd = new ConfirmDialog(header,message,icon);
		cd
		.withYesButton()
		.with(r)
		.withVariants(ButtonVariant.LUMO_PRIMARY)
		.withKey(Key.ENTER)
		;
		cd.withNoButton();
		return cd;
	}
	
	
}
