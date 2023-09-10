package com.postinforg.annoscoreapi.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.postinforg.annoscoreapi.model.ClsInfo;
import com.postinforg.annoscoreapi.model.UploadzipFile;

public class FileImageUtil {

	public static Map getAllExifData(File file) {
		Map result = new HashMap();
		
		try {
			Metadata metadata = ImageMetadataReader.readMetadata(file);

			for (Directory directory : metadata.getDirectories()) {
			    for (Tag tag : directory.getTags()) {
//			        System.out.format("[%s] - %s = %s\n",
//			            directory.getName(), tag.getTagName(), tag.getDescription());
			    	result.put(tag.getTagName(), tag.getDescription());
			    }
			    if (directory.hasErrors()) {
			        for (String error : directory.getErrors()) {
//			            System.err.format("ERROR: %s\n", error);
			        	result.put(directory.getName() + " error", error);
			        	break;
			        }
			    }
			}
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return result;
	}
	
	private static final DateTimeFormatter DATE_FORMATTER 
		= DateTimeFormatter.ofPattern("yyyyMMdd HHmmss");
	
	public static String formatDateTime(FileTime fileTime) {
		LocalDateTime localDateTime = fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		
		return localDateTime.format(DATE_FORMATTER);
	}
	
	public static void getJsonLabelData(File file, UploadzipFile imgObj, ObjectMapper mapper, String saveHgtLoc) {
		try {
			Path path = file.toPath();
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
			imgObj.setJsonDate(formatDateTime(attr.creationTime()));
			imgObj.setJsonUpdate(formatDateTime(attr.lastModifiedTime()));
			imgObj.setJsonSize(Files.size(path));
			
			String jsonBody = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			Map data = new ObjectMapper().readValue(jsonBody, Map.class);
			
			if(data.containsKey("shapes")) {
				List<ClsInfo> clsList = new ArrayList<ClsInfo>();
				Map<String, ClsInfo> clsCntMap = new HashMap<String, ClsInfo>();
				List<Map> mList = (List<Map>)data.get("shapes");
				String key = "";
				Integer val = 0;
				for(Map m : mList) {
					key = m.get("label") + " " + m.get("shape_type");
					if(clsCntMap.containsKey(key)) {
						ClsInfo cinfo = clsCntMap.get(key);
						cinfo.increaseCnt();
					}
					else {
						ClsInfo cinfo = new ClsInfo(saveHgtLoc, imgObj.getJsonName()
								, m.get("label").toString(), m.get("shape_type").toString());
						clsCntMap.put(key, cinfo);
					}
				}
				if(clsCntMap.size() > 0) {
					Iterator it = clsCntMap.keySet().iterator();
					while(it.hasNext()) {
						key = (String)it.next();
						clsList.add(clsCntMap.get(key));
					}
					
					imgObj.setClsList(clsList);
				}
			}
			else {
				System.err.println("<<< json has no shapes !!!");
				imgObj.setJsonValidity(1);
			}
		}
		catch(Exception e) {
			imgObj.setJsonValidity(1);
			e.printStackTrace();
		}
	}
	
	public static void getExifLabelData(File file, UploadzipFile imgObj) {
		try {
			Path path = file.toPath();
			BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
			imgObj.setImageDate(formatDateTime(attr.creationTime()));
			imgObj.setImageUpdate(formatDateTime(attr.lastModifiedTime()));
			imgObj.setImageSize(Files.size(path));
			
			Metadata metadata = ImageMetadataReader.readMetadata(file);
			String temp = "";
			int iWid = 0;
			int iHei = 0;
			for (Directory directory : metadata.getDirectories()) {
				for (Tag tag : directory.getTags()) {
					if(tag.getTagName().equals("Image Height")) {
						temp = tag.getDescription().split(" ")[0];
						iHei = Integer.parseInt(temp);
						imgObj.setImageHeight(iHei);
					}
					else if(tag.getTagName().equals("Image Width")) {
						temp = tag.getDescription().split(" ")[0];
						iWid = Integer.parseInt(temp);
						imgObj.setImageWidth(iWid);
					}
					if(iHei != 0) {
						double c = Math.round(iWid * 100 / iHei);
						imgObj.setImageRatio(c / 100);
					}
				}
				if (directory.hasErrors()) {
//					for (String error : directory.getErrors()) {
//						result.put(directory.getName() + " error", error);
//						break;
//					}
					imgObj.setImageValidity(1);
				}
			}
			
		}
		catch(Exception ex) {
			imgObj.setImageValidity(1);
			ex.printStackTrace();
		}
	}
	
	public static boolean deleteFile(String filename) {
		File file = new File(filename);
		if(file.exists() && file.isFile()) {
			System.out.println(">>> delete file : " + filename);
			return file.delete();
		}
		return false;
	}
	
	public static boolean deleteAll(File file, boolean rootDel) {
		if(file.exists() && file.isDirectory()) {
			try {
				File[] list = file.listFiles();
				for(File f : list) {
					if(f.isDirectory()) {
						deleteAll(f, true);
					}
					else {
						f.delete();
					}
				}
				if(rootDel) file.delete();
			}
			catch(Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
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
	
	public static void getZipfileList(String path, List<String> list) {
		try {
			System.out.println("> getZipfileList : " + path);
			File dir = new File(path);
			
			if(dir.isDirectory()) {
				File[] files = dir.listFiles();
				for(File f : files) {
					if(f.isDirectory()) {
						getZipfileList(f.getAbsolutePath(), list);
					}
					else {
						if(f.getName().toLowerCase().endsWith(".zip")) {
							list.add(f.getAbsolutePath());
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public static boolean runUnzipCommand(String activeProfiles, String srcFile
		, String newDir, String linuxOnlyFilename) throws IOException {
//		String[] cmd = null;
		boolean result = false;
		String commands = "";
		
		if(activeProfiles.startsWith("local")) {	// Windows
			String src = "D:" + srcFile.replaceAll("/", "\\\\");
			String dest = "D:" + newDir.replaceAll("/", "\\\\");
//			cmd = new String[] {"cmd", "/c", "alzipcon", "-x", "-oa", src, dest};
			commands = "alzipcon -x -oa " + src + " " + dest;
		}
		else {		// Linux
//			cmd = new String[] { "unzip", srcFile, "-d", newDir +File.separatorChar+ linuxOnlyFilename};
			commands = "unzip " + srcFile + " -d " + newDir + linuxOnlyFilename;
		}
		
		System.out.println(">>> commands : " + commands);
		try {
			ProcessBuilder pb = new ProcessBuilder();
			if(activeProfiles.startsWith("local")) 
				pb.command("cmd", "/c", commands);
			else
				pb.command("bash", "-c", commands);
			Process process = pb.start();
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			String str = null;
			while((str = stdOut.readLine()) != null) {
				System.out.println(">>>> std out : " + str);
			}
			
			result = true;
		}
		catch(Exception e) { e.printStackTrace(); }
		
		return result;
	}

	public static boolean runCopyCommand(String activeProfiles, String srcDir, String newDir) throws IOException {
		if(activeProfiles.equals("local")) return true;
		
		File file = new File(srcDir);
		if(!file.exists() || !file.isDirectory()) {
			System.out.println(srcDir + " not exist or file.");
			return false;
		}
		file = new File(newDir);
		if(file.exists()) {
			System.out.println(newDir + " exist !!!");
			return false;
		}
		
		String[] cmd = new String[] { "cp", "-r", srcDir, newDir };
//		String[] cmd = new String[] {"cmd", "/c", "copy", srcDir, newDir};
		Process process = new ProcessBuilder(cmd).start();
		BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		String str = null;
		while((str = stdOut.readLine()) != null) {
			System.out.println(str);
		}
		
		return true;
	}
	
	public static String runCommand(String commands) throws IOException {
		ProcessBuilder pb = new ProcessBuilder();
		pb.command("bash", "-c", commands);
		System.out.println("cmd >> " + commands);
		Process process = pb.start();
		BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
		
		String str = null;
		String result = "";
		while((str = stdOut.readLine()) != null) {
			System.out.println(str);
			result += str + " ";
		}
		
		return result;
	}
}
