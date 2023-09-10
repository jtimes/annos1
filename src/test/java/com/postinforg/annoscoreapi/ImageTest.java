package com.postinforg.annoscoreapi;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.postinforg.annoscoreapi.model.UploadzipFile;
import com.postinforg.annoscoreapi.util.FileImageUtil;

public class ImageTest {

	public static void main(String[] args) {
		ImageTest test = new ImageTest();
//		test.test1();
//		test.test2();
		test.test3();
	}

	public void test1() {
		String file = "D:\\aaihq\\upload_data\\test1\\images\\volleyball_ag_00001_001_00001.jpg";
		File f = new File(file);
		Map res = FileImageUtil.getAllExifData(f);
		
		Iterator it = res.keySet().iterator();
		while(it.hasNext()) {
			String key = (String)it.next();
			System.out.println(key + " : " + res.get(key));
		}
	}
	
	public void test2() {
		String file = "D:\\aaihq\\upload_data\\test1\\images\\volleyball_ag_00001_001_00001.jpg";
		File f = new File(file);
		UploadzipFile imgObj = new UploadzipFile();
		FileImageUtil.getExifLabelData(f, imgObj);
		
		file = "D:\\aaihq\\upload_data\\test1\\pred\\volleyball_ag_00001_001_00002.json";
		imgObj.setJsonPath("D:\\aaihq\\upload_data\\test1\\pred\\");
		imgObj.setJsonName("volleyball_ag_00001_001_00002.json");
		f = new File(file);
		FileImageUtil.getJsonLabelData(f, imgObj, new ObjectMapper(), "D:\\aaihq\\annoscore2-labeling\\test1\\pred\\");
		
		System.out.println("result");
		System.out.println(imgObj);
		System.out.println(imgObj.getClsList());
	}
	
	public void test3() {
		int a = 1280;
		int b = 720;
		
		double c = Math.round(a * 100 / b);
		System.out.println(c / 100);
	}
}
