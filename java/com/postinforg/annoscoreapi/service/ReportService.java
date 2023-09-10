package com.postinforg.annoscoreapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.postinforg.annoscoreapi.mapper.ReportMapper;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
public class ReportService {
	
	@Resource ReportMapper reportMapper;
	
	Logger logger = LoggerFactory.getLogger(ReportService.class);

	public Map getReportList(Map param) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		int total = 0;
		try {
			total = reportMapper.selectReportListTotal(param);
			l = reportMapper.selectReportList(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("list", l);
		return res;
	}

	public int reportAdd(Map param) {
		int result = 0;
		try {
			List l = reportMapper.selectReportList(param);
			if(l.size() > 0) return -1;
			
			result = reportMapper.reportAdd(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int reportUpdate(Map param) {
		int result = 0;
		try {
//			List l = reportMapper.selectReportList(param);
//			if(l.size() < 1) return -1;
			
			result = reportMapper.reportUpdate(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
