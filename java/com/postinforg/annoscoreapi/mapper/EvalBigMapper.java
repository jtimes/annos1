package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface EvalBigMapper {

	int evaluationBigSetup(Map param) throws Exception;
	List evaluationBigStatus(Map param) throws Exception;
	int updateEvalBigStatus(Map param) throws Exception;
	
	List evaluationClsMin(Map param) throws Exception;
	List evaluationClsMax(Map param) throws Exception;
	
	int evaluationClsInsert(Map param) throws Exception;
	int evaluationClsUpdate(Map param) throws Exception;
	
	int evaluationObjRefClear(Map param) throws Exception;
	int evaluationObjClear(Map param) throws Exception;
	int evaluationObjInsertBulk(Map param) throws Exception;

	/// 기존 evalMapper 에서 이관
	int selectEvalClsListTotal(Map param) throws Exception;
	int selectEvalClsCheckedTotal(Map param) throws Exception;
	List<EgovMap> selectEvalClsList(Map param) throws Exception;
	List<EgovMap> selectEvalClsList2(Map param) throws Exception;
	int selectAnalOpenTotal(Map param) throws Exception;
	
	List selectEvalClsObjCountList(Map param) throws Exception;
	

//	int selectEvalObjListTotal(Map param) throws Exception;
//	int selectEvalObjCheckedTotal(Map param) throws Exception;
//	List<EgovMap> selectEvalObjList(Map param) throws Exception;
	List<EgovMap> selectEvalObjList2(Map param) throws Exception;
	int evaluationOpenSet(Map param) throws Exception;
	int selectEvalObjListTotal2(Map param) throws Exception;
	List<EgovMap> selectEvalObjShapes(Map param) throws Exception;
	
	int evaluationOpenSetAll(Map param) throws Exception;
	EgovMap getEvalClsAvg(Map param) throws Exception;
	
	int evaluationFileRefClear(Map param) throws Exception;
	int evaluationFileClear(Map param) throws Exception;
	int evaluationFileInsertBulk(Map param) throws Exception;
	
	int deleteEvalBigFile(EgovMap evalBig) throws Exception;
	int insertEvalBigFile(EgovMap evalBig) throws Exception;
	int updateEvalClsObjCount(EgovMap param) throws Exception;
	int updateEvalClsObjCount2(Map param) throws Exception;
	
	int selectEvalFileListTotal(Map param) throws Exception;
	int selectEvalFileCheckedTotal(Map param) throws Exception;
	List selectEvalFileList(Map param) throws Exception;
	int evaluationClsReset(Map param) throws Exception;
	int copyEvalFileRef(Map param) throws Exception;
	int copyEvalCls(Map param) throws Exception;
	int copyEvalObjRef(Map param) throws Exception;
	EgovMap evalSummary(Map param) throws Exception;
	
	int evaluationClsClear(Map param) throws Exception;
	int deleteEvalBigFileRef(EgovMap evalBig) throws Exception;
	int evaluationClsDsClear(Map param) throws Exception;
	int evaluationObjDsRefClear(Map param) throws Exception;
	int evaluationObjDsClear(Map param) throws Exception;
	int datasetLabelingDel(Map param) throws Exception;
	List evalClassNames(Map result) throws Exception;

}
