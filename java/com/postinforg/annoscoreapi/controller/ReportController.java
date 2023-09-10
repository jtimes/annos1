package com.postinforg.annoscoreapi.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.service.ReportService;

@RestController
public class ReportController extends CommonController {
	
	@Resource ReportService reportService;
	/* 
	@RequestMapping("/reportList")
	public Map reportlist(@RequestParam(defaultValue="0") String projectno
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit) {
		
		if(projectno.equals("0")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = getOffsetLimit(pageno, limit);
		param.put("projectno", projectno);
		
		Map res = reportService.getReportList(param);
		resultSuccess(res, param);
		
		return res;
	}

	@RequestMapping("/reportAdd")
	public Map reportAdd(@RequestParam(defaultValue="0") String projectno
			, @RequestParam(defaultValue="") String datasetnos
			, @RequestParam(defaultValue="") String userno
			, @RequestParam(defaultValue="") String data
			, @RequestParam(defaultValue="") String note) {
		
		if(projectno.equals("0")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		String newreportno = "0";
		try {
			String datas = URLDecoder.decode(data, "UTF-8");
			String notes = URLDecoder.decode(note, "UTF-8");
			
			Map param = new HashMap();
			param.put("projectno", projectno);
			param.put("userno", userno);
			param.put("data", datas);
			param.put("note", notes);
			param.put("datasetnos", datasetnos);
			
			int result = reportService.reportAdd(param);
			if(result < 1) {
				return resultFail(ERR_CD_DAO3, ERR_MSG_DAO3);
			}
			newreportno = param.get("no").toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess("newno", newreportno);
	}
	*/
	
	@RequestMapping("/reportUpdate")
	public Map reportUpdate(@RequestParam(defaultValue="") String reportno
			, @RequestParam(defaultValue="") String datasetnos
			, @RequestParam(defaultValue="") String userno
			, @RequestParam(defaultValue="") String data
			, @RequestParam(defaultValue="") String note
			, @RequestParam(defaultValue="") String audit) {
		
		if(reportno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			String datas = URLDecoder.decode(data, "UTF-8");
			String notes = URLDecoder.decode(note, "UTF-8");
			String audits = URLDecoder.decode(audit, "UTF-8");
			
			Map param = new HashMap();
			param.put("reportno", reportno);
			param.put("userno", userno);
			if(!data.equals("")) param.put("data", datas);
			if(!note.equals("")) param.put("note", notes);
			if(!audit.equals("")) param.put("audit", audits);
			if(!datasetnos.equals("")) param.put("datasetnos", datasetnos);
			
			int result = reportService.reportUpdate(param);
			if(result < 1) {
				return resultFail(ERR_CD_DAO3, ERR_MSG_DAO3);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}
	
}
