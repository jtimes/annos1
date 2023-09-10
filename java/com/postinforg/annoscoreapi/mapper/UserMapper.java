package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface UserMapper {

	int selectUserTotal(Map param) throws Exception;
	List<EgovMap> selectUserList(Map param) throws Exception;
	int userDel(String userno) throws Exception;
	int userEmailDupCheck(Map param) throws Exception;
	int userAdd(Map param);
	int userorgAdd(Map param);
	
}
