package com.postinforg.annoscoreapi.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.postinforg.annoscoreapi.service.DatasetService;
import com.postinforg.annoscoreapi.service.ReportItemService;

@RestController
public class ReportItemController extends CommonController {
	
	@Resource ReportItemService reportItemService;
	@Resource DatasetService datasetService;
	
	@RequestMapping("/reportItemList")
	public Map reportItemList(@RequestParam(defaultValue="") String reportno) {
		
		Map param = new HashMap();
		if(!reportno.equals("")) {
			param.put("reportno", reportno);
		}
		Map res = reportItemService.reportItemList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@RequestMapping("/reportReportItemList")
	public Map reportReportItemList(@RequestParam(defaultValue="") String reportno
			, @RequestParam(defaultValue="") String visible) {
		
		if(reportno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		Map param = new HashMap();
		param.put("reportno", reportno);
		
		if(!visible.equals("")) {
			param.put("visible", visible);
		}
		
		Map res = reportItemService.reportReportItemList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@RequestMapping("/reportItemAdd")
	public Map reportItemAdd(@RequestParam(defaultValue="") String reportno
			, @RequestParam(defaultValue="") String reportitemno
			, @RequestParam(defaultValue="") String value
			, @RequestParam(defaultValue="") String rank) {
		
		if(reportno.equals("") || reportitemno.equals("") || value.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			String values = URLDecoder.decode(value, "UTF-8");
			
			Map param = new HashMap();
			param.put("reportno", reportno);
			param.put("reportitemno", reportitemno);
			param.put("value", values);
			param.put("rank", rank);
			
			int result = reportItemService.reportItemAdd(param);
			if(result < 1) {
				return resultFail(ERR_CD_DAO3, ERR_MSG_DAO3);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}

	@RequestMapping("/reportItemUpdate")
	public Map reportItemUpdate(@RequestParam(defaultValue="") String reportno
			, @RequestParam(defaultValue="") String reportitemno
			, @RequestParam(defaultValue="") String value
			, @RequestParam(defaultValue="") String rank
			, @RequestParam(defaultValue="") String visible) {
		
		if(reportno.equals("") || reportitemno.equals("") || value.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			String values = URLDecoder.decode(value, "UTF-8");
			
			Map param = new HashMap();
			param.put("reportno", reportno);
			param.put("reportitemno", reportitemno);
			if(!values.equals("")) param.put("value", values);
			if(!rank.equals("")) param.put("rank", rank);
			if(!visible.equals("")) param.put("visible", visible);
			
			int result = reportItemService.reportItemUpdate(param, null);
			if(result < 1) {
				return resultFail(ERR_CD_DAO3, ERR_MSG_DAO3);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}
	
	@RequestMapping("/reportItemUpdateFile")
	public Map reportItemUpdateFile(@RequestParam(defaultValue="") String reportno
			, @RequestParam(defaultValue="") String reportitemno
			, @RequestParam(defaultValue="") String value
			, @RequestParam(defaultValue="") String rank
			, @RequestParam(defaultValue="") String visible
			, @RequestParam("file") MultipartFile multipartFile) {
		
		if(reportno.equals("") || reportitemno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("reportno", reportno);
		param.put("reportitemno", reportitemno);
		
		int result = reportItemService.reportItemUpdate(param, multipartFile);
		if(result < 1) {
			return resultFail(ERR_CD_DAO3, ERR_MSG_DAO3);
		}
		return resultSuccess();
	}

	@RequestMapping("/reportBaseInfo")
	public Map reportBaseInfo(@RequestParam(defaultValue="") String datasetno) {
		
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		Map ds = datasetService.datasetDetail(param);
		param.put("reportno", ds.get("rno"));
		
		try {
			Map res = reportItemService.reportBaseInfo(param);
			if(res.containsKey("result")) {
				return res;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultFail(ERR_CD_DECODE, ERR_MSG_DECODE);
	}
	
	
}
