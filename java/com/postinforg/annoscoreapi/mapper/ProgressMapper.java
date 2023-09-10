package com.postinforg.annoscoreapi.mapper;

import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface ProgressMapper {

	EgovMap selectProgress(Map param) throws Exception;
	int progressAdd(Map param) throws Exception;
	int progressUpdate(Map param)  throws Exception;
	
}
