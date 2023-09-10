package com.postinforg.annoscoreapi.model;

public class ClsInfo {
	private String labelingDir;
	private String labelingFile;
	private String clsName;
	private String shapeType;
	private int cnt = 1;
	
	public ClsInfo() {
		
	}
	public ClsInfo(String labelingDir, String labelingFile, String clsName, String shapeType) {
		this.labelingDir = labelingDir;
		this.labelingFile = labelingFile;
		this.clsName = clsName;
		this.shapeType = shapeType;
	}
	
	public String getLabelingDir() {
		return labelingDir;
	}
	public void setLabelingDir(String labelingDir) {
		this.labelingDir = labelingDir;
	}
	public String getLabelingFile() {
		return labelingFile;
	}
	public void setLabelingFile(String labelingFile) {
		this.labelingFile = labelingFile;
	}
	public String getClsName() {
		return clsName;
	}
	public void setClsName(String clsName) {
		this.clsName = clsName;
	}
	public String getShapeType() {
		return shapeType;
	}
	public void setShapeType(String shapeType) {
		this.shapeType = shapeType;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public void increaseCnt() {
		this.cnt ++;
	}
	
	@Override
	public String toString() {
		return "ClsInfo [labelingDir=" + labelingDir + ", labelingFile=" + labelingFile + ", clsName=" + clsName
				+ ", shapeType=" + shapeType + ", cnt=" + cnt + "]";
	}
	
}
