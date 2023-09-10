package com.postinforg.annoscoreapi.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.postinforg.annoscoreapi.service.LabelingService;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@RestController
public class LabelingController extends CommonController {
	
	@Resource LabelingService labelingService;
	Logger logger = LoggerFactory.getLogger(LabelingController.class);
	
	@RequestMapping("/labelingList")
	public Map labelinglist(@RequestParam(defaultValue="") String assignmentno
			, @RequestParam(defaultValue="") String datasetno
			, @RequestParam(defaultValue="0") String pageno
			, @RequestParam(defaultValue="") String gttype
			, @RequestParam(defaultValue="") String mgtflag
			, @RequestParam(defaultValue="") String imagesizeFrom
			, @RequestParam(defaultValue="") String imagesizeTo
			, @RequestParam(defaultValue="") String imagewidthFrom
			, @RequestParam(defaultValue="") String imagewidthTo
			, @RequestParam(defaultValue="") String imageheightFrom
			, @RequestParam(defaultValue="") String imageheightTo
			, @RequestParam(defaultValue="") String imageratioFrom
			, @RequestParam(defaultValue="") String imageratioTo
			, @RequestParam(defaultValue="") String labelingsizeFrom
			, @RequestParam(defaultValue="") String labelingsizeTo
			, @RequestParam(defaultValue="") String imagedateFrom
			, @RequestParam(defaultValue="") String imagedateTo
			, @RequestParam(defaultValue="") String imageupdateFrom
			, @RequestParam(defaultValue="") String imageupdateTo
			, @RequestParam(defaultValue="") String imagevalidity
			, @RequestParam(defaultValue="") String labelingdateFrom
			, @RequestParam(defaultValue="") String labelingdateTo
			, @RequestParam(defaultValue="") String labelingupdateFrom
			, @RequestParam(defaultValue="") String labelingupdateTo
			, @RequestParam(defaultValue="") String labelingvalidity
			, @RequestParam(defaultValue=DEFAULT_LIMIT) String limit
			, @RequestParam(defaultValue="") String orderColumn
			, @RequestParam(defaultValue="asc") String orderBy) {
		if(assignmentno.equals("") || (!mgtflag.equals("") && !mgtflag.equals("1") && !mgtflag.equals("0"))
				|| (!gttype.equals("") && !gttype.equals("hgt") && !gttype.equals("mgt"))) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		if(gttype.equals("mgt") && !mgtflag.equals("1") && !mgtflag.equals("0")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = getOffsetLimit(pageno, limit);
		param.put("assignmentno", assignmentno);
		param.put("gttype", gttype.toLowerCase().trim());
		param.put("mgtflag", mgtflag);
		
		if(!datasetno.equals("")) param.put("datasetno", datasetno);
		if(!imagesizeFrom.equals("")) param.put("imagesizeFrom", imagesizeFrom);
		if(!imagesizeTo.equals("")) param.put("imagesizeTo", imagesizeTo);
		if(!imagewidthFrom.equals("")) param.put("imagewidthFrom", imagewidthFrom);
		if(!imagewidthTo.equals("")) param.put("imagewidthTo", imagewidthTo);
		if(!imageheightFrom.equals("")) param.put("imageheightFrom", imageheightFrom);
		if(!imageheightTo.equals("")) param.put("imageheightTo", imageheightTo);
		if(!imageratioFrom.equals("")) param.put("imageratioFrom", imageratioFrom);
		if(!imageratioTo.equals("")) param.put("imageratioTo", imageratioTo);
		if(!labelingsizeFrom.equals("")) param.put("labelingsizeFrom", labelingsizeFrom);
		if(!labelingsizeTo.equals("")) param.put("labelingsizeTo", labelingsizeTo);
		if(!imagevalidity.equals("")) param.put("imagevalidity", imagevalidity);
		if(!labelingvalidity.equals("")) param.put("labelingvalidity", labelingvalidity);
		
		if(!imagedateFrom.equals("")) param.put("imagedateFrom", imagedateFrom);
		if(!imagedateTo.equals("")) param.put("imagedateTo", imagedateTo);
		if(!imageupdateFrom.equals("")) param.put("imageupdateFrom", imageupdateFrom);
		if(!imageupdateTo.equals("")) param.put("imageupdateTo", imageupdateTo);
		if(!labelingdateFrom.equals("")) param.put("labelingdateFrom", labelingdateFrom);
		if(!labelingdateTo.equals("")) param.put("labelingdateTo", labelingdateTo);
		if(!labelingupdateFrom.equals("")) param.put("labelingupdateFrom", labelingupdateFrom);
		if(!labelingupdateTo.equals("")) param.put("labelingupdateTo", labelingupdateTo);
		
		param.put("orderColumn", orderColumn);
		param.put("orderBy", orderBy);

		Map res = labelingService.getLabelingList(param);
		resultSuccess(res, param);
		
		return res;
	}
	
	@RequestMapping("/labelingGtAdd")
	public Map labelingGtAdd(@RequestParam(defaultValue="") String labelingno
			, @RequestParam(defaultValue="") String assignmentno) {
		
		if(labelingno.equals("") || assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			String[] anos = URLDecoder.decode(labelingno, "UTF-8").split(",");
			
			int result = labelingService.labelingGtAdd(anos, assignmentno);
			if(result < 1) {
				return resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}
	
	@RequestMapping("/labelingGtAddAll")
	public Map labelingGtAddAll(@RequestParam(defaultValue="") String assignmentno) {
		
		if(assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			int result = labelingService.labelingGtAddAll(assignmentno);
			logger.debug(">>> labelingService.labelingGtAddAll result : " + result);
			
			if(result < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}
	
	@RequestMapping("/labelingGtAddRandom")
	public Map labelingGtAddRandom(@RequestParam(defaultValue="") String count
			, @RequestParam(defaultValue="") String assignmentno) {
		
		if(count.equals("") || assignmentno.equals("")) {
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
		
		try {
			int result = labelingService.labelingGtAddRandom(assignmentno, cnt);
			if(result == -2) {
				return resultFail(ERR_CD_PARAM2, ERR_MSG_PARAM2);
			}
			else if(result == -3) {
				return resultFail(ERR_CD_FILE2, ERR_MSG_FILE2);
			}
			else if(result < 1) {
				return resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}
	
	@RequestMapping("/labelingGtDel")
	public Map labelingGtDel(@RequestParam(defaultValue="") String hgtno
			, @RequestParam(defaultValue="") String assignmentno) {
		
		if(hgtno.equals("") || assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			String[] anos = URLDecoder.decode(hgtno, "UTF-8").split(",");
			
			int result = labelingService.labelingGtDel(anos, assignmentno);
			if(result < 1) {
				return resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}
	
	@RequestMapping("/labelingGtDelAll")
	public Map labelingGtDelAll(@RequestParam(defaultValue="") String assignmentno) {
		
		if(assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			int result = labelingService.labelingGtDelAll(assignmentno);
			if(result < 1) {
				return resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}
	
	@RequestMapping("/labelingMgtUpdate")
	public Map labelingMgtUpdate(@RequestParam(defaultValue="") String labelingno
			, @RequestParam(defaultValue="") String mgtdir
			, @RequestParam(defaultValue="") String mgtfile) {
		
		if(labelingno.equals("") ||  mgtdir.equals("") || mgtfile.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			int result = labelingService.labelingMgtUpdate(labelingno, mgtdir, mgtfile);
			if(result < 1) {
				return resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}
	
	@RequestMapping("/labelingMgtUpdateBulk")
	public Map labelingMgtUpdateBulk(
			@RequestParam(defaultValue="") String mgtinfo) {
		
		if(mgtinfo.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		int result = 0;
		try {
			result = labelingService.labelingMgtUpdateBulk(URLDecoder.decode(mgtinfo, "UTF-8"));
			if(result < 1) {
				return resultFail(ERR_CD_DAO1, ERR_MSG_DAO1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess("updateCount", result);
	}
	
	@RequestMapping("/labelingMgtReset")
	public Map labelingMgtReset(@RequestParam(defaultValue="") String assignmentno) {
		
		if(assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			int result = labelingService.labelingMgtReset(assignmentno);
			if(result < 1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}
	
	@RequestMapping("/labeleditor/loadLabelingData")
	public Map labeldLoadLabelingData(@RequestParam(defaultValue="") String labelingno) {
		
		if(labelingno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("labelingno", labelingno);
		Map res = labelingService.getLabelingInfo(param);
		if(res == null || res.isEmpty()) {
			return resultFail(ERR_CD_NODATA, ERR_MSG_NODATA);
		}
		
		resultSuccess(res);
		return res;
	}
	
	@RequestMapping("/labeleditor/saveLabelingData")
	public Map labelsaveLabelingData(@RequestParam(defaultValue="") String data
			, @RequestParam(defaultValue="") String no) {
		
		if(no.equals("") || data.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("labelingno", no);
		Map labeling = labelingService.getLabelingInfo(param);
		if(labeling == null || labeling.isEmpty()) {
			return resultFail(ERR_CD_NODATA, ERR_MSG_NODATA);
		}
		
		Map res = resultSuccess();
		String resStr = labelingService.saveLabeling(labeling, data, false);
		if(resStr.equals("")) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		else if(resStr.equals("fe")) {
			return resultFail(ERR_CD_FILE1, ERR_MSG_FILE1);
		}
		else {
			// res.put("orgfilename", orgfilename);
			// res.put("newfilename", resStr);
		}
		
		return res;
	}
	
	@RequestMapping("/gteditor/loadLabelingData")
	public Map loadLabelingData(@RequestParam(defaultValue="") String hgtno) {

		if(hgtno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("hgtno", hgtno);
		Map res = labelingService.getGtInfo(param);
		if(res == null) {
			return resultFail(ERR_CD_NODATA, ERR_MSG_NODATA);
		}
		
		resultSuccess(res);
		return res;
	}
	
	@RequestMapping("/gteditor/saveLabelingData")
	public Map saveLabelingData(@RequestParam(defaultValue="") String data
			, @RequestParam(defaultValue="") String hgtno) {
		
		if(hgtno.equals("") || data.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		Map param = new HashMap();
		param.put("hgtno", hgtno);
		Map anno = labelingService.getGtInfo(param);
		if(anno == null || anno.isEmpty()) {
			return resultFail(ERR_CD_NODATA, ERR_MSG_NODATA);
		}
		
		Map res = resultSuccess();
		String resStr = labelingService.saveLabeling(anno, data, true);
		if(resStr.equals("")) {
			return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
		}
		else if(resStr.equals("fe")) {
			return resultFail(ERR_CD_FILE1, ERR_MSG_FILE1);
		}
		else {
			// res.put("orgfilename", orgfilename);
			// res.put("newfilename", resStr);
		}
		
		return res;
	}

	@RequestMapping("/labelingGtAddChecked")
	public Map labelingGtAddChecked(
			@RequestParam(defaultValue="") String assignmentno) {
		
		if(assignmentno.equals("")) {
			return resultFail(ERR_CD_PARAM, ERR_MSG_PARAM);
		}
		
		try {
			int result = labelingService.labelingGtAddChecked(assignmentno);
			logger.debug(">>> labelingService.labelingGtAddChecked result : " + result);
			
			if(result == -1) {
				return resultFail(ERR_CD_DAO2, ERR_MSG_DAO2);
			}
			else if(result == -2) {
				return resultFail(ERR_CD_NODATA, ERR_MSG_NODATA);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return resultFail(ERR_CD_PARSE, ERR_MSG_PARSE);
		}
		return resultSuccess();
	}
	
}