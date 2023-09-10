package com.postinforg.annoscoreapi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class UtilTest {

	public static void main(String[] args) {
		UtilTest test = new UtilTest();
		//test.test6();
//		test.test7();
	}

	public void test1() {
		String file = "/aaihq/annoscore2-data/voll-dataset_yolov5/images/volleyball_ag_00001_004_00023.jpg";
		File f = new File(file);
		if(f.exists()) {
			System.out.println(" file exist !");
		}
		else {
			System.out.println(" file not exist !");
		}
	}
	
	public void test2() {
		String srcFile = "C:\\aaihq\\annoscore2-data\\voll-dataset_yolov5\\upload\\20221226_94447.zip";
		String newDir = "C:\\aaihq\\annoscore2-data\\voll-dataset_yolov5\\upload\\";
		
		String[] cmd = new String[] {"cmd", "/c", "alzipcon", "-x", "-oa"
					, srcFile, newDir};
		
		Process process;
		try {
			process = new ProcessBuilder(cmd).start();
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String str = null;
			while((str = stdOut.readLine()) != null) {
				System.out.println(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void test3() {
//		String src = "D:\\202212_Annos2\\annoscore2-ftp\\20230101_51090";
//		String savefilename = src.substring(src.length()-14);
//		System.out.println(savefilename);
		
		String src = "ap_50";
		System.out.println(">>> " + src.substring(0, src.length() -3));
		
		Gson gson = new Gson();
		File f = new File("C:\\aaihq\\annoscore-task\\0000028\\eval\\86\\cls\\ball_90.json");
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(f));
			Map json = gson.fromJson(bufferedReader, Map.class);
//			System.out.println("json : " + json);
			System.out.println("temp : ");
			List l = (List)json.get("interpolated_recall_precision_curve");
			System.out.println("l : " + l.get(0));
			System.out.println("l2 : " + gson.toJson(l).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	
//	public void test4() {
//		AssignmentUtil u = new AssignmentUtil();
//		String src = "/task/number/my-data/p_id";
//		String res = u.getTaskBgTempLocation(src);
//		System.out.println(" res : " + res);
//	}
	
	public void test5() {
		String topath = "test.zip";
		topath = topath.substring(0, topath.length()-4);
		
		System.out.println("topath : " + topath);
	}
	
	public void test6() {
		String topath = "/aaihq/annoscore-assignment/0000012/incoming/temp/test2.zip";
		File f = new File(topath);
		String strpath = "";
		try {
			strpath = f.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("strpath : " + strpath);
		strpath = strpath.replaceAll("\\\\", "/");
		System.out.println("strpath : " + strpath);
		if(strpath.indexOf(":") > 0) {
			strpath = strpath.substring(strpath.indexOf(":") + 1);
			System.out.println("strpath : " + strpath);
		}
	}
	
	public void test7() {
		String classFileName = "abcd_50.json";
		String[] aNames = classFileName.split("_");
		if(aNames.length > 0) {
			int nIdx = classFileName.indexOf(aNames[aNames.length-1]);
			classFileName = classFileName.substring(0, nIdx-1);
		}
		
		System.out.println("> " + classFileName);
	}
	
}
