package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface LabelingMapper {

	int selectLabelingTotal(Map param) throws Exception;
	List<EgovMap> selectLabelingList(Map param) throws Exception;
	
	int getLabelingListChecked(Map param) throws Exception;
	List<EgovMap> selectLabelingCheckedList(Map param) throws Exception;
	
	int selectLabelingHgtTotal(Map param) throws Exception;
	int selectLabelingHgtChecked(Map param) throws Exception;
	List<EgovMap> selectLabelingHgtList(Map param) throws Exception;
	
	int selectLabelingMgtTotal(Map param) throws Exception;
	List<EgovMap> selectLabelingMgtList(Map param) throws Exception;
	
	int labelingGtAdd(Map param) throws Exception;
	int labelingGtDel(Map param) throws Exception;
	
	EgovMap getLabeling(Map param) throws Exception;

	List<EgovMap> labelingCheckList(Map param) throws Exception;
	List<EgovMap> labelingRandomList(Map param) throws Exception;
	
	int deleteFromLabelingno(String labelingno);
	
	List<EgovMap> labelingGtList(Map param) throws Exception;

	int labelingMgtUpdate(Map param) throws Exception;
	
	int labelingMgtReset(Map param) throws Exception;
	
	int labelingMgtUpdateBulk(Map param) throws Exception;
	
	EgovMap getGtLabeling(Map param) throws Exception;

	int labelingGtAddAll(Map param) throws Exception;
	int labelingGtDelAll(Map param) throws Exception;
	String getEt(Map param) throws Exception;
	
}
