package com.postinforg.annoscoreapi.model;

import java.io.File;

import com.postinforg.annoscoreapi.util.StrNumUtil;

public class FileObject {
	
	String filename;
	String type;
	String updatetime;
	long filesize;

	public FileObject(File f) {
		this.filename = f.getName();
		this.type = f.isFile() ? "file" : "dir";
		this.updatetime = StrNumUtil.getTimeString(StrNumUtil.DATETIME_FORMAT, f.lastModified());
		this.filesize = f.length() / 1024; 	// kb
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}
	
	
}
