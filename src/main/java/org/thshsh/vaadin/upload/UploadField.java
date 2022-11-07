package org.thshsh.vaadin.upload;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;

@SuppressWarnings("serial")
public class UploadField extends CustomField<UploadFile> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(UploadField.class);

    protected InputStream is;
    byte[] data;
    protected MemoryBuffer buffer;
    protected SucceededEvent event;
    UploadFile value;
    Upload upload;

    public UploadField() {
    	this(new MemoryBuffer());
    }
    
    public UploadField(MemoryBuffer b) {
    	buffer = b;
        upload = new Upload(b);
     
        
        //upload.set
       
        upload.addSucceededListener(e -> {
        	try {
				event = e;
				is = buffer.getInputStream();
				data = IOUtils.toByteArray(is);
				LOGGER.info("got stream: {}",is);
				this.setModelValue(generateModelValue(), true);
			} catch (IOException e1) {
				
				throw new IllegalStateException(e1);
			}
            
            
        });
        upload.setWidthFull();
        add(upload);            
    }
    
    public String getFileName() {
        return buffer.getFileName();
    }

    @Override
    protected UploadFile generateModelValue() {
    	LOGGER.info("generateModelValue");
    	
    	return new UploadFile(event.getMIMEType(), event.getFileName(), data);
    }

    @Override
    protected void setPresentationValue(UploadFile newPresentationValue) {
    	LOGGER.info("setPresentationValue: {}",newPresentationValue);
    	this.value = newPresentationValue;
    }
    

	@Override
	protected boolean valueEquals(UploadFile value1, UploadFile value2) {
		boolean b = super.valueEquals(value1, value2);
		LOGGER.info("valueEquals: {} - {} = {}",value1,value2,b);
		return b;
	}

	@Override
	public void setValue(UploadFile value) {
		LOGGER.info("setValue: {}",value);
		super.setValue(value);
		LOGGER.info("setValue exit");
	}

	@Override
	protected void setModelValue(UploadFile newModelValue, boolean fromClient) {
		LOGGER.info("setModelValue: {} fromclient: {}",newModelValue,fromClient);
		super.setModelValue(newModelValue, fromClient);
	}

	@Override
	public UploadFile getValue() {
		
		UploadFile value = super.getValue();
		LOGGER.info("getValue: {}",value);
		return super.getValue();
	}
    
    
}