package com.postinforg.annoscoreapi.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Component
public class AssignmentUtil {
	
	@Value("${media.datahome}") private String dataHome;
	@Value("${media.assignmenthome}") private String assignmentHome;
	
	public static final String DIR_ASSIGNMENT_EVAL = "/eval/";
	
	public static final String DIR_LABELING_IMG = "/stored/img/";
	public static final String DIR_LABELING_JSN = "/stored/json";
	public static final String DIR_LABELING_SIMG = "/img/";
	public static final String DIR_LABELING_SJSN = "/json/";
	
	public static final String DIR_ASSIGNMENT_IMG = "/model/model-data/images/";
	public static final String DIR_ASSIGNMENT_HGT = "/model/model-data/hgt/";
	
	public static final String DIR_ASSIGNMENT_MGT = "/model/.mgt/";
	public static final String DIR_ASSIGNMENT_MODEL = "/model/models/";
//	public static final String DIR_ASSIGNMENT_RAW = "/.modelraw/";
	public static final String DIR_ASSIGNMENT_INC = "/incoming/";
	public static final String DIR_ASSIGNMENT_EXT = "/extract/";
	public static final String DIR_ASSIGNMENT_STO = "/stored/";
	public static final String DIR_ASSIGNMENT_EXP = "/exported/";
	
	Logger logger = LoggerFactory.getLogger(AssignmentUtil.class);

	public static String getAssignmentDirFromNo(Map assignment) {
		return getAssignmentDirFromNo(Integer.parseInt(assignment.get("no").toString()));
	}
	
	public static String getAssignmentDirFromNo(int no) {
		return String.format("%07d", no);
	}
	
	public static String getAssignmentDirFromNo(String no) {
		return getAssignmentDirFromNo(Integer.parseInt(no.trim()));
	}
	/*
	public String getAssignmentBgTempLocation(String location) {
		String res = location;
		if(location.length() >= 14)
			res = location.substring(location.length()-14);
		
		if(res.contains("/")) res = res.substring(res.lastIndexOf("/")+1);
		
		return res;
	} */
	
	public void makeAssignmentDirs(String addDir) {
		File target = new File(addDir);
		
		logger.debug(">>> check assignment directory : " + addDir);
		if(!target.exists() || target.isFile()) {
			target.setWritable(true, false);
			target.mkdirs();
			target = new File(addDir + DIR_ASSIGNMENT_IMG);
			target.mkdirs();
			target = new File(addDir + DIR_ASSIGNMENT_HGT);
			target.mkdirs();
			target = new File(addDir + DIR_ASSIGNMENT_MGT);
			target.mkdirs();
			target = new File(addDir + DIR_ASSIGNMENT_EVAL);
			target.setWritable(true, false);
			target.mkdirs();
			target = new File(addDir + DIR_ASSIGNMENT_MODEL);
			target.setWritable(true, false);
			target.mkdirs();

//			target = new File(addDir + DIR_ASSIGNMENT_RAW);
//			target.mkdirs();
			target = new File(addDir + DIR_ASSIGNMENT_INC);
			target.setWritable(true, false);
			target.mkdirs();
//			target = new File(addDir + DIR_ASSIGNMENT_STR);
//			target.mkdirs();
			target = new File(addDir + DIR_ASSIGNMENT_EXP);
			target.mkdirs();
			target = new File(addDir + DIR_LABELING_IMG);
			target.mkdirs();
			target = new File(addDir + DIR_LABELING_JSN);
			target.mkdirs();
			
			try {
				FileImageUtil.runCommand("chmod -R 777 " + addDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void makeAnnotationDirs(String addDir) {
		logger.debug(">>> check labeling directory : " + addDir);
		File target = new File(addDir + DIR_ASSIGNMENT_IMG);
		target.mkdirs();
		target = new File(addDir + DIR_ASSIGNMENT_HGT);
		target.mkdirs();
	}
	
	public boolean gtCopy(EgovMap eMap, Map assignment, String dataDir) {
		String srcImgDir = eMap.get("imagedir").toString();
		String srcImgFile = eMap.get("imagefile").toString();
		String srcAnnoDir = eMap.get("labelingdir").toString();
		String srcAnnoFile = eMap.get("labelingfile").toString();
		
		File sFile = new File(srcImgDir + "/" + srcImgFile);
		logger.debug(">>> gtCopy : " + eMap.toString());
//		logger.debug(">>> src file : " + srcImgDir + "/" + srcImgFile);
		
		if(sFile.exists() && sFile.isFile()) {
			String assignmentRoot = dataDir + getAssignmentDirFromNo(assignment);
			String newFileName = StrNumUtil.getNewName(srcImgFile);
			File newFile = new File(assignmentRoot + DIR_ASSIGNMENT_IMG);
			try {
				if(!newFile.exists()) newFile.mkdirs();
				
				newFile = new File(assignmentRoot + DIR_ASSIGNMENT_IMG + newFileName);
				Files.copy(sFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				eMap.put("newimagedir", assignmentRoot + DIR_ASSIGNMENT_IMG);
				eMap.put("newimagefile", newFileName);
			
				sFile = new File(srcAnnoDir + "/" + srcAnnoFile);
				logger.debug(">>> src file : " + srcAnnoDir + "/" + srcAnnoFile);
				logger.debug(">>> newFileName : " + newFileName);
				if(sFile.exists()) {
//					newFileName = StrNumUtil.getNewName(srcAnnoFile);
					newFileName = StrNumUtil.exchangeExtName(newFileName, "json");
					
					newFile = new File(assignmentRoot + DIR_ASSIGNMENT_HGT);
					if(!newFile.exists()) newFile.mkdirs();
					
					newFile = new File(assignmentRoot + DIR_ASSIGNMENT_HGT + newFileName);
					Files.copy(sFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
					eMap.put("newlabelingdir", assignmentRoot + DIR_ASSIGNMENT_HGT);
					eMap.put("newlabelingfile", newFileName);
				}
				else {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
//			logger.debug(">>> after copy eMap : " + eMap);
		}
		else {
			logger.error("<<< file not exist: " + srcImgDir + "/" + srcImgFile);
		}
		return true;
	}

	public void gtDel(EgovMap eMap) {
		String srcImgDir = eMap.get("imagedir").toString();
		String srcImgFile = eMap.get("imagefile").toString();
		String srcAnnoDir = eMap.get("hgtdir").toString();
		String srcAnnoFile = eMap.get("hgtfile").toString();
		
//		File sFile = new File(srcImgDir + "/" + srcImgFile);
		
		FileImageUtil.deleteFile(srcImgDir + "/" + srcImgFile);
		FileImageUtil.deleteFile(srcAnnoDir + "/" + srcAnnoFile);
	}
	
	public int gtDelAll(String srcImgDir, String srcAnnoDir) {
		File sFile = new File(srcImgDir);
		boolean result = FileImageUtil.deleteAll(sFile, false);
		
		if(result) {
			sFile = new File(srcAnnoDir);
			result = FileImageUtil.deleteAll(sFile, false);
		}
		return result ? 1 : 0;
	}
	
//	public String dirUsage(Map eMap) {
//		if(eMap == null || !eMap.containsKey("location")) {
//			return null;
//		}
//		
//		String result = null;
//		String location = eMap.get("location").toString();
//		
//		return dirUsage(location);
//	}
	
	public String dirUsage(String loc) {
		String result = null;
		logger.debug("> " + dataHome + loc);
		File f = new File(dataHome + loc);
		if(f.exists()) {
			long size = FileUtils.sizeOfDirectory(f);
			
			logger.debug("> " + assignmentHome + loc);
			
			f = new File(assignmentHome + loc);
			if(f.exists()) {
				size += FileUtils.sizeOfDirectory(f);
			}
			
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
		}
		return result;
	}

	public String dirUsage2(String loc) {
		String result = null;
		try {
			String commands = "du --max-depth=0 " + loc;
			result = FileImageUtil.runCommand(commands);
			
			if(result != null) {
				String[] ss = result.split("\t");
				result = ss[0];
			}
				
			logger.debug(">>> dirUsage2 : " + result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
}
