package org.thshsh.vaadin.tabs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thshsh.vaadin.tabsheet.BasicTab;
import org.thshsh.vaadin.tabsheet.BasicTabSheet;
import org.thshsh.vaadin.tabsheet.BasicTabSheet.Orientation;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;

/**
 * TODO
 *  
 */
@Route("")
public class AddonView extends Div {
	
	
    private static final Logger LOGGER = LoggerFactory.getLogger(AddonView.class);

	

    public AddonView() {
        
        {
            BasicTabSheet hor = createSheet();
            hor.setOrientation(Orientation.Horizontal);
            add(hor);
        }
        
        add(new Hr());
        
        {
            BasicTabSheet vert = createSheet();
            vert.setOrientation(Orientation.Vertical);
            add(vert);
        }
        
        add(new Hr());
        
        {
            BasicTabSheet vert = createSheet();
            vert.setDraggable(true);
            vert.setOrientation(Orientation.Vertical);
            add(vert);
        }
        
        
    }
    
    public BasicTabSheet createSheet() {
        BasicTabSheet tabsheet = new BasicTabSheet();
        //add(tabsheet);
        
        Icon icon = VaadinIcon.ALARM.create();
        BasicTab dragTab = tabsheet.addTab("Tab1",icon, new Span("my content1"));
        //DragSource<BasicTab> tabDragSource = DragSource.create(dragTab);
        //tabDragSource.setDraggable(true);
        icon = VaadinIcon.ANCHOR.create();
        icon.addClassName(BasicTabSheet.getDragHandleClass());
        tabsheet.addTab("Tab2",icon, new Span("my content2"));
        
        icon = VaadinIcon.MAILBOX.create();
        icon.addClassName(BasicTabSheet.getDragHandleClass());
        tabsheet.addTab("Tab3", icon, new Span("my content3"));
        
        icon = VaadinIcon.STOPWATCH.create();
        icon.addClassName(BasicTabSheet.getDragHandleClass());
        tabsheet.addTab("Tab4", icon ,new Span("my content 4"));
        
        tabsheet.setSelectedIndex(1);
        
        //tabsheet.getTabs().setOrientation(Orientation.VERTICAL);
        return tabsheet;
    }
    
   
}
