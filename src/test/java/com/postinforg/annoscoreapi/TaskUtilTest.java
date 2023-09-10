package com.postinforg.annoscoreapi;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.postinforg.annoscoreapi.util.AssignmentUtil;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class TaskUtilTest {
	
	static final Logger logger = LoggerFactory.getLogger(TaskUtilTest.class);

	AssignmentUtil assignUtil; 
	public TaskUtilTest() {
		this.assignUtil = new AssignmentUtil();
	}
	
	public static void main(String[] args) {
		TaskUtilTest test = new TaskUtilTest();
		
		test.test3();
	}
	
	public void test1() {
		EgovMap eMap = new EgovMap();
		eMap.put("imagedir", "/aaihq/annoscore2-data/voll-dataset_yolov5/images");
		eMap.put("imagefile", "volleyball_ag_00001_001_00001.jpg");
		eMap.put("annotationdir", "/aaihq/annoscore2-data/voll-dataset_yolov5/pred");
		eMap.put("annotationfile", "volleyball_ag_00001_001_00001.json");
		
		Map task = new HashMap();
		task.put("taskno", 1);
		
		assignUtil.gtCopy(eMap, task, "/aaihq/annoscore2-task/");
	}
	
	public void test2() {
		String dir = "/aaihq/annoscore2-data/voll-dataset_yolov5/";
		
		File f = new File(dir);
		long size = FileUtils.sizeOfDirectory(f);
		System.out.println(">>> size : " + size);
		System.out.println(">>> size : " + size/1024/1024);
	}

	private String subDouble(String s) {
		int idx = s.indexOf(".");
		System.out.println(">>> idx : " + idx);
		if(idx >= 0 && idx + 3 < s.length()) {
			return s.substring(0, idx+4);
		}
		return s;
	}
	
	private String subInt(String s) {
		int idx = s.indexOf(".");
		System.out.println(">>> idx : " + idx);
		if(idx >= 0) {
			return s.substring(0, idx);
		}
		return s;
	}
	
	public void test3() {
		String src = "0.0";
		System.out.println(">>> result : " + subDouble(src));
		System.out.println(">>> result : " + subInt(src));
	}
	
}
