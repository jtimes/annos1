package com.postinforg.annoscoreapi.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.service.AssignmentService;
import com.postinforg.annoscoreapi.util.AssignmentUtil;
import com.postinforg.annoscoreapi.util.StrNumUtil;

@RestController
public class AssignmentController extends CommonController {
	Logger logger = LoggerFactory.getLogger(AssignmentController.class);

	private AssignmentService assignmentService;
	
	@Autowired
	public AssignmentController(AssignmentService assignmentService) {
		this.assignmentService = assignmentService;
	}
	
	@RequestMapping("/assignmentList")
	public Map assignmentlist(@RequestParam(defaultValue="0") String orgno
			, @RequestParam(defaultValue="") String orgcode
			, @RequestParam(defaultValue="") String userno
			, @RequestParam(defaultValue="") String delyn
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit) {
		Map param = getOffsetLimit(pageno, limit);
		if(!orgcode.equals("")) {
			param.put("orgcode", orgcode);
		}
		if(!delyn.equals("")) {
			param.put("delyn", delyn);
		}
		
		Map res = assignmentService.getassignmentList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@RequestMapping("/assignmentDetail")
	public Map assignmentdetail(@RequestParam(defaultValue="") String assignmentno) {
		if(assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map result = new HashMap();
		Map res = assignmentService.getAssignmentDetail(assignmentno);
		if(res != null) {
			res.put("assignmentlocation", 
					assignmentService.getAssignmentLocation(res));
			result.put("detail", res);
			resultSuccess(result);
		}
		else {
			resultFail(ERR_CD_NODATA, ERR_MSG_NODATA);
		}
		
		return result;
	}
	
	@PostMapping("/assignmentDel")
	public Map assignmentDel(@RequestParam(defaultValue="") String assignmentno) {
		if(assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int res = assignmentService.assignmentDel(assignmentno);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/assignmentAdd")
	public Map assignmentAdd(@RequestParam(defaultValue="") String name
			, @RequestParam(defaultValue="") String note
			, @RequestParam(defaultValue="") String location
			, @RequestParam(defaultValue="") String userno
			, @RequestParam(defaultValue="") String orgcode) {
		if(name.equals("") || orgcode.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		String oname = "";
		String onote = "";
		try {
			oname = URLDecoder.decode(name, "UTF-8");
			onote = URLDecoder.decode(note, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		int res = assignmentService.assignmentAdd(oname, onote, userno, orgcode, location);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		Map retMap = resultSuccess("assignmentno", res);
		retMap.put("assignmentlocation", assignmentService.getAssignmentLocation(res));
		return retMap;
	}
	
	@PostMapping("/assignmentUpdate")
	public Map assignmentUpdate(@RequestParam(defaultValue="") String name
			, @RequestParam(defaultValue="") String note
			, @RequestParam(defaultValue="") String modelname
			, @RequestParam(defaultValue="") String modelyn
			, @RequestParam(defaultValue="") String mgtcount
			, @RequestParam(defaultValue="") String mgtyn
			, @RequestParam(defaultValue="") String mgttime
			, @RequestParam(defaultValue="") String envinfo
			, @RequestParam(defaultValue="") String assignmentno) {
		if(assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		String modelYn = modelyn.toUpperCase();
		if(!modelYn.equals("") && !modelYn.equals("Y") && !modelYn.equals("N")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		String mgtYn = mgtyn.toUpperCase();
		if(!mgtYn.equals("") && !mgtYn.equals("Y") && !mgtYn.equals("N")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("no", assignmentno);
		if(!modelname.equals("")) param.put("modelname", modelname);
		if(!modelYn.equals("")) param.put("modelyn", modelYn);
		if(!mgtYn.equals("")) param.put("mgtyn", mgtYn);
		if(!mgttime.equals("") && StrNumUtil.isDatetime(mgttime)) {
			param.put("mgttime", mgttime);
		}
		
		String oname = "";
		String onote = "";
		try {
			if(!envinfo.equals("")) param.put("envinfo", URLDecoder.decode(envinfo, "UTF-8"));
			oname = URLDecoder.decode(name, "UTF-8");
			onote = URLDecoder.decode(note, "UTF-8");
		
			if(!oname.equals("")) param.put("name", oname);
			if(!onote.equals("")) param.put("note", onote);
			
			logger.debug(">>> mgtcount : " + mgtcount);
			if(!mgtcount.equals("")) {
				int mCount = Integer.parseInt(mgtcount.trim());
				logger.debug(">>> mCount : " + mCount);
				param.put("mgtcount", mCount);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int res = assignmentService.assignmentUpdate(param);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}

	@PostMapping("/assignmentFileAdd")
	public Map assignmentFtpZipAdd(@RequestParam(defaultValue="") String assignmentno
			, @RequestParam(defaultValue="") String filepath
			, @RequestParam(defaultValue="") String note
			, @RequestParam(defaultValue="") String userno) {
		if(assignmentno.equals("") || filepath.equals("") || !filepath.endsWith("zip")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		String fromLocation = "";
		String onote = "";
		try {
			fromLocation = URLDecoder.decode(filepath, "UTF-8");
			onote = URLDecoder.decode(note, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Map assignment = assignmentService.getAssignmentDetail(assignmentno);
		if(assignment == null || assignment.isEmpty() || assignment.get("location") == null 
				|| assignment.get("location").toString().equals("")) {
			return resultFail(ERR_CD_TASK1, ERR_MSG_TASK1);
		}
		
		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
		param.put("note", onote);
		param.put("fromLocation", fromLocation);
		String toLocation = assignment.get("location") + AssignmentUtil.DIR_ASSIGNMENT_EXP;
		
		File toLoc = new File(toLocation);
		if(!toLoc.exists()) {
			return resultFail(ERR_CD_NODIR, ERR_MSG_NODIR);
		}
		param.put("toLocation", toLoc);
		
		if(!userno.equals("")) 
			param.put("userno", userno);
		
		int res = assignmentService.assignmentFileAdd(param);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess("assignmentbgno", res);
	}

	@RequestMapping("/assignmentDirList")
	public Map assignmentDirList(@RequestParam(defaultValue="") String status
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit) {
		Map param = getOffsetLimit(pageno, limit);
		if(!status.equals("")) {
			param.put("status", status);
		}
		
		Map res = assignmentService.getAssignmentDirList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@RequestMapping("/evalReqUpdate")
	public Map evalReqUpdate(@RequestParam(defaultValue="") String assignmentno) {
		if(assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("evalreqdate", "true");
		param.put("assignmentno", assignmentno);
		
		Map result = new HashMap();
		int res = assignmentService.evalReqUpdate(param);
		if(res > 0) {
			resultSuccess(result);
		}
		else {
			resultFail(result, ERR_CD_NODATA, ERR_MSG_NODATA);
		}
		
		return result;
	}
	
	@RequestMapping("/esetClearAll")
	public Map esetClearAll(@RequestParam(defaultValue="") String assignmentno) {
		if(assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map result = new HashMap();
		int res = assignmentService.esetClearAll(assignmentno);
		if(res > 0) {
			resultSuccess(result);
		}
		else {
			resultFail(result, ERR_CD_NODATA, ERR_MSG_NODATA);
		}
		
		return result;
	}
	
}