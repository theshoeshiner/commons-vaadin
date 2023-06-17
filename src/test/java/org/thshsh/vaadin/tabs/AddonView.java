package org.thshsh.vaadin.tabs;

import org.thshsh.vaadin.tabsheet.BasicTabSheet;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tabs.Orientation;
import com.vaadin.flow.router.Route;

/**
 * TODO
 *  
 */
@Route("")
public class AddonView extends Div {
	
	
	

    public AddonView() {

        BasicTabSheet hor = createSheet();
        hor.setOrientation(Orientation.HORIZONTAL);
        add(hor);
        
        BasicTabSheet vert = createSheet();
        vert.setOrientation(Orientation.VERTICAL);
        add(vert);
        
        
    }
    
    public BasicTabSheet createSheet() {
        BasicTabSheet tabsheet = new BasicTabSheet();
        //add(tabsheet);
        tabsheet.addTab("Tab1", new Span("my content1"));
        tabsheet.addTab("Tab2",VaadinIcon.ANCHOR.create(), new Span("my content2"));
        tabsheet.addTab("Tab3", new Span("my content3"));
        
        //tabsheet.getTabs().setOrientation(Orientation.VERTICAL);
        return tabsheet;
    }
    
   
}
