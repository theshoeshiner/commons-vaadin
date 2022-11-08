package org.thshsh.vaadin.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.Upload;

import elemental.json.JsonObject;

@SuppressWarnings("serial")
public class UploadFieldUpload extends Upload {

	protected static final Logger LOGGER = LoggerFactory.getLogger(UploadFieldUpload.class);
	

	public UploadFieldUpload() {
		super();
	}

	public UploadFieldUpload(Receiver receiver) {
		super(receiver);
	}

	@Override
	protected void uploadFiles(JsonObject files) {
		super.uploadFiles(files);
	}

	@Override
	protected void addToFileList(Component... components) {
		super.addToFileList(components);
	}

	
}
