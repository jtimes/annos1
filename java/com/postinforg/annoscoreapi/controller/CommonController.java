package com.postinforg.annoscoreapi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonController {
	public static final String CHAR_SER = "UTF-8";
	
	public static final String KEY_RES = "result";
	public static final String KEY_ERCODE = "errcode";
	public static final String KEY_ERMSG = "errmsg";
	
	public static final String SUCC = "success";
	public static final String FAIL = "fail";
	public static final int LIMIT = 15;
	public static final String DEFAULT_LIMIT = "15";
	
	public static final String ERR_CD_PARAM = "11";
	public static final String ERR_MSG_PARAM = "parameter error";
	public static final String ERR_CD_PARSE = "12";
	public static final String ERR_MSG_PARSE = "parsing error";
	public static final String ERR_CD_NODATA = "13";
	public static final String ERR_MSG_NODATA = "no data exist";
	public static final String ERR_CD_DUPMAIL = "14";
	public static final String ERR_MSG_DUPMAIL = "email duplicated";
	public static final String ERR_CD_NODIR = "15";
	public static final String ERR_MSG_NODIR = "no directory exist";
	public static final String ERR_CD_MAKE = "16";
	public static final String ERR_MSG_MAKE = "cannot make directory";
	public static final String ERR_CD_PARAM2 = "17";
	public static final String ERR_MSG_PARAM2 = "parameter count is over";
	public static final String ERR_CD_DUPDATA = "18";
	public static final String ERR_MSG_DUPDATA = "data duplicated";
	public static final String ERR_CD_DECODE = "19";
	public static final String ERR_MSG_DECODE = "decoding error";
	
	public static final String ERR_CD_LOGIN = "21";
	public static final String ERR_MSG_LOGIN = "login fail";
	
	public static final String ERR_CD_DAO1 = "31";
	public static final String ERR_MSG_DAO1 = "persistent select error";
	
	public static final String ERR_CD_DAO2 = "32";
	public static final String ERR_MSG_DAO2 = "persistent update error";
	
	public static final String ERR_CD_DAO3 = "33";
	public static final String ERR_MSG_DAO3 = "persistent update no data exist";
	
	public static final String ERR_CD_DAO4 = "34";
	public static final String ERR_MSG_DAO4 = "cannot delete data";
	
	public static final String ERR_CD_SYS = "41";
	public static final String ERR_MSG_SYS1 = "mail sending service error";
	
	public static final String ERR_CD_FILE1 = "51";
	public static final String ERR_MSG_FILE1 = "file not exist error";
	public static final String ERR_CD_FILE2 = "52";
	public static final String ERR_MSG_FILE2 = "file copy error";
	public static final String ERR_CD_FILE3 = "53";
	public static final String ERR_MSG_FILE3 = "file zip error";
	
	public static final String ERR_CD_TASK1 = "61";
	public static final String ERR_MSG_TASK1 = "task information error";
	
	public static final String ERR_CD_UNKOWN = "99";
	public static final String ERR_MSG_UNKOWN = "unknown error";
	
	protected Map resultSuccess() {
		Map res = new HashMap();
		res.put(KEY_RES, SUCC);
		res.put(KEY_ERCODE, "");
		res.put(KEY_ERMSG, "");
		
		return res;
	}
	
	protected Map resultSuccess(String key, Object value) {
		Map res = new HashMap();
		res.put(KEY_RES, SUCC);
		res.put(key, value);
		res.put(KEY_ERCODE, "");
		res.put(KEY_ERMSG, "");
		
		return res;
	}
	
	protected void resultSuccess(Map res, Map param){
		resultSuccess(res);
		
		if(param.containsKey("pageno")) res.put("pageno", Integer.parseInt(param.get("pageno").toString()));
		if(param.containsKey("limit")) res.put("limit", Integer.parseInt(param.get("limit").toString()));
	}
	
	protected void resultSuccess(Map res) {
		res.put(KEY_RES, SUCC);
		res.put(KEY_ERCODE, "");
		res.put(KEY_ERMSG, "");
	}
	
	protected Map resultSuccess(List list) {
		Map res = new HashMap();
		res.put(KEY_RES, SUCC);
		res.put(KEY_ERCODE, "");
		res.put(KEY_ERMSG, "");
		
		res.put("list", list);
		
		return res;
	}
	
	protected Map resultSuccess(List list, Map param) {
		Map res = resultSuccess(list);
		if(param.containsKey("pageno")) res.put("pageno", Integer.parseInt(param.get("pageno").toString()));
		if(param.containsKey("limit")) res.put("limit", Integer.parseInt(param.get("limit").toString()));
		
		return res;
	}
	
	protected Map resultFail(String code, String msg) {
		Map res = new HashMap();
		res.put(KEY_RES, FAIL);
		res.put(KEY_ERCODE, code);
		res.put(KEY_ERMSG, msg);
		return res;
	}
	
	protected void resultFail(Map res, String code, String msg) {
		res.put(KEY_RES, FAIL);
		res.put(KEY_ERCODE, code);
		res.put(KEY_ERMSG, msg);
	}
	
	protected Map getOffsetLimit(String pageNo, String limit) {
		Map res = new HashMap();
		int pNo = 1;
		int lim = LIMIT;
		if(!pageNo.equals("") && !pageNo.equals("0")) {
			try {
				pNo = Integer.parseInt(pageNo);
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(pNo < 0) pNo = 1;
		}
		if(!limit.equals("") && !limit.equals("0")) {
			try {
				lim = Integer.parseInt(limit);
			} catch(Exception e) {
				e.printStackTrace();
			}
			if(lim < 0) lim = LIMIT;
		}
		
		res.put("pageno", pNo);
		res.put("offset", (pNo-1)*lim);
		res.put("limit", lim);
		
		return res;
	}
	
}
