package org.thshsh.vaadin.upload;

import java.io.File;



public class UploadFile {

	String mimeType;
	String name;
	byte[] data;
	File file;
	
	public UploadFile() {}

	public UploadFile(String name, String mimeType, byte[] data,File f) {
		super();
		this.name = name;
		this.mimeType = mimeType;
		this.data = data;
		this.file = f;
	}

	public String getMimeType() {
		return mimeType;
	}

	public String getName() {
		return name;
	}

	public byte[] getData() {
		return data;
	}

	public File getFile() {
		return file;
	}

	public boolean hasData() {
		return data != null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UploadFile [mimeType=");
		builder.append(mimeType);
		builder.append(", name=");
		builder.append(name);
		builder.append(", data=");
		builder.append(data==null?null:data.length);
		builder.append(", file=");
		builder.append(file);
		builder.append("]");
		return builder.toString();
	}
	
	
}
