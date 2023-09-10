package com.postinforg.annoscoreapi.model;

import java.util.List;

public class UploadzipFile {

	private String jsonPath;
	private String jsonName;
	private String imagePath;
	private String imageName;
	
	private int imageWidth;
	private int imageHeight;
	private long imageSize;
	private int imageValidity = 0;
	private String imageDate;
	private String imageUpdate;
	private double imageRatio;
	
	private long jsonSize;
	private int jsonValidity = 0;
	private String jsonDate;
	private String jsonUpdate;
	
	private List<ClsInfo> clsList;
	
	public UploadzipFile() {
	}
	
	public UploadzipFile(String jpath, String jname, String ipath, String iname) {
		this.jsonName = jname;
		this.jsonPath = jpath;
		this.imageName = iname;
		this.imagePath = ipath;
	}

	public String getJsonPath() {
		return jsonPath;
	}

	public void setJsonPath(String jsonPath) {
		this.jsonPath = jsonPath;
	}

	public String getJsonName() {
		return jsonName;
	}

	public void setJsonName(String jsonName) {
		this.jsonName = jsonName;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public long getImageSize() {
		return imageSize;
	}

	public void setImageSize(long imageSize) {
		this.imageSize = imageSize;
	}

	public int getImageValidity() {
		return imageValidity;
	}

	public void setImageValidity(int imageValidity) {
		this.imageValidity = imageValidity;
	}

	public String getImageDate() {
		return imageDate;
	}

	public void setImageDate(String imageDate) {
		this.imageDate = imageDate;
	}

	public String getImageUpdate() {
		return imageUpdate;
	}

	public void setImageUpdate(String imageUpdate) {
		this.imageUpdate = imageUpdate;
	}

	public double getImageRatio() {
		return imageRatio;
	}

	public void setImageRatio(double imageRatio) {
		this.imageRatio = imageRatio;
	}

	public long getJsonSize() {
		return jsonSize;
	}

	public void setJsonSize(long jsonSize) {
		this.jsonSize = jsonSize;
	}

	public int getJsonValidity() {
		return jsonValidity;
	}

	public void setJsonValidity(int jsonValidity) {
		this.jsonValidity = jsonValidity;
	}

	public String getJsonDate() {
		return jsonDate;
	}

	public void setJsonDate(String jsonDate) {
		this.jsonDate = jsonDate;
	}

	public String getJsonUpdate() {
		return jsonUpdate;
	}

	public void setJsonUpdate(String jsonUpdate) {
		this.jsonUpdate = jsonUpdate;
	}
	
	public List<ClsInfo> getClsList() {
		return clsList;
	}

	public void setClsList(List<ClsInfo> clsList) {
		this.clsList = clsList;
	}

	@Override
	public String toString() {
		return "UploadzipFile [jsonPath=" + jsonPath + ", jsonName=" + jsonName + ", imagePath=" + imagePath
				+ ", imageName=" + imageName + ", imageWidth=" + imageWidth + ", imageHeight=" + imageHeight
				+ ", imageSize=" + imageSize + ", imageValidity=" + imageValidity + ", imageDate=" + imageDate
				+ ", imageUpdate=" + imageUpdate + ", imageRatio=" + imageRatio + ", jsonSize=" + jsonSize
				+ ", jsonValidity=" + jsonValidity + ", jsonDate=" + jsonDate + ", jsonUpdate=" + jsonUpdate + "]";
	}
	
		
}
