package com.postinforg.annoscoreapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.postinforg.annoscoreapi.mapper.ManageMapper;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
public class ManageService {
	
	@Resource
	private ManageMapper manageMapper;

	public Map login(String name, String pw) {
		Map param = new HashMap();
		param.put("name", name);
		param.put("pw", pw);
		
		Map result = new HashMap();
		try {
			result = manageMapper.login(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int orgcreate(String orgname, String orgcode, String managername
			, String email, String contact) {
		int result = -1;
		
		Map param = new HashMap();
		param.put("orgname", orgname);
		param.put("orgcode", orgcode);
		param.put("managername", managername);
		param.put("email", email);
		param.put("contact", contact);
		
		try {
			result = manageMapper.orgcreate(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int orgupdate(String orgname, String orgcode, String managername
			, String email, String contact) {
		int result = -1;
		
		Map param = new HashMap();
		param.put("orgname", orgname);
		param.put("orgcode", orgcode);
		param.put("managername", managername);
		param.put("email", email);
		param.put("contact", contact);
		
		try {
			result = manageMapper.orgupdate(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int orgnewcode() {
		int result = 0;
		try {
			result = manageMapper.orgnewcode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int orgDel(String orgcode) {
		int result = 0;
		try {
			result = manageMapper.orgDel(orgcode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map getOrgList(Map param) {
		Map res = new HashMap();
		
		List<EgovMap> l = null;
		int total = 0;
		try {
			total = manageMapper.selectOrgTotal(param);
			l = manageMapper.selectOrgList(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("list", l);
		return res;
	}

	public int existOrgCode(String orgcode) {
		int result = 0;
		try {
			result = manageMapper.existOrgCode(orgcode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int checkedUpdate(Map param, String menu) {
		int result = -1;
		try {
			result = manageMapper.checkedUpdate(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
