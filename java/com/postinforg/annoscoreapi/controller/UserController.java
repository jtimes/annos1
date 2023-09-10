package com.postinforg.annoscoreapi.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.mapper.EvalBigMapper;
import com.postinforg.annoscoreapi.service.UserService;
import com.postinforg.annoscoreapi.util.FileImageUtil;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@RestController
public class UserController extends CommonController {
	
	@Resource UserService userService;
	
	@RequestMapping("/userList")
	public Map userlist(@RequestParam(defaultValue="") String orgcode
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue="") String delyn
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit) {
		Map param = getOffsetLimit(pageno, limit);
		
		if(!delyn.equals("")) {
			delyn = delyn.toUpperCase();
			if(!delyn.equals("Y") && !delyn.equals("N")) 
				return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
			param.put("delyn", delyn);
		}
		
		if(!orgcode.equals("")) {
			param.put("orgcode", orgcode);
		}
		
		Map res = userService.getUserList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@PostMapping("/userDel")
	public Map userDel(@RequestParam(defaultValue="") String userno) {
		if(userno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int res = userService.userDel(userno);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/emailDupCheck")
	public Map emailDupCheck(@RequestParam(defaultValue="") String email) {
		if(email.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int res = userService.userEmailDupCheck(email);
		if(res < 0) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		if(res == 0) {
			return resultSuccess();
		}
		
		return resultFail(ERR_CD_DUPMAIL, ERR_MSG_DUPMAIL);
	}
	
	@PostMapping("/userAdd")
	public Map userAdd(@RequestParam String email, @RequestParam String pw
			, @RequestParam String orgcode , @RequestParam String username
			, @RequestParam String contact) {
		if(username == null || orgcode == null || username.equals("") || orgcode.equals("")
				|| pw == null || pw.equals("") || email == null || email.equals("")
				|| contact == null || contact.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		String oname = "";
		if(username != null && !username.equals("")) {
			try {
				oname = URLDecoder.decode(username, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		int res = -1;
		try {
			res = userService.userAdd(email, pw, oname, orgcode, contact);
		} catch(Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		if(res > 0)
			return resultSuccess();
		
		return resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
	}
	
	@RequestMapping("/checkEx")
	public Map checkEx() {
		Map res = new HashMap();
		String location = "/aaihq/annoscore-assignment/0000013";
		String commands = "du --max-depth=0 " + location;
		
		String result = null;
		try {
			result = FileImageUtil.runCommand(commands);
			
			System.out.println(">>> result : " + result);
			if(result != null) {
				String[] ss = result.split("\t");
				for(String s : ss) {
					System.out.println("> " + s +"<");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		res.put("size", result);
		resultSuccess(res);
		
		return res;
	}
	
	
	@Autowired EvalBigMapper evalBigMapper;
	@RequestMapping("/evalBigTest")
	public Map evalBigTest(@RequestParam(defaultValue="") String datasetno) {
		if(datasetno.equals("") ) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		EgovMap em = new EgovMap();
		em.put("datasetno", datasetno);
		int re = 0;
		try {
			re = evalBigMapper.deleteEvalBigFile(em);
			re += evalBigMapper.insertEvalBigFile(em);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Map res = new HashMap();
		res.put("cnt", re);
		resultSuccess(res);
		
		return res;
	}
	
}