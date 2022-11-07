package org.thshsh.vaadin.upload;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

/**
 * Embedded type that wraps values commonly necessary for file storage.
 *
 */
@Embeddable
public class UploadFile {

	@Column
	String mimeType;
	
	@Column
	String name;
	
	@Column()
	@Lob
	byte[] data;
	
	public UploadFile() {}

	public UploadFile(String name, String mimeType, byte[] data) {
		super();
		this.name = name;
		this.mimeType = mimeType;
		this.data = data;
	}



	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public boolean hasData() {
		return data != null;
	}
	
}
