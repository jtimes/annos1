package com.postinforg.annoscoreapi.util;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class JsonFileValidator {
	public static final String[] keylist = {
			"version", "flags", "shapes", "imagePath", "imageData", "imageWidth", "imageHeight"
	};
	public static final String[] shapeslist = {
//			"label", "points", "group_id", "shape_type", "flags"
			"label", "points", "line_color", "shape_type", "fill_color"
	};
	
	public boolean isValid(Map src) {
		for(String key1 : keylist) {
			if(!src.containsKey(key1)) return false;
			
			if(key1.equals("shapes")) {
				try {
					List<Map> shapes = (List<Map>)src.get(key1);
					for(Map shape : shapes) {
						for(String key2 : shapeslist) {
							if(!shape.containsKey(key2)) return false;
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		
		return true;
	}
	
}
