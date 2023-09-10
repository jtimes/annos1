package com.postinforg.annoscoreapi.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.service.ProgressService;


@RestController
public class ProgressController extends CommonController {
	
	@Resource ProgressService progressService;
	Logger logger = LoggerFactory.getLogger(ProgressController.class);
	
	@RequestMapping("/progressAddOrUpdate")
	public Map progressAddOrUpdate(@RequestParam(defaultValue="") String assignmentno
			, @RequestParam(defaultValue="") String status
			, @RequestParam(defaultValue="") String progress
			, @RequestParam(defaultValue="") String note
			, @RequestParam(defaultValue="") String type
			, @RequestParam(defaultValue="") String progressno
			, @RequestParam(defaultValue="") String datasetno) {
		
		Map param = new HashMap();
		if(!assignmentno.equals("")) param.put("assignmentno", assignmentno);
		if(!datasetno.equals("")) param.put("datasetno", datasetno);
		if(!type.equals("")) param.put("type", type);
		
		if(!status.equals("")) param.put("status", status);
		if(!progress.equals("")) param.put("progress", progress);
		if(!progressno.equals("")) param.put("progressno", progressno);
		if(!note.equals("")) param.put("note", note);
		
		if(param.isEmpty()) return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);

		Map res = progressService.progressAddOrUpdate(param);
		if(res.containsKey("count") && !res.get("count").toString().equals("0"))
			resultSuccess(res);
		else 
			resultFail(res, ERR_CD_NODATA, ERR_MSG_NODATA);
		
		return res;
	}
	
	@RequestMapping("/progressDetail")
	public Map progressAddOrUpdate(@RequestParam(defaultValue="") String assignmentno
			, @RequestParam(defaultValue="") String progressno
			, @RequestParam(defaultValue="") String type
			, @RequestParam(defaultValue="") String datasetno) {
		
		Map param = new HashMap();
		if(!assignmentno.equals("")) param.put("assignmentno", assignmentno);
		if(!datasetno.equals("")) param.put("datasetno", datasetno);
		if(!type.equals("")) param.put("type", type);
		if(!progressno.equals("")) param.put("progressno", progressno);
		
		if(param.isEmpty()) return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		
		Map res = progressService.progressDetail(param);
		/*if(res == null) {
			progressService.progressAdd(param);
			res = progressService.progressDetail(param);
		}*/
		
		if(res.isEmpty())
			resultFail(res, ERR_CD_NODATA, ERR_MSG_NODATA);
		else 
			resultSuccess(res);
		
		return res;
	}
	
}