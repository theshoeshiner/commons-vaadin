package org.thshsh.vaadin.progress;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;

@CssImport("segmented-progress-bar.css")
public class SegmentedProgressBar extends HorizontalLayout {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SegmentedProgressBar.class);

	private static final long serialVersionUID = -4842099998398517938L;
	
	protected int max = 1;
	protected int value = 0;
	protected int segmentWidth;
	protected List<ProgressBar> segments = new ArrayList<>();
	
	public SegmentedProgressBar() {
    	this.addClassName("segmented-progress-bar");
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
		this.segmentWidth = 100/max;
		updateSegments();
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		updateValue();
	}

	protected void updateValue() {
		for(int i=0;i<max;i++) {
			ProgressBar segmentBar  = segments.get(i);
			if(i<value) {
				segmentBar.setValue(1);
				segmentBar.setIndeterminate(false);
			}
			else if(i>value) {
				segmentBar.setValue(0);
				segmentBar.setIndeterminate(false);
			}
			else {
				segmentBar.setIndeterminate(true);
			}
		}
	}
	
	protected void updateSegments() {
		if(segments.size() != max) {
			LOGGER.debug("updating segments");
			this.remove(segments.toArray(new Component[segments.size()]));
			segments.clear();
			for(int i=0;i<max;i++) {
		    	ProgressBar segmentBar = new ProgressBar();
		    	segmentBar.setHeight("10px");
		    	segmentBar.addClassName("segment-progress-bar");
		    	if(i==0) segmentBar.addClassName("first");
		    	if(i+1==max) segmentBar.addClassName("last");
		    	segmentBar.setWidth(segmentWidth+"%");
		    	this.add(segmentBar);
		    	segments.add(segmentBar);
	    	}
		}
	}
	
	
	
}
