package com.postinforg.annoscoreapi.util;

import java.io.File;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;

public class StrNumUtil {
	
	public static final String DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

	public static int getRandomNum(int startNum, int endNum) {
		int randomNum = 0;

		// 랜덤 객체 생성
		SecureRandom rnd = new SecureRandom();

		do {
			// 종료숫자내에서 랜덤 숫자를 발생시킨다.
			randomNum = rnd.nextInt(endNum + 1);
		} while (randomNum < startNum); // 랜덤 숫자가 시작숫자보다 작을경우 다시 랜덤숫자를 발생시킨다.

		return randomNum;
    }

	public static String getNow(String formatStr) {
		return getTimeString(formatStr, System.currentTimeMillis());
	}
	
	public static String getTimeString(String formatStr, long millis) {
		Date now = new Date(millis); 
		SimpleDateFormat simpledateformat = 
				new SimpleDateFormat(formatStr, Locale.getDefault(Locale.Category.FORMAT));
		return simpledateformat.format(now);
	}
	
	public static String getTimeStamp() { 
		String pattern = "yyyyMMddhhmmssSSS";
		return getNow(pattern);
    }
	
	public static String getTimeStampMin() { 
		String pattern = "hhmmssSSS";
		return getNow(pattern);
	}
	
	public static synchronized String exchangeExtName(String orgname, String extName) {
		if(orgname == null || orgname.equals("")) return orgname;
		String newname = orgname;
		
		String[] arr = orgname.split("[.]");
		if(arr.length > 1) {
			String ext = arr[arr.length-1];
			newname = orgname.substring(0, orgname.indexOf("." + ext)) + "." + extName; 
		}
		
		return newname;
	}
	
	public static synchronized String getNewName(String orgname) {
		if(orgname == null || orgname.equals("")) return orgname;
		
		String newname = getTimeStampMin() + getRandomNum(10, 99);
		
		String[] arr = orgname.split("[.]");
		if(arr.length > 1) {
			String ext = arr[arr.length-1];
			newname = orgname.substring(0, orgname.indexOf("." + ext)) + "_" + newname + "." + ext; 
		}
		else {
			newname = orgname + newname;
		}
		
		return newname;
	}
	
	public static synchronized String excludeExtName(String orgname) {
		if(orgname == null || orgname.equals("")) return orgname;
		String newname = "";
		
		String[] arr = orgname.split("[.]");
		if(arr.length > 1) {
			String ext = arr[arr.length-1];
			newname = orgname.substring(0, orgname.indexOf("." + ext)); 
		}
		else {
			newname = orgname;
		}
		
		return newname;
	}

	public static boolean fileExist(String f) {
		File file = new File(f);
		if(file.exists() && file.isFile()) return true;
		return false;
	}
	

	public static List<String> getDirList(String path) {
		List<String> l = new ArrayList<String>();
		try {
			File dir = new File(path);
			if(dir.isDirectory()) {
				for(File f : dir.listFiles()) {
					if(f.isDirectory()) {
						l.add(f.getName());
					}
				}
			}
			l.sort(Comparator.naturalOrder());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return l;
	}
	
	public static List<String> getFileList(String path) {
		List<String> l = new ArrayList<String>();
		try {
			File dir = new File(path);
			if(dir.isDirectory()) {
				for(File f : dir.listFiles()) {
					if(f.isFile()) {
						l.add(f.getName());
					}
				}
			}
			l.sort(Comparator.naturalOrder());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return l;
	}

	public static boolean isDatetime(String src) {
		if(src.length() != 14) return false;
		if(!src.startsWith("20")) return false;
		try {
			Long l = Long.parseLong(src);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	public static String fileSize(long size) {
		String result = "";
		
		long temp = size / 1024;
		if(temp > 1000) {
			temp = temp / 1024;
			if(temp > 1000) {
				temp = temp / 1024;
				if(temp > 1000) {
					result = (temp / 1024) + " GB";
				}
				else {
					result = temp + " MB";
				}
			}
			else {
				result = temp + " KB";
			}
		}
		else {
			result = temp + " B";
		}
		
		return result;
	}
	
	public static String getFileSize(EgovMap emap, String key) {
		if(emap.containsKey(key)) {
			if(emap.get(key) != null) {
				try {
					long size = Long.parseLong(emap.get(key).toString());
					return fileSize(size);
				} catch(Exception e) { e.printStackTrace(); }
			}
		}
		
		return "0 B";
	}
	
	public static String getFileSizeK(EgovMap emap, String key) {
		if(emap.containsKey(key)) {
			if(emap.get(key) != null) {
				try {
					long size = Long.parseLong(emap.get(key).toString()) * 1000;
					return fileSize(size);
				} catch(Exception e) { e.printStackTrace(); }
			}
		}
		
		return "0 B";
	}
	
	public static String getValue(EgovMap emap, String key) {
		if(emap.containsKey(key)) {
			if(emap.get(key) != null)
				return emap.get(key).toString();
		}
		return "";
	}
	
	public static String getIntValue(Map emap, String key) {
		if(emap.containsKey(key)) {
			if(emap.get(key) != null) {
				String s = emap.get(key).toString();
				int idx = s.indexOf(".");
				if(idx >= 0) {
					return s.substring(0, idx);
				}
				return s;
			}
		}
		return "0";
	}
	
	public static int getIntVal0(Map emap, String key) {
		String ii = getIntValue(emap, key);
		
		return Integer.parseInt(ii);
	}
	
	public static String getValidValue(EgovMap emap, String key) {
		if(emap.containsKey(key) && emap.get(key).toString().equals("0"))
			return "유효";
		
		return "무효";
	}
	
	public static String getDoubleValue(EgovMap emap, String key) {
		if(emap.containsKey(key)) {
			if(emap.get(key) != null) {
				String s = emap.get(key).toString();
				int idx = s.indexOf(".");
				if(idx >= 0 && idx + 2 < s.length()) {
					return s.substring(0, idx+3);
				}
				return s;
			}
		}
		return "0.00";
	}
	
	public static String getDoubleValue3(EgovMap emap, String key) {
		if(emap.containsKey(key)) {
			if(emap.get(key) != null) {
				String s = emap.get(key).toString();
				int idx = s.indexOf(".");
				if(idx >= 0 && idx + 3 < s.length()) {
					return s.substring(0, idx+4);
				}
				return s;
			}
		}
		return "0.000";
	}
	
}
