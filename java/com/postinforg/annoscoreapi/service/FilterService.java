package com.postinforg.annoscoreapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postinforg.annoscoreapi.mapper.AssignmentMapper;
import com.postinforg.annoscoreapi.mapper.FilterMapper;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
@Transactional(rollbackFor = Exception.class)
public class FilterService {
	
	@Resource FilterMapper filterMapper;
	@Resource AssignmentMapper assignmentMapper;
	
	Logger logger = LoggerFactory.getLogger(FilterService.class);
	
	public Map filterDs(Map param, boolean isDsOrEds) {
		Map res = new HashMap();
		int total = 0;
		String checked = param.get("checked").toString();
		
		try {
			EgovMap asm = assignmentMapper.getAssignmentDetail(param);
			if(param.containsKey("datasetno")) {
				if(param.get("datasetno").toString().equals(
						asm.get("datasetno").toString())) isDsOrEds = true;
			}
			
			if(isDsOrEds) {
				if(checked.equals("1"))
					total = filterMapper.filterDsUpdate0(param);
				total = filterMapper.filterDsUpdate(param);
			}
			else {
				if(checked.equals("1"))
					total = filterMapper.filterEdsUpdate0(param);
				total = filterMapper.filterEdsUpdate(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		return res;
	}

	

}
