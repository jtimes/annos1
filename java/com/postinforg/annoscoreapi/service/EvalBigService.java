package com.postinforg.annoscoreapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.postinforg.annoscoreapi.mapper.DatasetMapper;
import com.postinforg.annoscoreapi.mapper.EvalBigMapper;
import com.postinforg.annoscoreapi.mapper.EvalMapper;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
public class EvalBigService {
	
	@Resource EvalMapper evalMapper;
	@Resource EvalBigMapper evalBigMapper;
	@Resource DatasetMapper datasetMapper;
	
	Logger logger = LoggerFactory.getLogger(EvalBigService.class);

	public int evaluationBigSetup(Map param) {
		int res = 0;
		try {
//			EgovMap dset = datasetMapper.selectDataset(param);
//			if(dset == null || dset.isEmpty()) {
//				return -1;
//			}
			param.put("status", "0");
			List l = evalBigMapper.evaluationBigStatus(param);
			if(l != null && l.size() > 0) {
				return -2;
			}
			
			res = evalBigMapper.evaluationBigSetup(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public Map evaluationBigStatus(Map param) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		try {
			l = evalBigMapper.evaluationBigStatus(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("list", l);
		return res;
	}
	
}
