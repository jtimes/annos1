package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import com.postinforg.annoscoreapi.model.Export;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface ExportMapper {

	int selectExportTotal(Map param) throws Exception;
	List<EgovMap> selectExportList(Map param) throws Exception;
	Map exportDetail(Export param) throws Exception;
	
	int exportAdd(Map param) throws Exception;
	int exportUpdate(Map param) throws Exception;
}
