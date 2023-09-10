package com.postinforg.annoscoreapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.postinforg.annoscoreapi.mapper.AssignmentMapper;
import com.postinforg.annoscoreapi.mapper.ExportMapper;
import com.postinforg.annoscoreapi.model.Export;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
public class ExportService {
	
	Logger logger = LoggerFactory.getLogger(ExportService.class);
	
	@Resource ExportMapper exportMapper;
	@Resource AssignmentMapper assignmentMapper;

	public int newExport(Map param) {
		int res = -1;
		try {
			res = exportMapper.selectExportTotal(param);
			if(res > 0) return 0;
			
			EgovMap asm = assignmentMapper.getAssignmentDetail(param);
			param.put("odatasetno", asm.get("datasetno"));
			res = exportMapper.exportAdd(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

	public Map getExportList(Map param) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		int total = 0;
		try {
			total = exportMapper.selectExportTotal(param);
			l = exportMapper.selectExportList(param);
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("list", l);
		return res;
	}

	public Map getExportDetail(Export param) {
		Map res = null;
		try {
			res = exportMapper.exportDetail(param);
		} catch (Exception e) {
			// e.printStackTrace();
			e.getMessage();
		}
		return res;
	}

}
