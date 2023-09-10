package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface ReportItemMapper {

	List<EgovMap> selectReportItemList(Map param) throws Exception;

	List<EgovMap> selectReportReportItemList(Map param) throws Exception;

	int reportItemAdd(Map param) throws Exception;
	int reportItemUpdate(Map param) throws Exception;

	List<EgovMap> evalClsList(Map param) throws Exception;

	List<EgovMap> evalObjList1(Map param) throws Exception;
//	List<EgovMap> evalObjList2(Map param) throws Exception;
	List<EgovMap> evalObjList3(Map param) throws Exception;

	EgovMap selectReportReportItem(Map param) throws Exception;

	List<EgovMap> evalFileObjCountList(Map param) throws Exception;

	int reportItemInit(Map param) throws Exception;

}
