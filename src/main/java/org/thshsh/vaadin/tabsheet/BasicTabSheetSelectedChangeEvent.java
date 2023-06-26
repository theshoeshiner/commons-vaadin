package org.thshsh.vaadin.tabsheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEvent;

@SuppressWarnings("serial")
public class BasicTabSheetSelectedChangeEvent extends ComponentEvent<BasicTabSheet> {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicTabSheetSelectedChangeEvent.class);

	protected ClickEvent<BasicTab> clickEvent;
	protected Boolean handled = false;
	protected ContinueSelectedChangeAction continueAction;
	protected BasicTab selectedTab;
	protected BasicTab previousTab;
	protected BasicTabSheet tabSheet;
	
	public BasicTabSheetSelectedChangeEvent(ClickEvent<BasicTab> e,BasicTab previous) {
		super(e.getSource().getTabSheet(),e.isFromClient());
	    this.clickEvent = e;
	    this.selectedTab = e.getSource();
	    this.previousTab = previous;
	    this.selectedTab.getTabSheet();
	}


	public ContinueSelectedChangeAction postpone() {
		LOGGER.debug("postpone");
		if(continueAction == null) continueAction = new ContinueSelectedChangeAction();
		return continueAction;
	}
	
	public boolean isPostponed() {
		return continueAction != null;
	}

	public Boolean getHandled() {
		return handled;
	}

	public void setHandled(Boolean handled) {
		this.handled = handled;
	}
	
	public Boolean isSelected(BasicTab tab) {
		return tab == getSelectedTab();
	}
	
	 public BasicTab getSelectedTab() {
         return selectedTab;
     }
	 
	 

	public BasicTab getPreviousTab() {
        return previousTab;
    }

	

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[continueAction=");
		builder.append(continueAction);
		builder.append(", getSelectedTab()=");
		builder.append(getSelectedTab());
		builder.append(", getPreviousTab()=");
		builder.append(getPreviousTab());
		builder.append(", isFromClient()=");
		builder.append(isFromClient());
		builder.append("]");
		return builder.toString();
	}




    public class ContinueSelectedChangeAction {
		
		public void proceed() {
			if(handled) {
				LOGGER.debug("event was already handled so proceeding to {}",getSelectedTab());
				tabSheet.setSelectedTab(getSelectedTab());
			}
			else {
				LOGGER.debug("event was not handled so reseeting postpone status");
				continueAction = null;
			}
		}
		
	}
}
