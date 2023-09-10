package com.postinforg.annoscoreapi.model;

public class Export {
	private int no;
	private int assignmentno;
	private int datasetno;
	private int odatasetno;
	private String menu;
	private String exportformat;
	private int tasklimit;
	private int splitsize;
	private String subdir;
	private String filename;
	private String ziptype;
	private String status;
	private int totalCount;
	private int currentCount;
	private String creationtime;
	private String udpatetime;

	public static final int DEFAULT_CSV_LIMIT = 50000;
	public static final int DEFAULT_TASK_LIMIT = 10000;
	public static final int DEFAULT_SPLIT_SIZE = 1000;
	public static final String DEFAULT_FORMAT = "CVAT";
	
	public Export() {
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public int getAssignmentno() {
		return assignmentno;
	}

	public void setAssignmentno(int assignmentno) {
		this.assignmentno = assignmentno;
	}

	public int getDatasetno() {
		return datasetno;
	}

	public void setDatasetno(int datasetno) {
		this.datasetno = datasetno;
	}

	public int getOdatasetno() {
		return odatasetno;
	}

	public void setOdatasetno(int odatasetno) {
		this.odatasetno = odatasetno;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public String getExportformat() {
		return exportformat;
	}

	public void setExportformat(String exportformat) {
		this.exportformat = exportformat;
	}

	public int getTasklimit() {
		return tasklimit;
	}

	public void setTasklimit(int tasklimit) {
		this.tasklimit = tasklimit;
	}

	public int getSplitsize() {
		return splitsize;
	}

	public void setSplitsize(int splitsize) {
		this.splitsize = splitsize;
	}

	public String getSubdir() {
		return subdir;
	}

	public void setSubdir(String subdir) {
		this.subdir = subdir;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getZiptype() {
		return ziptype;
	}

	public void setZiptype(String ziptype) {
		this.ziptype = ziptype;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getCurrentCount() {
		return currentCount;
	}

	public void setCurrentCount(int currentCount) {
		this.currentCount = currentCount;
	}

	public String getCreationtime() {
		return creationtime;
	}

	public void setCreationtime(String creationtime) {
		this.creationtime = creationtime;
	}

	public String getUdpatetime() {
		return udpatetime;
	}

	public void setUdpatetime(String udpatetime) {
		this.udpatetime = udpatetime;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Export [no=");
		builder.append(no);
		builder.append(", assignmentno=");
		builder.append(assignmentno);
		builder.append(", datasetno=");
		builder.append(datasetno);
		builder.append(", odatasetno=");
		builder.append(odatasetno);
		builder.append(", menu=");
		builder.append(menu);
		builder.append(", exportformat=");
		builder.append(exportformat);
		builder.append(", tasklimit=");
		builder.append(tasklimit);
		builder.append(", splitsize=");
		builder.append(splitsize);
		builder.append(", subdir=");
		builder.append(subdir);
		builder.append(", filename=");
		builder.append(filename);
		builder.append(", ziptype=");
		builder.append(ziptype);
		builder.append(", status=");
		builder.append(status);
		builder.append(", totalCount=");
		builder.append(totalCount);
		builder.append(", currentCount=");
		builder.append(currentCount);
		builder.append(", creationtime=");
		builder.append(creationtime);
		builder.append(", udpatetime=");
		builder.append(udpatetime);
		builder.append("]");
		return builder.toString();
	}

}
