package com.postinforg.annoscoreapi.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.service.ExportService;
import com.postinforg.annoscoreapi.service.MailSend;
import com.postinforg.annoscoreapi.service.ManageService;

@RestController
public class ManageController extends CommonController {
	
	@Resource ManageService manageService;
	@Autowired MailSend mailSend;
	@Resource ExportService exportService;
	
	Logger logger = LoggerFactory.getLogger(ManageController.class);

	@PostMapping("/login")
	public Map login(@RequestParam String name, @RequestParam String pw) {
		if(name == null || pw == null || name.equals("") || pw.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		Map res = manageService.login(name, pw);
		if(res == null || res.isEmpty()) {
			return resultFail(ERR_CD_LOGIN, ERR_MSG_LOGIN);
		}
		
		res.put(KEY_RES, SUCC);
		return res;
	}
	
	@RequestMapping("/orgList")
	public Map orglist(@RequestParam(defaultValue="") String delyn
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit) {
		Map param = getOffsetLimit(pageno, limit);

		if(!delyn.equals("")) {
			delyn = delyn.toUpperCase();
			if(!delyn.equals("Y") && !delyn.equals("N")) 
				return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
			param.put("delyn", delyn);
		}
		
		Map res = manageService.getOrgList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@RequestMapping("/orgNewCode")
	public Map orgNewCode() {
		SecureRandom ran = new SecureRandom();
		
		int maxnum = manageService.orgnewcode();
		if(maxnum < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		String value = String.format("AS%04d%04d", maxnum, ran.nextInt(10000));
		return resultSuccess("orgcode", value);
	}
	
	@PostMapping("/orgAdd")
	public Map orgAdd(@RequestParam String orgname, @RequestParam String orgcode
			, @RequestParam String managername, @RequestParam String email, @RequestParam String contact) {
		if(orgname == null || orgcode == null || orgname.equals("") || orgcode.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		String oname = "";
		String mname = "";
		if(orgname != null && !orgname.equals("") && 
				managername != null && !managername.equals("")) {
			try {
				oname = URLDecoder.decode(orgname, "UTF-8");
				mname = URLDecoder.decode(managername, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		int res = manageService.orgcreate(oname, orgcode, mname, email, contact);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/orgUpdate")
	public Map orgUpdate(@RequestParam String orgname, @RequestParam String orgcode
			, @RequestParam String managername, @RequestParam String email, @RequestParam String contact) {
		if(orgname == null || orgcode == null || orgname.equals("") || orgcode.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		String oname = "";
		String mname = "";
		if(orgname != null && !orgname.equals("") && 
				managername != null && !managername.equals("")) {
			try {
				oname = URLDecoder.decode(orgname, "UTF-8");
				mname = URLDecoder.decode(managername, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		int res = manageService.orgupdate(oname, orgcode, mname, email, contact);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/orgDel")
	public Map orgDel(@RequestParam String orgcode) {
		if(orgcode == null || orgcode.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int res = manageService.orgDel(orgcode);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/sendOrgCode")
	public Map sendOrgCode(@RequestParam(defaultValue="") String orgcode
			, @RequestParam(defaultValue="") String mailTo) {
		if(orgcode.equals("") || mailTo.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			String[] to = { mailTo };
			mailSend.mailSending("AI Annotaion 에서 기관코드를 발송했습니다.", orgcode, to, null);
		}
		catch(Exception e) {
			return resultFail(ERR_CD_SYS, ERR_MSG_SYS1);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/existOrgCode")
	public Map existOrgCode(@RequestParam(defaultValue="") String orgcode) {
		if(orgcode.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int res = manageService.existOrgCode(orgcode);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}
	
	@RequestMapping("/checkedUpdate")
	public Map checkedUpdate(@RequestParam(defaultValue="") String menu
			, @RequestParam(defaultValue="") String assignmentno
			, @RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String nos
			, @RequestParam(defaultValue="") String value) {
		if(menu.equals("") || assignmentno.equals("") || nos.equals("") || value.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		if(!menu.equals("model") && !menu.equals("ds") && !menu.equals("oa") && !menu.equals("oe")
				&& !menu.equals("eds") && !menu.equals("ce")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		if(!value.equals("1") && !value.equals("0")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		String table = "";
		if(menu.equals("model")) {
			table = "assignment_hgt";
			datasetno = "";
		}
		else if(menu.equals("ds")) {
			table = "labeling";
			datasetno = "";
		}
		else if(menu.equals("eds")) table = "eval_file_ref";
		else if(menu.equals("oa")) table = "eval_obj_pred0_ref";
		else if(menu.equals("oe")) table = "eval_obj_pred1_ref";
		else if(menu.equals("ce")) table = "eval_cls";
		
		Map param = new HashMap();
		param.put("table", table);
		param.put("value", value);
		
		param.put("assignmentno", assignmentno);
		if(!datasetno.equals("")) param.put("datasetno", datasetno);
		try {
			if(nos.equals("all")) {
			}
			else {
				param.put("nos", nos);
			}
			int res = manageService.checkedUpdate(param, menu);
			
			if(res < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		
		return resultSuccess();
	}

}
