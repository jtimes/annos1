package com.postinforg.annoscoreapi.controller;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.service.EvalBigService;

@RestController
public class EvalBigController extends CommonController {
	
	@Resource EvalBigService evalBigService;
	Logger logger = LoggerFactory.getLogger(EvalBigController.class);
	
	@PostMapping("/evaluationBigSetup")
	public Map evaluationBigSetup(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String type
			, @RequestParam(defaultValue="") String note
			, @RequestParam(defaultValue="") String files
			, @RequestParam(defaultValue="") String path) {
		
		if(datasetno.equals("") || type.equals("") || files.equals("") || path.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		if(!type.equals("class") && !type.equals("object") && !type.equals("file")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		try {
			param.put("files", URLDecoder.decode(files, "UTF-8"));
			if(!note.equals("")) param.put("note", URLDecoder.decode(note, "UTF-8"));
		}
		catch(Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		param.put("path", path);
		param.put("type", type);
		
		int res = evalBigService.evaluationBigSetup(param);
		if(res == -1) {
			return resultFail(ERR_CD_DAO1, "no data in dataset table");
		}
		else if(res == -2) {
			return resultFail(ERR_CD_DUPDATA, "not finished data exists");
		}
		else if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/evaluationBigStatus")
	public Map evaluationBigStatus(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String status) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		if(!status.equals("")) param.put("status", status);
		
		Map res = evalBigService.evaluationBigStatus(param);
		resultSuccess(res, param);
		
		return res;
	}
	
}
