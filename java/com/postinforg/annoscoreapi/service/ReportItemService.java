package com.postinforg.annoscoreapi.service;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.postinforg.annoscoreapi.controller.CommonController;
import com.postinforg.annoscoreapi.mapper.EvalBigMapper;
import com.postinforg.annoscoreapi.mapper.EvalMapper;
import com.postinforg.annoscoreapi.mapper.ReportItemMapper;
import com.postinforg.annoscoreapi.mapper.ReportMapper;
import com.postinforg.annoscoreapi.util.AssignmentUtil;
import com.postinforg.annoscoreapi.util.StrNumUtil;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
public class ReportItemService {
	
	@Resource ReportItemMapper reportItemMapper;
	@Resource ReportMapper reportMapper;
	@Resource EvalMapper evalMapper;
	@Resource EvalBigMapper evalBigMapper;

	@Value("${media.assignmenthome}") private String assignHome;
	@Autowired AssignmentUtil assignUtil;
	
	Logger logger = LoggerFactory.getLogger(ReportItemService.class);

	public Map reportItemList(Map param) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		try {
			l = reportItemMapper.selectReportItemList(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("list", l);
		return res;
	}

	public Map reportReportItemList(Map param) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		try {
			l = reportItemMapper.selectReportReportItemList(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("list", l);
		return res;
	}

	public int reportItemAdd(Map param) {
		int result = 0;
		try {
			result = reportItemMapper.reportItemAdd(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int reportItemUpdate(Map param, MultipartFile multipartFile) {
		int result = 0;
		try {
			EgovMap ri = reportItemMapper.selectReportReportItem(param);
			String datatype = ri.get("datatype").toString();
			if(ri.containsKey("assignmentno")) {
				param.put("assignmentno", ri.get("assignmentno"));
				if(datatype.equals("file")) {
					saveFile(param, multipartFile);
				}
				if(ri == null || ri.get("no") == null || ri.get("no").toString().equals("")) {
					result = reportItemMapper.reportItemAdd(param);
				}
				else {
					result = reportItemMapper.reportItemUpdate(param);
				}
			}
			else {
				logger.error("<<< report item select error : " + ri);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void saveFile(Map param, MultipartFile multipartFile) {
		String orgname = multipartFile.getOriginalFilename();
		logger.debug("> orgname : " + orgname);
		SecureRandom ran = new SecureRandom();
		String[] arr = orgname.split("[.]");
		String ext = arr[arr.length-1].toLowerCase();
		
		String newFileName = assignHome + assignUtil.getAssignmentDirFromNo(param.get("assignmentno").toString())
			+ "/attachfile";
		logger.debug(">>> new file name : " + newFileName);
		File savefile = new File(newFileName);
		if(!savefile.exists() || savefile.isFile()) savefile.mkdirs();
		
		newFileName = newFileName
				+ "/r" + param.get("reportno").toString() + "_i" + param.get("reportitemno").toString() + "_" 
				+ StrNumUtil.getNow("MMdd") + String.format("%03d", ran.nextInt(999))
				+ "." + ext;
		savefile = new File(newFileName);
		try {
			multipartFile.transferTo(savefile);
			param.put("savepath", newFileName);
			param.put("value", orgname);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map reportBaseInfo(Map param) {
		EgovMap result = new EgovMap();
		ObjectMapper mapper = new ObjectMapper();
		try {
			result = reportMapper.selectReportDetail(param);
			
			String aptype = "";
			if(result.get("evalAptype") != null) aptype = result.get("evalAptype").toString();
			String css = "";
			if(result.get("evalClasses") != null) css = result.get("evalClasses").toString();
			String[] classes = css.split(",");

			if(result.isEmpty() || aptype.equals("") || css.equals("")) {
				result.put(CommonController.KEY_RES, CommonController.FAIL);
				result.put(CommonController.KEY_ERCODE, CommonController.ERR_CD_NODATA);
				result.put(CommonController.KEY_ERMSG, CommonController.ERR_MSG_NODATA);
				return result;
			}
			param.put("aptype", aptype);
			param.put("classes", classes);
			
			result.clear();
			
			Map condition = new HashMap();
			condition.put("aptype", aptype);
			condition.put("classes", css);
			result.put("condition", condition);
			
			List classList = new ArrayList();
			double meanAp = 0.0;
			double miou = 0.0;
			
			List<EgovMap> clsList = reportItemMapper.evalClsList(param);
			if(clsList != null && clsList.size() > 0) logger.debug(">>> clsList : " + clsList.get(0));
			List<EgovMap> objList1 = reportItemMapper.evalObjList1(param);
			
			for(String clazz : classes) {
				for(EgovMap map : clsList) {
					Object obj = map.get("name");
//					logger.debug(">>> clslist name : " + obj);
					if(obj != null && obj.toString().equals(clazz)) {
						if(!aptype.equals("")) {
							try {
								Object dVal = map.get("ap" + aptype);
								if(dVal != null)
									meanAp += Double.parseDouble(dVal.toString());
								else meanAp += 0;
							
								Map cdata = extractApValue(aptype, map);
								cdata.put("cls", clazz);
								
								for(EgovMap o1 : objList1) {
									if(o1.get("cls").toString().equals(clazz)) {
										cdata.put("iou", o1.get("iou"));
										cdata.put("objCount", o1.get("objCount"));
										dVal = o1.get("iou");
										if(dVal != null)
											miou += Double.parseDouble(dVal.toString());
										else miou += 0;
									}
								}
								
								classList.add(cdata);
							}
							catch(Exception ex) {
								ex.printStackTrace();
							}
						}
							
							/*
							obj = map.get("jsons");
							if(obj != null) {
								File f = new File(obj.toString());
								if(f.exists() && f.isFile()) {
									String jsonsSrc = FileUtil.readAsString(f);
									Map data = mapper.readValue(jsonsSrc, Map.class);
									Map cdata = extractApValue(aptype, data);
									cdata.put("cls", clazz);
									try {
										meanAp += Double.parseDouble(cdata.get("AP_"+aptype).toString());
									}
									catch(Exception ex) {
										ex.printStackTrace();
									}
									
									for(EgovMap o1 : objList1) {
										if(o1.get("cls").toString().equals(clazz)) {
											cdata.put("iou", o1.get("iou"));
											cdata.put("objCount", o1.get("objCount"));
											try {
												miou += Double.parseDouble(o1.get("iou").toString());
											}
											catch(Exception ex) {
												ex.printStackTrace();
											}
										}
									}
									
									classList.add(cdata);
								}
							}
							*/
							
					}
				}
			}
			if(classList.size() > 0) {
				meanAp = meanAp / classList.size();
				miou = miou / classList.size();
				result.put("mAp", meanAp);
				result.put("mIou", miou);
			}
			result.put("classes", classList);
			
//			result.put("objSegmentation", objSegmentation(reportItemMapper.evalObjList2(param)));
			
			/* 
			Map fileJsonMap = null;
			param.put("clsName", "file");
			List<EgovMap> evalSource = evalMapper.evaluationofdataset(param);
			if(!evalSource.isEmpty()) {
				EgovMap fileSource = evalSource.get(0);
				String path = fileSource.get("data").toString();
				File fileJson = new File(path);
				if(fileJson.exists() && fileJson.isFile()) {
					logger.debug(">>> file found : " + fileJson);
					String str = FileUtils.readFileToString(fileJson, StandardCharsets.UTF_8);
					fileJsonMap = mapper.readValue(str, Map.class);
				}
			}
			result.put("objCountList", imgObjCountList(fileJsonMap));
			*/
			result.put("objCountList", reportItemMapper.evalFileObjCountList(param));
			
			EgovMap summary = evalBigMapper.evalSummary(param);
			result.put("summary", summary);
			
			result.put(CommonController.KEY_RES, CommonController.SUCC);
		} 
		catch (Exception e) {
			e.printStackTrace();
			result.put(CommonController.KEY_RES, CommonController.FAIL);
			result.put(CommonController.KEY_ERCODE, CommonController.ERR_CD_PARSE);
			result.put(CommonController.KEY_ERMSG, CommonController.ERR_MSG_PARSE);
		}
		return result;
	}

	/* 4.클래스, 오브젝트 크기  조회 */
	private List objSegmentation(List<EgovMap> l) {
		List result = new ArrayList();
		String clname = "";
		List temp = new ArrayList();
		for(EgovMap map : l) {
			if(map.get("cls").toString().equals(clname)) {
				temp.add(map);
			}
			else {
				if(!clname.equals("") && !temp.isEmpty()) {
					Map resultMap = new HashMap();
					resultMap.put("class", clname);
					resultMap.put("obj", temp);
					result.add(resultMap);
					
//					logger.debug(">>> obj seg : " + clname + ", tempsize : " + temp.size());
					temp = new ArrayList();
				}
				clname = map.get("cls").toString();
				temp.add(map);
			}
		}
		if(!temp.isEmpty()) {
			Map resultMap = new HashMap();
			resultMap.put("class", clname);
			resultMap.put("obj", temp);
//			logger.debug(">>> obj seg : " + clname + ", tempsize : " + temp.size());
			result.add(resultMap);
		}
		
//		logger.debug(">>> objSegmentation : " + result);
		return result;
	}

	/* 5.이미지수, 오브젝트 수 조회 */
	private List imgObjCountList(Map fileJsonMap) {
		List result = new ArrayList();
		if(fileJsonMap != null) {
			if(fileJsonMap.containsKey("results")) {
				List<Map> list = (List<Map>)fileJsonMap.get("results");
				String filename = "";
				for(int i=0; i<list.size(); i++) {
					Map m1 = list.get(i);
					if(m1.containsKey("summary")) {
						Map summary = (Map)m1.get("summary");
						if(summary.containsKey("filename")) {
							filename = summary.get("filename").toString();
						}
					}
					if(!filename.equals("") && m1.containsKey("objects")) {
						List<Map> objs = (List<Map>)m1.get("objects");
						int objCount = 0;
						for(Map ob : objs) {
//							if(ob.get("pred_flag").toString().equals("0") &&
//									ob.get("class").toString().equals(clazz)) {
							if(ob.get("pred_flag").toString().equals("1")) {
								objCount ++;
							}
						}
						Map objCountResult = new HashMap();
						objCountResult.put("file_no", i);
						objCountResult.put("object_cnt", objCount);
						result.add(objCountResult);
					}
				}
			}
		}
		return result;
	}
	
	private Map extractApValue(String aptype, Map data) {
		Map result = new HashMap();
		Iterator iter = data.keySet().iterator();
		while(iter.hasNext()) {
			String keyName = (String)iter.next();
			if(keyName.endsWith(aptype)) {
				result.put(keyName.substring(0, keyName.length()-2), data.get(keyName));
			}
		}
		return result;
	}

}
