package com.postinforg.annoscoreapi.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Component
public class LabelingUtil {
	
	public static void checkImageAttribute(EgovMap anno, ObjectMapper mapper, JsonFileValidator jsonFileValidator) {
		
		String path = "";
		
		if(anno.get("mgtflag") == null || !anno.get("mgtflag").toString().equals("1")) {
			path = anno.get("labelingdir").toString();
			if(!path.endsWith("/")) path += "/";
			path += anno.get("labelingfile").toString();
		}
		else {
			path = anno.get("mgtdir").toString();
			if(!path.endsWith("/")) path += "/";
			path += anno.get("mgtfile").toString();
		}
		
//		String path2 = anno.get("imagedir").toString() + "/" + anno.get("imagefile").toString();
		System.out.println("> path : " + path);
		
		File file = new File(path);
//		File file2 = new File(path2);
		boolean hasLabelValid = true;
		if(anno.containsKey("labelingvalidity") && anno.get("labelingvalidity").toString().equals("1")) hasLabelValid = false;
		
		if(file.exists() && file.isFile() && hasLabelValid) {
//		if(file.exists() && file.isFile() && file2.exists() && file2.isFile()) {
			try {
				String jsonBody = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
				Map data = mapper.readValue(jsonBody, Map.class);
				
				/*
				if(data.containsKey("imageWidth")) {
					anno.put("imageWidth", data.get("imageWidth"));
				}
				if(data.containsKey("imageHeight")) {
					anno.put("imageHeight", data.get("imageHeight"));
				}
				if(jsonFileValidator.isValid(data)) {
					anno.put("isJsonValid", true);
				}
				else {
					anno.put("isJsonValid", false);
				} */
				
				if(data.containsKey("shapes")) {
					anno.put("shapes", data.get("shapes"));
				}
				
//				if(file2.isFile()) anno.put("filesize", file2.length() / 1024); 	// kb
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("> not exist : " + path);
		}
	}
	
}
