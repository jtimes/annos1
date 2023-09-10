package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface EvalMapper {
	List<EgovMap> evaluationofdataset(Map param) throws Exception;
	
	List<EgovMap> evaluationofdatasetCount(Map param) throws Exception;
	void insertClsBulk(Map param) throws Exception;
	void insertObjBulk(Map param) throws Exception;
	void clearCls(Map param) throws Exception;
	void clearObj(Map param) throws Exception;
	void insertClsOne(Map map) throws Exception;
	
	int selectEvalClsListTotal(Map param) throws Exception;
	List<EgovMap> selectEvalClsList(Map param) throws Exception;
	List<EgovMap> selectEvalClsList2(Map param) throws Exception;
	int selectAnalOpenTotal(Map param) throws Exception;
	
	int selectEvalObjListTotal(Map param) throws Exception;
	List<EgovMap> selectEvalObjList(Map param) throws Exception;
	List<EgovMap> selectEvalObjList2(Map param) throws Exception;
	int evaluationOpenSet(Map param) throws Exception;
	int updateEvalCls(EgovMap map) throws Exception;
	int saveEvaluation(Map param) throws Exception;
	
	int selectEvalObjListTotal2(Map param) throws Exception;
	List<EgovMap> selectEvalObjShapes(Map param) throws Exception;
	int evaluationDel(Map param) throws Exception;

	int evaluationOpenSetAll(Map param) throws Exception;
	List selectEvalClsObjCountList(Map param) throws Exception;
	EgovMap getEvalClsAvg(Map param) throws Exception;

	int saveEvalClassInfo(Map param) throws Exception;
	
}
