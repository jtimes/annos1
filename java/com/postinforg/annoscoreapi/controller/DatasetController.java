package com.postinforg.annoscoreapi.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.service.DatasetService;

@RestController
public class DatasetController extends CommonController {
	
	@Resource DatasetService datasetService;
	
	@RequestMapping("/datasetList")
	public Map datasetList(@RequestParam(defaultValue="0") String assignmentno
			, @RequestParam(defaultValue="") String delyn
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit) {
		
		if(assignmentno.equals("0")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = getOffsetLimit(pageno, limit);
		param.put("assignmentno", assignmentno);
		
		Map res = datasetService.getDatasetList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@PostMapping("/datasetAdd")
	public Map datasetAdd(@RequestParam(defaultValue="") String name
			, @RequestParam(defaultValue="") String note
			, @RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String assignmentno ) {
		if(name.equals("") || assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		String oname = "";
		String onote = "";
		try {
			oname = URLDecoder.decode(name, "UTF-8");
			onote = URLDecoder.decode(note, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Map param = new HashMap();
		param.put("name", oname);
		param.put("note", onote);
		param.put("assignmentno", assignmentno);
		
		Map result = new HashMap();
		int res = 0;
		if(datasetno.equals("")) {
			res = datasetService.datasetAdd(param, false);
			result.put("reportno", param.get("reportno"));
		}
		else {
			param.put("datasetno", datasetno);
			res = datasetService.datasetUpdate(param, false);
		}
		
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		resultSuccess(result);
		result.put("no", res);
		
		return result;
	}
	
	@PostMapping("/datasetDel")
	public Map datasetDel(@RequestParam(defaultValue="") String datasetno ) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int res = datasetService.datasetCheck(datasetno);
		if(res < 0) return resultFail(ERR_CD_DAO4, ERR_MSG_DAO4);
		
		res = datasetService.datasetDel(datasetno);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}
	
	@RequestMapping("/datasetLabelingList")
	public Map datasetLabelingList(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String isChecked
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit) {
		
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = getOffsetLimit(pageno, limit);
		param.put("datasetno", datasetno);
		if(!isChecked.equals("")) param.put("ischecked", isChecked);
		
		Map res = datasetService.getDatasetLabelingList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@RequestMapping("/datasetLabelingDetail")
	public Map datasetLabelingDetil(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String labelingno) {
		
		if(datasetno.equals("") || labelingno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		param.put("labelingno", labelingno);
		
		Map res = datasetService.getDatasetLabelingDetail(param);
		
		Map result = new HashMap();
		if(res != null) {
			result.put("detail", res);
			resultSuccess(result);
		}
		else {
			return resultFail(ERR_CD_NODATA, ERR_MSG_NODATA);
		}
		
		return result;
	}
	
	@PostMapping("/datasetLabelingAdd")
	public Map datasetLabelingAdd(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String labelingno) {
		if(datasetno.equals("") || labelingno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int res = datasetService.datasetCheck(datasetno);
		if(res < 0) return resultFail(ERR_CD_DAO4, ERR_MSG_DAO4);
		try {
			String[] anos = URLDecoder.decode(labelingno, "UTF-8").split(",");
			
			res = datasetService.datasetAnnoAdd(datasetno, anos);
			if(res == -2) {
				return resultFail(ERR_CD_DAO4, ERR_MSG_DAO4);
			}
			else if(res < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/datasetLabelingAddRandom")
	public Map datasetLabelingAddRandom(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String count) {
		if(datasetno.equals("") || count.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int cnt = 0;
		try {
			cnt = Integer.parseInt(count);
		}
		catch(Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int result = 0;
		try {
			int res = datasetService.datasetCheck(datasetno);
			if(res < 0) return resultFail(ERR_CD_DAO4, ERR_MSG_DAO4);
			
			result = datasetService.datasetAnnoAddRandom(datasetno, cnt);
			if(result == -2) {
				return resultFail(ERR_CD_PARAM2, ERR_MSG_PARAM2);
			}
			else if(result == -3) {
				return resultFail(ERR_CD_FILE2, ERR_MSG_FILE2);
			}
			else if(result < 1) {
				return resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		
		return resultSuccess("insertCount", result);
	}
	
	@PostMapping("/datasetLabelingAddAll")
	public Map datasetLabelingAddAll(
			@RequestParam(defaultValue="") String assignmentno
			, @RequestParam(defaultValue="") String datasetno) {
		if(datasetno.equals("") || assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
		param.put("datasetno", datasetno);
		
		try {
			int res = datasetService.datasetCheck(datasetno);
			if(res < 0) return resultFail(ERR_CD_DAO4, ERR_MSG_DAO4);
			
			res = datasetService.datasetLabelingDelAll(param);
			res = datasetService.datasetLabelingAddAll(param);
			if(res < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/datasetLabelingAddFilter")
	public Map datasetLabelingAddFilter(@RequestParam(defaultValue="") String assignmentno
			, @RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit
			, @RequestParam(defaultValue="") String orderColumn
			, @RequestParam(defaultValue="asc") String orderBy) {
		
		if(datasetno.equals("") || assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = getOffsetLimit("1", limit);
		param.put("assignmentno", assignmentno);
		param.put("datasetno", datasetno);
		
		param.put("orderColumn", orderColumn);
		param.put("orderBy", orderBy);
		
		try {
			int res = datasetService.datasetCheck(datasetno);
			if(res < 0) return resultFail(ERR_CD_DAO4, ERR_MSG_DAO4);
			
			res = datasetService.datasetLabelingAddFilter(param);
			if(res < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		
		return resultSuccess();
	}
	
	@PostMapping("/datasetLabelingDelAll")
	public Map datasetLabelingDelAll(@RequestParam(defaultValue="") String datasetno) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			Map param = new HashMap();
			param.put("datasetno", datasetno);
			
			int res = datasetService.datasetCheck(datasetno);
			if(res < 0) return resultFail(ERR_CD_DAO4, ERR_MSG_DAO4);
			
			res = datasetService.datasetLabelingDelAll(param);
			if(res < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		
		return resultSuccess();
	}
	
	
	@PostMapping("/datasetLabelingDel")
	public Map datasetLabelingDel(
			@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String labelingno) {
		if(datasetno.equals("") || labelingno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			int res = datasetService.datasetCheck(datasetno);
			if(res < 0) return resultFail(ERR_CD_DAO4, ERR_MSG_DAO4);
			
			String[] anos = URLDecoder.decode(labelingno, "UTF-8").split(",");
			
			res = datasetService.datasetAnnoDel(datasetno, anos);
			if (res < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		
		return resultSuccess();
	}
	
	
	@PostMapping("/datasetEval")
	public Map datasetEval(@RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="") String status ) {
		if(datasetno.equals("") || status.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		if(!status.equals("0") && !status.equals("1") && !status.equals("9")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		param.put("status", status);
		
		int res = datasetService.datasetEval(param);
		if(res < 1) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		
		return resultSuccess();
	}

	@PostMapping("/datasetDetail")
	public Map datasetAdd(@RequestParam(defaultValue="") String datasetno ) {
		if(datasetno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		
		Map res = datasetService.datasetDetail(param);;
		resultSuccess(res);
		
		return res;
	}
	
	@PostMapping("/datasetLabelingAddChecked")
	public Map datasetLabelingAddChecked(
			@RequestParam(defaultValue="") String assignmentno
			, @RequestParam(defaultValue="") String datasetno) {
		
		if(datasetno.equals("") || assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
		param.put("datasetno", datasetno);
		
		int res = datasetService.datasetLabelingAddChecked(param);
		Map result = resultSuccess();
		result.put("count", res);
		
		return result;
	}
	
}
