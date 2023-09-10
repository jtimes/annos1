package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface EvalBig2Mapper {

	int selectEvalObjSimpleListTotal(Map param) throws Exception;
	int selectEvalObjSimpleCheckedTotal(Map param) throws Exception;
	List<EgovMap> selectEvalObjSimpleList(Map param) throws Exception;
	
	int selectEvalObjSimplePredListTotal(Map param) throws Exception;
	int selectEvalObjSimpleCheckedPredTotal(Map param) throws Exception;
	List<EgovMap> selectEvalObjSimplePredList(Map param) throws Exception;
	int evalObjRefLink(EgovMap evalBig) throws Exception;
	int evalObjRefInit(Map param) throws Exception;

}
