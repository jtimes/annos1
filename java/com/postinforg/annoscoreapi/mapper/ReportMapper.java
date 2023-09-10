package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface ReportMapper {

	int selectReportListTotal(Map param) throws Exception;
	List<EgovMap> selectReportList(Map param) throws Exception;
	
	int reportAdd(Map param) throws Exception;	
	int reportUpdate(Map param) throws Exception;
	int evaluationOpenSet(Map param) throws Exception;
	
	EgovMap selectReportDetail(Map param) throws Exception;
	int deleteReportItems(EgovMap param) throws Exception;
	int deleteReports(EgovMap param) throws Exception;
	
	int deleteReportItemsDs(EgovMap param) throws Exception;
	int deleteReportsDs(EgovMap param) throws Exception;
	
}
