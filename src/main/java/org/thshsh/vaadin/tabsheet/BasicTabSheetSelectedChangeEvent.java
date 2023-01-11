package org.thshsh.vaadin.tabsheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.tabs.Tabs.SelectedChangeEvent;

@SuppressWarnings("serial")
public class BasicTabSheetSelectedChangeEvent extends SelectedChangeEvent {
	
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicTabSheetSelectedChangeEvent.class);


	protected Boolean handled = false;
	protected ContinueSelectedChangeAction continueAction;
	
	
	public BasicTabSheetSelectedChangeEvent(SelectedChangeEvent e) {
		super(e.getSource(),e.getPreviousTab(),e.isFromClient());
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
         return (BasicTab) super.getSelectedTab();
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
				getSource().setSelectedTab(getSelectedTab());
			}
			else {
				LOGGER.debug("event was not handled so reseeting postpone status");
				continueAction = null;
			}
		}
		
	}
}
