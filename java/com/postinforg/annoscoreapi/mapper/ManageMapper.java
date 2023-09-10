package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface ManageMapper {

	Map login(Map param) throws Exception;
	int orgcreate(Map param) throws Exception;
	int orgupdate(Map param) throws Exception;
	int orgnewcode() throws Exception;
	int orgDel(String orgcode) throws Exception;
	int selectOrgTotal(Map param) throws Exception;
	List<EgovMap> selectOrgList(Map param) throws Exception;
	int existOrgCode(String orgcode) throws Exception;
	int checkedUpdate(Map param) throws Exception;
	int checkedUpdateLabeling(Map param) throws Exception;
}
