package com.postinforg.annoscoreapi.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.service.FilterService;

@RestController
public class FilterController extends CommonController {
	
	@Resource FilterService filterService;
	Logger logger = LoggerFactory.getLogger(FilterController.class);
	
	@RequestMapping("/filterDs")
	public Map filterDs(@RequestParam(defaultValue="") String assignmentno
			, @RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String imagesizeFrom
			, @RequestParam(defaultValue="") String imagesizeTo
			, @RequestParam(defaultValue="") String imagewidthFrom
			, @RequestParam(defaultValue="") String imagewidthTo
			, @RequestParam(defaultValue="") String imageheightFrom
			, @RequestParam(defaultValue="") String imageheightTo
			, @RequestParam(defaultValue="") String imageratioFrom
			, @RequestParam(defaultValue="") String imageratioTo
			, @RequestParam(defaultValue="") String labelingsizeFrom
			, @RequestParam(defaultValue="") String labelingsizeTo
			, @RequestParam(defaultValue="") String imagedateFrom
			, @RequestParam(defaultValue="") String imagedateTo
			, @RequestParam(defaultValue="") String imageupdateFrom
			, @RequestParam(defaultValue="") String imageupdateTo
			, @RequestParam(defaultValue="") String imagevalidity
			, @RequestParam(defaultValue="") String labelingdateFrom
			, @RequestParam(defaultValue="") String labelingdateTo
			, @RequestParam(defaultValue="") String labelingupdateFrom
			, @RequestParam(defaultValue="") String labelingupdateTo
			, @RequestParam(defaultValue="") String labelingvalidity
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit
			, @RequestParam(defaultValue="1") String checked
			, @RequestParam(defaultValue="") String orderColumn
			, @RequestParam(defaultValue="asc") String orderBy) {
		
		if(assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = getOffsetLimit("1", limit);
		param.put("assignmentno", assignmentno);
		param.put("checked", checked);
		
		if(!datasetno.equals("")) param.put("datasetno", datasetno);
		if(!imagesizeFrom.equals("")) param.put("imagesizeFrom", imagesizeFrom);
		if(!imagesizeTo.equals("")) param.put("imagesizeTo", imagesizeTo);
		if(!imagewidthFrom.equals("")) param.put("imagewidthFrom", imagewidthFrom);
		if(!imagewidthTo.equals("")) param.put("imagewidthTo", imagewidthTo);
		if(!imageheightFrom.equals("")) param.put("imageheightFrom", imageheightFrom);
		if(!imageheightTo.equals("")) param.put("imageheightTo", imageheightTo);
		if(!imageratioFrom.equals("")) param.put("imageratioFrom", imageratioFrom);
		if(!imageratioTo.equals("")) param.put("imageratioTo", imageratioTo);
		if(!labelingsizeFrom.equals("")) param.put("labelingsizeFrom", labelingsizeFrom);
		if(!labelingsizeTo.equals("")) param.put("labelingsizeTo", labelingsizeTo);
		if(!imagevalidity.equals("")) param.put("imagevalidity", imagevalidity);
		if(!labelingvalidity.equals("")) param.put("labelingvalidity", labelingvalidity);
		
		if(!imagedateFrom.equals("")) param.put("imagedateFrom", imagedateFrom);
		if(!imagedateTo.equals("")) param.put("imagedateTo", imagedateTo);
		if(!imageupdateFrom.equals("")) param.put("imageupdateFrom", imageupdateFrom);
		if(!imageupdateTo.equals("")) param.put("imageupdateTo", imageupdateTo);
		if(!labelingdateFrom.equals("")) param.put("labelingdateFrom", labelingdateFrom);
		if(!labelingdateTo.equals("")) param.put("labelingdateTo", labelingdateTo);
		if(!labelingupdateFrom.equals("")) param.put("labelingupdateFrom", labelingupdateFrom);
		if(!labelingupdateTo.equals("")) param.put("labelingupdateTo", labelingupdateTo);
		
		param.put("orderColumn", orderColumn);
		param.put("orderBy", orderBy);

		Map res = filterService.filterDs(param, datasetno.equals(""));
		resultSuccess(res);
		
		return res;
	}
	
}