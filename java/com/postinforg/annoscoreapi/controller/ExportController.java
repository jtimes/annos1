package com.postinforg.annoscoreapi.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.model.Export;
import com.postinforg.annoscoreapi.service.ExportService;
import com.postinforg.annoscoreapi.util.AssignmentUtil;

@RestController
public class ExportController extends CommonController {
	
	@Value("${media.assignmenthome}") private String assignmentHome;
	@Resource ExportService exportService;
	Logger logger = LoggerFactory.getLogger(ExportController.class);

	@RequestMapping("/fileExport") 
	public Map fileExport (@RequestParam(defaultValue="") int assignmentno
			, @RequestParam(defaultValue="") int datasetno
			, @RequestParam(defaultValue="") String filename
			, @RequestParam(defaultValue="") String ziptype
			, @RequestParam(defaultValue="") String exportformat
			, @RequestParam(defaultValue="0") int tasklimit
			, @RequestParam(defaultValue="0") int splitsize
			, @RequestParam(defaultValue="") String menu) {
		if(assignmentno == 0 || datasetno == 0 || filename.equals("") || menu.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		if(!menu.equals("ds") && !menu.equals("oa") && !menu.equals("oe") 
				&& !menu.equals("eds") && !menu.equals("ce") && !menu.equals("zip")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}

		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
		param.put("filename", filename);
		param.put("menu", menu);

		String dir = assignmentHome + AssignmentUtil.getAssignmentDirFromNo(assignmentno)
			+ AssignmentUtil.DIR_ASSIGNMENT_EXP;
		if(menu.equals("zip")) {
			param.put("subdir", dir + filename + "/");
			
			if(exportformat.equals("")) exportformat = Export.DEFAULT_FORMAT;
			if(tasklimit == 0) tasklimit = Export.DEFAULT_TASK_LIMIT;
			if(splitsize == 0) splitsize = Export.DEFAULT_SPLIT_SIZE;
			if(ziptype.equals("")) ziptype = "json";
		}
		else {
			param.put("subdir", dir + menu + "/");
			if(!filename.endsWith(".csv")) {
				logger.debug("filename is not .csv ");
				return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
			}
		}
		
		param.put("exportformat", exportformat);
		param.put("tasklimit", tasklimit);
		param.put("splitsize", splitsize);
		param.put("ziptype", ziptype);
		
		param.put("datasetno", datasetno);
		
		Map res = new HashMap();
		int r = exportService.newExport(param);

		if(r > 0) {
			resultSuccess(res);
			res.put("exportno", param.get("no"));
		}
		else if(r == 0) {
			res = resultFail(ERR_CD_DUPDATA, ERR_MSG_DUPDATA);
		}
		else {
			res = resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
		}
		
		return res;
	}
	
	@RequestMapping("/exportList")
	public Map exportlist(@RequestParam(defaultValue="") String assignmentno
			, @RequestParam(defaultValue="") String no
			, @RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String menu
			, @RequestParam(defaultValue="") String filename
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit
			, @RequestParam(defaultValue="") String orderColumn
			, @RequestParam(defaultValue="asc") String orderBy) {
		
		if(assignmentno.equals("") ) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = getOffsetLimit(pageno, limit);
		param.put("assignmentno", assignmentno);
		if(!no.equals("")) param.put("no", no);
		if(!datasetno.equals("")) param.put("datasetno", datasetno);
		if(!menu.equals("")) param.put("menu", menu);
		if(!filename.equals("")) param.put("filename", filename);
		
		param.put("orderColumn", orderColumn);
		param.put("orderBy", orderBy);
		
		Map res = exportService.getExportList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@RequestMapping("/exportDetail")
	public Map exportDetail(@RequestParam(defaultValue="0") int no ) {
		
		if(no == 0) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		Export param = new Export();
		param.setNo(no);
		
//		Map result = new HashMap();
		Map res = exportService.getExportDetail(param);
		if(res != null) {
			resultSuccess(res);
//			res.put("export", res);
		}
		else {
			res = resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
		}
		
		return res;
	}
	
}
