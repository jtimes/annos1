package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface AssignmentMapper {

	int selectAssignmentTotal(Map param) throws Exception;
	List<EgovMap> selectAssignmentList(Map param) throws Exception;
	
	int assignmentDel(String assignmentno) throws Exception;
	int assignmentAdd(Map param) throws Exception;
	int assignmentUpdate(Map param) throws Exception;
	EgovMap getAssignmentDetail(Map param) throws Exception;
	int updateMgtCount(Map param) throws Exception;
	int updateAssignmentUsageDate(Map param) throws Exception;
	int insertUploadZip(Map param) throws Exception;
	int insertUploadFileBulk(Map param) throws Exception;
	int insertUploadFileBulkDetail(Map param) throws Exception;
	int evalReqUpdate(Map param) throws Exception;
	int locationUpdate(Map param) throws Exception;
	
}
