package org.thshsh.vaadin.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.AllFinishedEvent;
import com.vaadin.flow.component.upload.Receiver;
import com.vaadin.flow.component.upload.StartedEvent;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileData;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileBuffer;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;

@SuppressWarnings("serial")
@CssImport(value = "upload-field.css")
@CssImport(value = "upload-field-vaadin-upload.css", themeFor = "vaadin-upload")
@CssImport(value = "upload-field-vaadin-upload-file.css", themeFor = "vaadin-upload-file")
public class UploadField extends CustomField<List<UploadFile>> {

	public static final Logger LOGGER = LoggerFactory.getLogger(UploadField.class);

	public static final String CSS_CLASS_INPROGRESS = "in-progress";
	public static final String CSS_CLASS = "upload-field";
	public static final String CSS_CLASS_REMOVED = "removed";
	public static final String CSS_CLASS_REMOVE = "remove";
	public static final String CSS_CLASS_FILE = "file";


	protected Receiver receiver;
	protected Upload upload;
	protected Boolean allowDuplicateNames = false;
	protected List<UploadFile> files;
	protected Map<UploadFile,FlexComponent> fileLayouts;
	protected VerticalLayout layout;
	protected Integer maxFiles;

	public UploadField() {
		this(new MultiFileBuffer());

	}

	
	public UploadField(Receiver b) {
		this.receiver = b;
		this.addClassName(CSS_CLASS);
		

		fileLayouts = new HashMap<>();
		files = new ArrayList<>();
		
		layout = new VerticalLayout();
		layout.setSpacing(false);
		layout.addClassName(CSS_CLASS);
		this.add(layout);
		
		upload = new UploadFieldUpload(b);
		upload.setWidthFull();
		upload.addClassName(CSS_CLASS);
		layout.add(upload);

		upload.addSucceededListener(this::handleSucceededEvent);
		upload.addStartedListener(this::handleStartedEvent);
		upload.addAllFinishedListener(this::handleAllFinishedEvent);
		
		this.setValue(List.of());
				
	}

	
	
	public Receiver getReceiver() {
		return upload.getReceiver();
	}

	public void setMaxFiles(Integer maxFiles) {
		upload.setMaxFiles(maxFiles);
		this.maxFiles = maxFiles;
	}


	public int getMaxFiles() {
		return upload.getMaxFiles();
	}


	public void setMaxFileSize(int maxFileSize) {
		upload.setMaxFileSize(maxFileSize);
	}


	public int getMaxFileSize() {
		return upload.getMaxFileSize();
	}


	public void setAutoUpload(boolean autoUpload) {
		upload.setAutoUpload(autoUpload);
	}


	public void setAcceptedFileTypes(String... acceptedFileTypes) {
		upload.setAcceptedFileTypes(acceptedFileTypes);
	}


	public List<String> getAcceptedFileTypes() {
		return upload.getAcceptedFileTypes();
	}

	protected void handleAllFinishedEvent(AllFinishedEvent e) {
		this.removeClassName(CSS_CLASS_INPROGRESS);
		upload.removeClassName(CSS_CLASS_INPROGRESS);
	}

	protected void handleStartedEvent(StartedEvent e) {
		this.addClassName(CSS_CLASS_INPROGRESS);
		upload.addClassName(CSS_CLASS_INPROGRESS);
	}


	protected void handleSucceededEvent(SucceededEvent e) {
		handleUploadFile(e.getFileName());
	}

	protected void handleUploadFile(String name) {

		FileData fileData;
		InputStream stream = null;
		File file = null;
		byte[] data = null;

		if (receiver instanceof MultiFileMemoryBuffer) {
			MultiFileMemoryBuffer buffer = (MultiFileMemoryBuffer) receiver;
			fileData = buffer.getFileData(name);
			stream = buffer.getInputStream(name);
		} 
		else if (receiver instanceof MultiFileBuffer) {
			MultiFileBuffer buffer = (MultiFileBuffer) receiver;
			fileData = buffer.getFileData(name);
			file = fileData.getFile();
		}
		else if (receiver instanceof MemoryBuffer) {
			MemoryBuffer buffer = (MemoryBuffer) receiver;
			fileData = buffer.getFileData();
			stream = buffer.getInputStream();
		} 
		else {
			throw new IllegalArgumentException("Unknown receiever type: " + receiver.getClass());
		}

		try {
			if(stream != null) {
				data = IOUtils.toByteArray(stream);
			}
			UploadFile uf = new UploadFile(fileData.getFileName(), fileData.getMimeType(), data,file);
			List<UploadFile> newValue = new ArrayList<>(files);
			newValue.add(uf);
			setValue(newValue);

		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void handleRemoveFile(UploadFile file) {
		List<UploadFile> newValue = new ArrayList<>(files);
		newValue.remove(file);
		setValue(newValue);
	}
	
	/**
	 * This only gets called from the setvalue methods and operates directly on the internal list
	 * @param uf
	 */
	protected void addFile(UploadFile uf) {
		
		LOGGER.debug("addFile: {}",uf);
		
		if(!allowDuplicateNames) {
			Optional<UploadFile> dupOpt = files.stream().filter(f -> f.getName().equals(uf.getName())).findFirst();
			if(dupOpt.isPresent()) {
				UploadFile dup = dupOpt.get();
				removeFile(dup);
			}
		}
		
		files.add(uf);

		HorizontalLayout fileLayout = new HorizontalLayout();
		fileLayout.addClassName(CSS_CLASS_FILE);
		fileLayout.setWidthFull();
		fileLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
		layout.add(fileLayout);
		
		fileLayouts.put(uf,fileLayout);

		Span span = new Span(uf.getName());
		fileLayout.add(span);

		Button rem = new Button(VaadinIcon.CLOSE_SMALL.create());
		rem.addClassName(CSS_CLASS_REMOVE);
		rem.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		fileLayout.add(rem);
		rem.addClickListener(e -> handleRemoveFile(uf));
		
	}
	
	
	
	protected void removeFile(UploadFile file) {
		
		LOGGER.debug("remove file: {}",file);
		if(files.contains(file)) {
			files.remove(file);
			if(fileLayouts.containsKey(file)) {
				FlexComponent layout = fileLayouts.get(file);
				layout.removeClassName(CSS_CLASS_FILE);
				layout.addClassName(CSS_CLASS_REMOVED);
				fileLayouts.remove(file);
			}
			/* NOTE this is a hack to handle max files
			 * Because we cannot actually remove files from native UI component, all we can do is 
			 * allos more files when files are removed from the custom field wreapper
			 */
			if(maxFiles != null) {
				upload.setMaxFiles(upload.getMaxFiles()+1);
			}
		}
		

	}

	@Override
	protected List<UploadFile> generateModelValue() {
		LOGGER.debug("generateModelValue");
		return List.copyOf(files);
	}

	
	
	@Override
	protected void setModelValue(List<UploadFile> newModelValue, boolean fromClient) {
		super.setModelValue(newModelValue, fromClient);
	}


	@Override
	protected void setPresentationValue(List<UploadFile> newPresentationValue) {
		
		LOGGER.debug("setPresentationValue: {}",newPresentationValue);
		
		if(newPresentationValue == null) newPresentationValue = List.of();

		List<UploadFile> toAdd = new ArrayList<>(newPresentationValue);
		toAdd.removeAll(files);
		
		List<UploadFile> toRemove = new ArrayList<>(files);
		toRemove.removeAll(newPresentationValue);
		
		if(toRemove.size()>0 || toAdd.size() > 0) {
			toRemove.forEach(this::removeFile);
			toAdd.forEach(this::addFile);
		}
		
	}
	

	@Override
	protected boolean valueEquals(List<UploadFile> value1, List<UploadFile> value2) {
		boolean b = super.valueEquals(value1, value2);
		return b;
	}

	
	@Override
	public List<UploadFile> getEmptyValue() {
		return List.of();
	}

	@Override
	public List<UploadFile> getValue() {
		return List.copyOf(super.getValue());
	}

	public Upload getUpload() {
		return upload;
	}
}