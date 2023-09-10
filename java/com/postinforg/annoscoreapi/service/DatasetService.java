package com.postinforg.annoscoreapi.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postinforg.annoscoreapi.mapper.AssignmentMapper;
import com.postinforg.annoscoreapi.mapper.DatasetMapper;
import com.postinforg.annoscoreapi.mapper.EvalBigMapper;
import com.postinforg.annoscoreapi.mapper.ReportItemMapper;
import com.postinforg.annoscoreapi.mapper.ReportMapper;
import com.postinforg.annoscoreapi.util.JsonFileValidator;
import com.postinforg.annoscoreapi.util.LabelingUtil;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
@Transactional(rollbackFor = Exception.class)
public class DatasetService {
	
	@Autowired JsonFileValidator jsonFileValidator;
	@Resource DatasetMapper datasetMapper;
	@Resource ReportMapper reportMapper;
	@Resource ReportItemMapper reportItemMapper;
	@Resource AssignmentMapper assignmentMapper;
	@Resource EvalBigMapper evalBigMapper;
	
	Logger logger = LoggerFactory.getLogger(DatasetService.class);

	public Map getDatasetList(Map param) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		int total = 0;
		try {
			total = datasetMapper.selectDatasetTotal(param);
			l = datasetMapper.selectDatasetList(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("list", l);
		return res;
	}

	public int datasetAdd(Map param, boolean isFirst) {
		int newDatasetNo = 0;
		
		try {
			newDatasetNo = datasetMapper.datasetAdd(param);
			if(newDatasetNo > 0) {
				newDatasetNo = Integer.parseInt(param.get("no").toString());
				param.put("datasetno", newDatasetNo);
				
				EgovMap ass = assignmentMapper.getAssignmentDetail(param);
				if(ass != null && ass.containsKey("datasetno")) {
					datasetMapper.copyLabelingLink(param);
					reportMapper.reportAdd(param);
					param.put("reportno", param.get("no"));

					param.put("odatasetno", ass.get("datasetno"));
					evalBigMapper.copyEvalCls(param);
					
					param.put("predFlag", "0");
					evalBigMapper.copyEvalObjRef(param);
					param.put("predFlag", "1");
					evalBigMapper.copyEvalObjRef(param);
					
					evalBigMapper.copyEvalFileRef(param);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newDatasetNo;
	}

	public int datasetDel(String datasetno) {
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		int res = 0;
		try {
			evalBigMapper.evaluationFileRefClear(param);
			evalBigMapper.evaluationClsDsClear(param);
			param.put("predFlag", "0");
			evalBigMapper.evaluationObjDsRefClear(param);
			param.put("predFlag", "1");
			evalBigMapper.evaluationObjDsRefClear(param);
			
			datasetMapper.datasetLabelingDel(param);
			res = datasetMapper.datasetDel(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public Map getDatasetLabelingList(Map param) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		int total = 0;
		try {
			total = datasetMapper.selectDatasetLabelingTotal(param);
			if(param.containsKey("ischecked") && param.get("ischecked").toString().equals("0")) {
				l = datasetMapper.selectDatasetLabelingList0(param);
			}
			else {
				l = datasetMapper.selectDatasetLabelingList(param);
			}
			
			ObjectMapper mapper = new ObjectMapper();
			for(EgovMap anno : l) {
				LabelingUtil.checkImageAttribute(anno, mapper, jsonFileValidator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("list", l);
		return res;
	}

	
	public int datasetAnnoAdd(String datasetno, String[] anos) {
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		param.put("anos", anos);
		
		int res = 0;
		try {
			res = datasetMapper.datasetLabelingAdd(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public int datasetAnnoDel(String datasetno, String[] anos) {
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		param.put("anos", anos);
		
		int res = 0;
		try {
//			res = datasetMapper.datasetLabelingDel(param);
			res = evalBigMapper.datasetLabelingDel(param);
			
			datasetMapper.evalNeedTime(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public int datasetEval(Map param) {
		int res = 0;
		try {
			res = datasetMapper.datasetEval(param);
			if(param.get("status").toString().equals("1") && res > 0) {
				reportItemMapper.reportItemInit(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public int datasetLabelingDelAll(Map param) {
		int res = 0;
		try {
//			res = datasetMapper.datasetLabelingDelAll(param);
			res = evalBigMapper.evaluationFileRefClear(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public int datasetLabelingAddAll(Map param) {
		int res = 0;
		try {
			res = datasetMapper.datasetLabelingAddAll(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public Map datasetDetail(Map param) {
		Map res = null;
		try {
			res = datasetMapper.selectDataset(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public EgovMap getDatasetLabelingDetail(Map param) {
		EgovMap res = null;
		try {
			res = datasetMapper.selectDatasetLabelingDetail(param);
			if(res != null) {
				ObjectMapper mapper = new ObjectMapper();
				LabelingUtil.checkImageAttribute(res, mapper, jsonFileValidator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public int datasetUpdate(Map param, boolean b) {
		try {
			int res = datasetMapper.datasetUpdate(param);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int datasetAnnoAddRandom(String datasetno, int cnt) {
		Map param = new HashMap();
		param.put("datasetno", datasetno);

		int result = 0;
		try {
			EgovMap info = datasetMapper.datasetLabelingCount(param);
			
			int orgCount = Integer.parseInt(info.get("orgCount").toString());
			int defCount = Integer.parseInt(info.get("defCount").toString());
			if(orgCount >= defCount) {
				return -2;
			}
			
			int limit = cnt;
			if(orgCount + cnt > defCount) {
				limit = defCount - orgCount;
			}
			
			param.put("limit", limit);
			result = datasetMapper.datasetLabelingAddRandom(param);
			if(result > 0) result = limit;
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int datasetLabelingAddFilter(Map param) {
		int res = 0;
		try {
			res = datasetMapper.datasetLabelingAddFilter(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public int datasetLabelingAddChecked(Map param) {
		int res = 0;
		try {
			EgovMap ass = assignmentMapper.getAssignmentDetail(param);
			param.put("odatasetno", ass.get("datasetno"));
			
			res = datasetMapper.datasetLabelingAddChecked(param);
			datasetMapper.datasetLabelingFileAddChecked(param);
			
			datasetMapper.evalNeedTime(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	public int datasetCheck(String datasetno) {
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		int res = 1;
		try {
			EgovMap dset = datasetMapper.selectDataset(param);
			if(dset == null || dset.get("no") == null ||
					dset.get("settype").toString().equals("1")) {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
