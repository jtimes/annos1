package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface AssignmentBgMapper {

	int assignmentBgAdd(Map param) throws Exception;
	List<EgovMap> selectAssignmentBgList(Map param) throws Exception;
	int updateAssignmentBgJob(Map param) throws Exception;
	
	int selectAssignmentDirTotal(Map param) throws Exception;
	List<EgovMap> selectAssignmentDirList(Map param) throws Exception;
	
	List<EgovMap> assignmentDiskUsageCheck(Map param) throws Exception;
	
	
	List<EgovMap> assignmentAliveList(Map param) throws Exception;
	List<EgovMap> assignmentIncomingList(Map param) throws Exception;
	int updateAssignmentIncomingStatus(Map param) throws Exception;
	int assignmentIncomingAdd(Map param) throws Exception;
	
}
