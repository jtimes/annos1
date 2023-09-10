package com.postinforg.annoscoreapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postinforg.annoscoreapi.mapper.UserMapper;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {

	@Resource UserMapper userMapper;
	Logger logger = LoggerFactory.getLogger(UserService.class);

	public Map getUserList(Map param) {
		Map res = new HashMap();
		
		List<EgovMap> l = null;
		int total = 0;
		try {
			total = userMapper.selectUserTotal(param);
			l = userMapper.selectUserList(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("list", l);
		return res;
	}

	public int userDel(String userno) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int userEmailDupCheck(String email) {
		Map param = new HashMap();
		param.put("email", email);
		
		int result = -1;
		try {
			result = userMapper.userEmailDupCheck(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int userAdd(String email, String pw, String oname, String orgcode, String contact) throws Exception {
		int result = -1;
		
		Map param = new HashMap();
		param.put("email", email);
		param.put("pw", pw);
		param.put("username", oname);
		param.put("contact", contact);
		param.put("orgcode", orgcode);
		
		result = userMapper.userAdd(param);
		if(result > 0) {
			result = userMapper.userorgAdd(param);
		}
		return result;
	}

}
