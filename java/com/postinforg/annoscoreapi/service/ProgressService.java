package com.postinforg.annoscoreapi.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postinforg.annoscoreapi.mapper.ProgressMapper;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProgressService {
	
	@Resource ProgressMapper progressMapper;
	
	Logger logger = LoggerFactory.getLogger(ProgressService.class);
	
	public Map progressAddOrUpdate(Map param) {
		Map res = new HashMap();
		int rr = 0;
		try {
			if(param.containsKey("progressno")) {
				rr = progressMapper.progressUpdate(param);
			}
			else {
				EgovMap prg = progressMapper.selectProgress(param);
				if(prg == null || prg.isEmpty()) {
					rr = progressMapper.progressAdd(param);
					res.put("progressno", param.get("no"));
				}
				else {
					rr = progressMapper.progressUpdate(param);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("count", rr);
		return res;
	}
	
	public void progressAdd(Map param) {
		try {
			param.put("progress", 0);
			param.put("status", "1");
			progressMapper.progressAdd(param);
			param.put("progressno", param.get("no"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map progressDetail(Map param) {
		EgovMap prg = new EgovMap();
		try {
			prg = progressMapper.selectProgress(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return prg;
	}

}
