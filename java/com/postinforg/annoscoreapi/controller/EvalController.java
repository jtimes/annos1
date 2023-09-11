package com.postinforg.annoscoreapi.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.service.EvalService;

import egovframework.rte.psl.dataaccess.util.EgovMap;
@RestController
public class EvalController extends CommonController {
	
	@Resource EvalService evalService;
	Logger logger = LoggerFactory.getLogger(EvalController.class);
	
	@PostMapping("/evaluationofdataset")
	public Map evaluationofdataset(@RequestParam(defaultValue="") String datasetno ) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int res = evalService.evaluationofdataset(datasetno);
		if(res == -1) {
			return resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
		}
		else if(res == -2) {
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		else if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/saveEvaluation")
	public Map saveEvaluation(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String data
			, @RequestParam(defaultValue="") String clsName
			) {
		
		if(datasetno.equals("") || data.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		if(!clsName.equals("cls") && !clsName.equals("obj") && !clsName.equals("file")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		logger.debug(">>> datasetno :" + datasetno);
		logger.debug(">>> clsName :" + clsName);
		
		try {
			Map param = new HashMap();
			param.put("datasetno", datasetno);
			param.put("clsName", clsName);
			
			int exitstCount = evalService.evaluationofdatasetCount(param);
			if(exitstCount > 0) {
				return resultFail(ERR_CD_DUPDATA, ERR_MSG_DUPDATA);
			}
			String odata = URLDecoder.decode(data, "UTF-8");
			logger.debug(">>> data :" + odata.length());
			logger.debug(">>> data :" + odata);
			param.put("data", odata);
			
			int res = evalService.saveEvaluation(param);
			if(res < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultSuccess();
	}
	
	@RequestMapping("/evaluationClassList")
	public Map evaluationClassList(
			@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit
			, @RequestParam(defaultValue="") String name
			, @RequestParam(defaultValue="") String ap50from
			, @RequestParam(defaultValue="") String ap50to
			, @RequestParam(defaultValue="") String ap55from
			, @RequestParam(defaultValue="") String ap55to
			, @RequestParam(defaultValue="") String ap60from
			, @RequestParam(defaultValue="") String ap60to
			, @RequestParam(defaultValue="") String ap65from
			, @RequestParam(defaultValue="") String ap65to
			, @RequestParam(defaultValue="") String ap70from
			, @RequestParam(defaultValue="") String ap70to
			, @RequestParam(defaultValue="") String ap75from
			, @RequestParam(defaultValue="") String ap75to
			, @RequestParam(defaultValue="") String ap80from
			, @RequestParam(defaultValue="") String ap80to
			, @RequestParam(defaultValue="") String ap85from
			, @RequestParam(defaultValue="") String ap85to
			, @RequestParam(defaultValue="") String ap90from
			, @RequestParam(defaultValue="") String ap90to
			, @RequestParam(defaultValue="") String ap95from
			, @RequestParam(defaultValue="") String ap95to
			, @RequestParam(defaultValue="") String mapfrom
			, @RequestParam(defaultValue="") String mapto
			, @RequestParam(defaultValue="") String isopen
			, @RequestParam(defaultValue="") String gtFlag
			, @RequestParam(defaultValue="") String aplist
			, @RequestParam(defaultValue="") String orderColumn
			, @RequestParam(defaultValue="asc") String orderBy) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map res = null;
		Map param = getOffsetLimit(pageno, limit);
		param.put("datasetno", datasetno);
		
		if(!name.equals("")) param.put("name", name);
		if(!ap50from.equals("")) param.put("ap50from", ap50from);
		if(!ap50to.equals("")) param.put("ap50to", ap50to);
		if(!ap55from.equals("")) param.put("ap55from", ap55from);
		if(!ap55to.equals("")) param.put("ap55to", ap55to);
		if(!ap60from.equals("")) param.put("ap60from", ap60from);
		if(!ap60to.equals("")) param.put("ap60to", ap60to);
		if(!ap65from.equals("")) param.put("ap65from", ap65from);
		if(!ap65to.equals("")) param.put("ap65to", ap65to);
		if(!ap70from.equals("")) param.put("ap70from", ap70from);
		if(!ap70to.equals("")) param.put("ap70to", ap70to);
		if(!ap75from.equals("")) param.put("ap75from", ap75from);
		if(!ap75to.equals("")) param.put("ap75to", ap75to);
		if(!ap80from.equals("")) param.put("ap80from", ap80from);
		if(!ap80to.equals("")) param.put("ap80to", ap80to);
		if(!ap85from.equals("")) param.put("ap85from", ap85from);
		if(!ap85to.equals("")) param.put("ap85to", ap85to);
		if(!ap90from.equals("")) param.put("ap90from", ap90from);
		if(!ap90to.equals("")) param.put("ap90to", ap90to);
		if(!ap95from.equals("")) param.put("ap95from", ap95from);
		if(!ap95to.equals("")) param.put("ap95to", ap95to);
		if(!mapfrom.equals("")) param.put("mapfrom", mapfrom);
		if(!mapto.equals("")) param.put("mapto", mapto);
		if(!isopen.equals("")) param.put("isopen", isopen);
		if(!gtFlag.equals("")) param.put("gtFlag", gtFlag);
		
		if(!aplist.equals("")) {
			String[] apl = aplist.split(",");
			param.put("apl", apl);
		}
		param.put("orderColumn", orderColumn);
		param.put("orderBy", orderBy);
		
		res = evalService.getEvalClsList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@RequestMapping("/evaluationClassAvg")
	public Map evaluationClassAvg(
			@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String name
			, @RequestParam(defaultValue="") String ap50from
			, @RequestParam(defaultValue="") String ap50to
			, @RequestParam(defaultValue="") String ap55from
			, @RequestParam(defaultValue="") String ap55to
			, @RequestParam(defaultValue="") String ap60from
			, @RequestParam(defaultValue="") String ap60to
			, @RequestParam(defaultValue="") String ap65from
			, @RequestParam(defaultValue="") String ap65to
			, @RequestParam(defaultValue="") String ap70from
			, @RequestParam(defaultValue="") String ap70to
			, @RequestParam(defaultValue="") String ap75from
			, @RequestParam(defaultValue="") String ap75to
			, @RequestParam(defaultValue="") String ap80from
			, @RequestParam(defaultValue="") String ap80to
			, @RequestParam(defaultValue="") String ap85from
			, @RequestParam(defaultValue="") String ap85to
			, @RequestParam(defaultValue="") String ap90from
			, @RequestParam(defaultValue="") String ap90to
			, @RequestParam(defaultValue="") String ap95from
			, @RequestParam(defaultValue="") String ap95to
			, @RequestParam(defaultValue="") String mapfrom
			, @RequestParam(defaultValue="") String mapto
			, @RequestParam(defaultValue="") String isopen
			, @RequestParam(defaultValue="") String gtFlag
			) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		
		if(!name.equals("")) param.put("name", name);
		if(!ap50from.equals("")) param.put("ap50from", ap50from);
		if(!ap50to.equals("")) param.put("ap50to", ap50to);
		if(!ap55from.equals("")) param.put("ap55from", ap55from);
		if(!ap55to.equals("")) param.put("ap55to", ap55to);
		if(!ap60from.equals("")) param.put("ap60from", ap60from);
		if(!ap60to.equals("")) param.put("ap60to", ap60to);
		if(!ap65from.equals("")) param.put("ap65from", ap65from);
		if(!ap65to.equals("")) param.put("ap65to", ap65to);
		if(!ap70from.equals("")) param.put("ap70from", ap70from);
		if(!ap70to.equals("")) param.put("ap70to", ap70to);
		if(!ap75from.equals("")) param.put("ap75from", ap75from);
		if(!ap75to.equals("")) param.put("ap75to", ap75to);
		if(!ap80from.equals("")) param.put("ap80from", ap80from);
		if(!ap80to.equals("")) param.put("ap80to", ap80to);
		if(!ap85from.equals("")) param.put("ap85from", ap85from);
		if(!ap85to.equals("")) param.put("ap85to", ap85to);
		if(!ap90from.equals("")) param.put("ap90from", ap90from);
		if(!ap90to.equals("")) param.put("ap90to", ap90to);
		if(!ap95from.equals("")) param.put("ap95from", ap95from);
		if(!ap95to.equals("")) param.put("ap95to", ap95to);
		if(!mapfrom.equals("")) param.put("mapfrom", mapfrom);
		if(!mapto.equals("")) param.put("mapto", mapto);
		if(!isopen.equals("")) param.put("isopen", isopen);
		if(!gtFlag.equals("")) param.put("gtFlag", gtFlag);
		
		EgovMap res = evalService.getEvalClsAvg(param);
		return resultSuccess("avgs", res);
	}
	
	/* 
	@RequestMapping("/evaluationObjList")
	public Map evaluationObjList(
			@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String gtFlag
			, @RequestParam(defaultValue="") String predFlag
			, @RequestParam(defaultValue="") String cls
			, @RequestParam(defaultValue="") String widthfrom
			, @RequestParam(defaultValue="") String widthto
			, @RequestParam(defaultValue="") String heightfrom
			, @RequestParam(defaultValue="") String heightto
			, @RequestParam(defaultValue="") String ratiofrom
			, @RequestParam(defaultValue="") String ratioto
			, @RequestParam(defaultValue="") String diagonalfrom
			, @RequestParam(defaultValue="") String diagonalto
			, @RequestParam(defaultValue="") String shapeType
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit
			, @RequestParam(defaultValue="") String orderColumn
			, @RequestParam(defaultValue="asc") String orderBy) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map res = null;
		Map param = getOffsetLimit(pageno, limit);
		param.put("datasetno", datasetno);
		
		if(!gtFlag.equals("")) param.put("gtFlag", gtFlag);
		if(!predFlag.equals("")) param.put("predFlag", predFlag);
		if(!cls.equals("")) param.put("cls", cls);
		
		if(!widthfrom.equals("")) param.put("widthfrom", widthfrom);
		if(!widthto.equals("")) param.put("widthto", widthto);
		if(!heightfrom.equals("")) param.put("heightfrom", heightfrom);
		if(!heightto.equals("")) param.put("heightto", heightto);
		if(!ratiofrom.equals("")) param.put("ratiofrom", ratiofrom); 
		if(!ratioto.equals("")) param.put("ratioto", ratioto); 
		if(!diagonalfrom.equals("")) param.put("diagonalfrom", diagonalfrom); 
		if(!diagonalto.equals("")) param.put("diagonalto", diagonalto); 
		if(!shapeType.equals("")) param.put("shapeType", shapeType); 
		
		param.put("orderColumn", orderColumn);
		param.put("orderBy", orderBy);
		
		res = evalService.getEvalObjList(param);
		resultSuccess(res, param);
		
		return res;
	} */
	
	@RequestMapping("/evaluationObjList")
	public Map evaluationObjList(
			@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit
			, @RequestParam(defaultValue="0") String predFlag
			, @RequestParam(defaultValue="") String orderColumn
			, @RequestParam(defaultValue="asc") String orderBy) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map res = null;
		Map param = getOffsetLimit(pageno, limit);
		param.put("datasetno", datasetno);
		
		param.put("orderColumn", orderColumn);
		param.put("orderBy", orderBy);
		param.put("predFlag", predFlag);
		
//		boolean pred = predFlag.equals("1") ? true : false;
		res = evalService.getEvalObjSimpleList(param, true);
		resultSuccess(res, param);
		
		return res;
	}

	@RequestMapping("/evaluationOpenSetAll")
	public Map analysisOpenSetAll(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String name
			, @RequestParam(defaultValue="") String ap50from
			, @RequestParam(defaultValue="") String ap50to
			, @RequestParam(defaultValue="") String ap55from
			, @RequestParam(defaultValue="") String ap55to
			, @RequestParam(defaultValue="") String ap60from
			, @RequestParam(defaultValue="") String ap60to
			, @RequestParam(defaultValue="") String ap65from
			, @RequestParam(defaultValue="") String ap65to
			, @RequestParam(defaultValue="") String ap70from
			, @RequestParam(defaultValue="") String ap70to
			, @RequestParam(defaultValue="") String ap75from
			, @RequestParam(defaultValue="") String ap75to
			, @RequestParam(defaultValue="") String ap80from
			, @RequestParam(defaultValue="") String ap80to
			, @RequestParam(defaultValue="") String ap85from
			, @RequestParam(defaultValue="") String ap85to
			, @RequestParam(defaultValue="") String ap90from
			, @RequestParam(defaultValue="") String ap90to
			, @RequestParam(defaultValue="") String ap95from
			, @RequestParam(defaultValue="") String ap95to
			, @RequestParam(defaultValue="") String mapfrom
			, @RequestParam(defaultValue="") String mapto
			, @RequestParam(defaultValue="") String reverse) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		
		if(!name.equals("")) param.put("name", name);
		if(!ap50from.equals("")) param.put("ap50from", ap50from);
		if(!ap50to.equals("")) param.put("ap50to", ap50to);
		if(!ap55from.equals("")) param.put("ap55from", ap55from);
		if(!ap55to.equals("")) param.put("ap55to", ap55to);
		if(!ap60from.equals("")) param.put("ap60from", ap60from);
		if(!ap60to.equals("")) param.put("ap60to", ap60to);
		if(!ap65from.equals("")) param.put("ap65from", ap65from);
		if(!ap65to.equals("")) param.put("ap65to", ap65to);
		if(!ap70from.equals("")) param.put("ap70from", ap70from);
		if(!ap70to.equals("")) param.put("ap70to", ap70to);
		if(!ap75from.equals("")) param.put("ap75from", ap75from);
		if(!ap75to.equals("")) param.put("ap75to", ap75to);
		if(!ap80from.equals("")) param.put("ap80from", ap80from);
		if(!ap80to.equals("")) param.put("ap80to", ap80to);
		if(!ap85from.equals("")) param.put("ap85from", ap85from);
		if(!ap85to.equals("")) param.put("ap85to", ap85to);
		if(!ap90from.equals("")) param.put("ap90from", ap90from);
		if(!ap90to.equals("")) param.put("ap90to", ap90to);
		if(!ap95from.equals("")) param.put("ap95from", ap95from);
		if(!ap95to.equals("")) param.put("ap95to", ap95to);
		if(!mapfrom.equals("")) param.put("mapfrom", mapfrom);
		if(!mapto.equals("")) param.put("mapto", mapto);
		if(!reverse.equals("")) param.put("reverse", reverse);
		
		int result = evalService.evaluationOpenSetAll(param);
		if(result < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		return resultSuccess();
	}
	
	@RequestMapping("/evaluationOpenSetY")
	public Map analysisOpenSetY(@RequestParam(defaultValue="") String evaluationno) {
		
		return evaluationOpenSet(evaluationno, "y");
		
	}
	
	@RequestMapping("/evaluationOpenSetN")
	public Map analysisOpenSetN(@RequestParam(defaultValue="") String evaluationno) {
		
		return evaluationOpenSet(evaluationno, "n");
		
	}
	
	private Map evaluationOpenSet(String ecno, String openset) {
		if(ecno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int result = 0;
		try {
			String[] ecnos = URLDecoder.decode(ecno, "UTF-8").split(",");
			
			result = evalService.evaluationOpenSet(ecnos, openset);
			if(result < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
			
//			result = analysisService.analysisOpenCount(anos);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
//		return resultSuccess("opencount", result);
		return resultSuccess();
	}

	@PostMapping("/evaluationofdatasetDel")
	public Map evaluationofdatasetDel(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String clsName
			) {
		
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		if(!clsName.equals("cls") && !clsName.equals("obj") && !clsName.equals("file")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		logger.debug(">>> datasetno :" + datasetno);
		logger.debug(">>> clsName :" + clsName);
		
		try {
			Map param = new HashMap();
			param.put("datasetno", datasetno);
			param.put("clsName", clsName);
			int res = evalService.evaluationDel(param);
			if(res < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultSuccess();
	}

	@PostMapping("/saveEvalClassInfo")
	public Map saveEvalClassInfo(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String classes
			, @RequestParam(defaultValue="") String aptype ) {
		
		if(datasetno.equals("") || classes.equals("") || aptype.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		param.put("aptype", aptype);
		int res = 0;
		try {
			param.put("classes", URLDecoder.decode(classes, "UTF-8"));
			res = evalService.saveEvalClassInfo(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(res < 1) return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		return resultSuccess();
	}
	
	@RequestMapping("/evaluationFileList")
	public Map evaluationFileList(
			@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String ascfrom
			, @RequestParam(defaultValue="") String ascto
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit
			, @RequestParam(defaultValue="") String orderColumn
			, @RequestParam(defaultValue="asc") String orderBy) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map res = null;
		Map param = getOffsetLimit(pageno, limit);
		param.put("datasetno", datasetno);
		
		if(!ascfrom.equals("")) param.put("ascfrom", ascfrom);
		if(!ascto.equals("")) param.put("ascto", ascto);
		
		param.put("orderColumn", orderColumn);
		param.put("orderBy", orderBy);
		
		res = evalService.getEvalFileList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@RequestMapping("/evaluationFileCheckedCount")
	public Map evaluationFileCheckedCount (
			@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String ascfrom
			, @RequestParam(defaultValue="") String ascto ) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		
		if(!ascfrom.equals("")) param.put("ascfrom", ascfrom);
		if(!ascto.equals("")) param.put("ascto", ascto);
		
		Map res = evalService.getEvalFileCheckedCount(param);
		resultSuccess(res);
		
		return res;
	}
	
	@RequestMapping("/evaluationClsClear")
	public Map evaluationClsClear(@RequestParam(defaultValue="") String datasetno) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		
		Map result = new HashMap();
		int res = evalService.evaluationClsClear(param);
		if(res > 0) {
			resultSuccess(result);
		}
		else if(res < 0) {
			result = resultFail(ERR_CD_DAO4, ERR_MSG_DAO4);
		}
		else {
			result = resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
		}
		
		return result;
	}
	
}
