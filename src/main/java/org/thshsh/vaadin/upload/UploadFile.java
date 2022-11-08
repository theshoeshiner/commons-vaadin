package org.thshsh.vaadin.upload;

import java.io.FileDescriptor;



public class UploadFile {

	String mimeType;
	String name;
	byte[] data;
	FileDescriptor descriptor;
	
	public UploadFile() {}

	public UploadFile(String name, String mimeType, byte[] data,FileDescriptor descriptor) {
		super();
		this.name = name;
		this.mimeType = mimeType;
		this.data = data;
		this.descriptor = descriptor;
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
		builder.append(", descriptor=");
		builder.append(descriptor);
		builder.append("]");
		return builder.toString();
	}
	
	
}
