package org.thshsh.vaadin.progress;

import com.vaadin.flow.component.HasText;
import com.vaadin.flow.component.UI;

public class SegmentedProgressBarReporter extends ProgressReporter {
	
	protected SegmentedProgressBar progressBar;
	protected HasText message;
	protected UI ui;

	public SegmentedProgressBarReporter(UI ui,SegmentedProgressBar progressBar,HasText message) {
		super();
		this.ui = ui;
		this.progressBar = progressBar;
		this.message = message;
	}

	
	@Override
	public void increment(double val) {
		increment(val,null);
	}


	@Override
	public void increment(double val,String message) {
		int inc = (int)val;
		ui.access(()->{
			if(message != null) this.message.setText(message);
			progressBar.setValue(progressBar.getValue()+inc);
		});
	}



	@Override
	public void incrementMax(double val) {
		int inc = (int)val;
		ui.access(()->{
			progressBar.setMax(inc+progressBar.getMax());
		});
	}


	@Override
	public void message(String message) {
		if(message != null) {
			ui.access(()->{
				this.message.setText(message);
			});
		}
	}

}
