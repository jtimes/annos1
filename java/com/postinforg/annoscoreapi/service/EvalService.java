package com.postinforg.annoscoreapi.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.aspectj.util.FileUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.postinforg.annoscoreapi.mapper.DatasetMapper;
import com.postinforg.annoscoreapi.mapper.EvalBig2Mapper;
import com.postinforg.annoscoreapi.mapper.EvalBigMapper;
import com.postinforg.annoscoreapi.mapper.EvalMapper;
import com.postinforg.annoscoreapi.mapper.ReportMapper;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
public class EvalService {
	
	@Resource EvalMapper evalMapper;
	@Resource EvalBigMapper evalBigMapper;
	@Resource DatasetMapper datasetMapper;
	@Resource ReportMapper reportMapper;
	@Resource EvalBig2Mapper evalBig2Mapper;
	
	Logger logger = LoggerFactory.getLogger(EvalService.class);

	public List evaluationofdatasetList(Map param) {
		List l = null;
		try {
			l = evalMapper.evaluationofdataset(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return l;
	}
	
	public int evaluationofdatasetCount(Map param) {
		List<EgovMap> l = null;
		try {
			l = evalMapper.evaluationofdatasetCount(param);
		}
		catch(Exception e) {
			
		}
		return (l == null) ? 0 : l.size();
	}
	
	public int evaluationofdataset(String datasetno) {
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		try {
			List<EgovMap> cntMap = evalMapper.evaluationofdatasetCount(param);
			
			if(cntMap == null || cntMap.size() != 3) return -1;
			
			List<EgovMap> l = evalMapper.evaluationofdataset(param);
			if(l == null || l.size() != 3) return -1;
			
			String cls = "";
			String obj = "";
			String fName = "";
			String fNameCls = "";
			File f = null;
			for(EgovMap map : l) {
				if(map.get("clsName").toString().equals("cls")) {
//					cls = map.get("data").toString();
					fName = map.get("data").toString();
					f = new File(fName);
					if(f.exists()) {
						cls = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
						fNameCls = fName;
					}
				}
				else if(map.get("clsName").toString().equals("obj")) {
//					obj = map.get("data").toString();
					fName = map.get("data").toString();
					f = new File(fName);
					if(f.exists()) {
						obj = FileUtils.readFileToString(f, StandardCharsets.UTF_8);
					}
				}
			}
			
			if(cls.equals("") || obj.equals("")) return -1;
			
			List<Map> clsList = convertClsJson(cls);
			List<Map> objList = convertObjJson(obj);
			
			if(clsList == null || objList == null) return -2;
			
			insertClsBulk(clsList, datasetno, fNameCls);
			insertObjBulk(objList, datasetno);
//			clsUpdateObjJson(datasetno);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 3;
	}
/*
	private void clsUpdateObjJson(String datasetno) {
		Gson gson = new Gson();
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		try {
			List<EgovMap> clsList = evalMapper.selectEvalClsList2(param);
			for(EgovMap map : clsList) {
				List<EgovMap> olist = evalMapper.selectEvalObjList2(map);
				map.put("objJson", gson.toJson(olist).toString());
				evalMapper.updateEvalCls(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/
	private boolean insertClsBulk(List<Map> clsList, String datasetno, String fName) {
		Gson gson = new Gson();
		List<Map> l = new ArrayList<Map>();
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		
		
		try {
//			logger.debug("> " + clsList.size());
			evalMapper.clearCls(param);
			
			for(int i=0; i<clsList.size(); i++) {
				Map map = clsList.get(i);
//				logger.debug("> " +i+ " : " + map);
				String newName = fName + ".class" + i + ".json";
				File file = new File(newName);
				if(file.exists() && file.isFile()) {
					file.delete();
				}
				FileUtil.writeAsString(file, gson.toJsonTree(map).getAsJsonObject().toString());
				map.put("jsons", newName);

				map.put("datasetno", datasetno);
				evalMapper.insertClsOne(map);
			}

		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private boolean insertObjBulk(List<Map> objList, String datasetno) {
		List<Map> l = new ArrayList<Map>();
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		
		try {
			evalMapper.clearObj(param);
			logger.debug("> " + objList.size());
			for(int i=0; i<objList.size(); i++) {
				Map map = objList.get(i);
//				logger.debug("> " +i+ " : " + map);
				
				if(i%1000 == 999) {
					param.put("list", l);
					evalMapper.insertObjBulk(param);
					logger.debug("> insertObjBulk : " +i);
					l.clear();
				}
				l.add(map);
			}
			
			if(l.size() > 0) {
				param.put("list", l);
				evalMapper.insertObjBulk(param);
				logger.debug("> fin insertObjBulk ");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	private List<Map> convertClsJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		List<Map> res = null;
		if(json != null) {
			try {
				Map newJson = mapper.readValue(json, Map.class);
				if(newJson.containsKey("results")) {
					Map r = (Map)newJson.get("results");
					if(r.containsKey("classes")) {
						List<Map> classList = (List<Map>)r.get("classes");
						if(classList != null && classList.size() > 0) {
							res = classList;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return res;
	}

	private List<Map> convertObjJson(String json) {
		ObjectMapper mapper = new ObjectMapper();
		List<Map> res = null;
		if(json != null) {
			try {
				Map newJson = mapper.readValue(json, Map.class);
				if(newJson.containsKey("results")) {
					List<Map> objList = (List<Map>)newJson.get("results");
					if(objList != null && objList.size() > 0) {
						res = objList;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return res;
	}

	public Map getEvalClsList(Map param) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		int total = 0;
		int checked = 0;
		int openTotal = 0;
		try {
			total = evalBigMapper.selectEvalClsListTotal(param);
			checked = evalBigMapper.selectEvalClsCheckedTotal(param);
			l = evalBigMapper.selectEvalClsList(param);
			
			ObjectMapper mapper = new ObjectMapper();
			jsonAdd(l, mapper, param.get("apl"));
			
			openTotal = evalBigMapper.selectAnalOpenTotal(param);
			EgovMap em = datasetMapper.selectDataset(param);
			if(em != null && em.containsKey("name")) {
				res.put("datasetname", em.get("name"));
			}
			
//			List l2 = evalMapper.selectEvalClsObjCountList(param);
//			List l2 = evalBigMapper.selectEvalClsObjCountList(param);
//			res.put("objList", l2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("checkedCount", checked);
		res.put("list", l);
		res.put("opencount", openTotal);
		return res;
	}

	private void jsonAdd(List<EgovMap> l, ObjectMapper mapper, Object aplParam)
			throws Exception {
		if(aplParam == null) {
			for(EgovMap eval : l) {
				eval.remove("jsons");
			}
		}
		else {
			String[] apl = (String[])aplParam;
			for(EgovMap eval : l) {
				Object obj = eval.get("jsons");
				if(obj != null) {
					File f = new File(obj.toString());
					if(f.exists() && f.isFile()) {
						String jsonsSrc = FileUtil.readAsString(f);
						Map data = mapper.readValue(jsonsSrc, Map.class);
						Map newData = new HashMap();
						for(int i=0; i<apl.length; i++) {
							Iterator iter = data.keySet().iterator();
							while(iter.hasNext()) {
								String s = iter.next().toString();
								if(s.indexOf(apl[i]) > 0) {
									newData.put(s, data.get(s));
								}
							}
						}
						eval.remove("jsons");
						eval.put("jsons", newData);
					}
				}
			}
		}
	}

	/*
	public Map getEvalObjList(Map param) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		List<EgovMap> l2 = null;
		int total = 0;
		int checked = 0;
		int total2 = 0;
		Map param2 = new HashMap();
		param2.put("datasetno", param.get("datasetno"));
		try {
			total = evalBigMapper.selectEvalObjListTotal(param);
			checked = evalBigMapper.selectEvalObjCheckedTotal(param);
			l = evalBigMapper.selectEvalObjList(param);
			EgovMap em = datasetMapper.selectDataset(param);
			if(em != null && em.containsKey("name")) {
				res.put("datasetname", em.get("name"));
			}
			
//			total2 = evalBigMapper.selectEvalObjListTotal2(param2);
//			l2 = evalBigMapper.selectEvalObjShapes(param2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("checkedCount", checked);
		res.put("list", l);
//		res.put("basetotalcount", total2);
//		res.put("shapeTypes", l2);
		return res;
	}
	*/
	
	public Map getEvalObjSimpleList(Map param, boolean ispred) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		List<EgovMap> l2 = null;
		int total = 0;
		int checked = 0;
		int total2 = 0;
		Map param2 = new HashMap();
		param2.put("datasetno", param.get("datasetno"));
		try {
//			if(ispred) {
//				total = evalBig2Mapper.selectEvalObjSimplePredListTotal(param);
//				checked = evalBig2Mapper.selectEvalObjSimpleCheckedPredTotal(param);
//				l = evalBig2Mapper.selectEvalObjSimplePredList(param);
//			} else {
//				total = evalBig2Mapper.selectEvalObjSimpleListTotal(param);
//				checked = evalBig2Mapper.selectEvalObjSimpleCheckedTotal(param);
//				l = evalBig2Mapper.selectEvalObjSimpleList(param);
//			}
			
			total = evalBig2Mapper.selectEvalObjSimplePredListTotal(param);
			checked = evalBig2Mapper.selectEvalObjSimpleCheckedPredTotal(param);
			l = evalBig2Mapper.selectEvalObjSimplePredList(param);
			
			EgovMap em = datasetMapper.selectDataset(param);
			if(em != null && em.containsKey("name")) {
				res.put("datasetname", em.get("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("checkedCount", checked);
		res.put("list", l);
		return res;
	}

	public int evaluationOpenSet(String[] ecnos, String isOpen) {
		Map param = new HashMap();
		param.put("ecnos", ecnos);
		param.put("isopen", isOpen);
		
		int result = 0;
		try {
//			result = evalMapper.evaluationOpenSet(param);
			result = evalBigMapper.evaluationOpenSet(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int saveEvaluation(Map param) {
		int result = 0;
		try {
			result = evalMapper.saveEvaluation(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int evaluationDel(Map param) {
		int result = 0;
		try {
			result = evalMapper.evaluationDel(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int evaluationOpenSetAll(Map param) {
		int result = 0;
		try {
//			result = evalMapper.evaluationOpenSetAll(param);
			result = evalBigMapper.evaluationOpenSetAll(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public EgovMap getEvalClsAvg(Map param) {
		EgovMap result = new EgovMap();
		try {
//			result = evalMapper.getEvalClsAvg(param);
			result = evalBigMapper.getEvalClsAvg(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Map getEvalFileCheckedCount(Map param) {
		Map result = new HashMap();
		try {
			int checked = evalBigMapper.selectEvalFileCheckedTotal(param);
			result.put("checkedCount", checked);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int saveEvalClassInfo(Map param) {
		int result = 0;
		try {
			result = evalMapper.saveEvalClassInfo(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Map getEvalFileList(Map param) {
		Map res = new HashMap();
		int total = 0;
		int checked = 0;
		List l = null;
		try {
			total = evalBigMapper.selectEvalFileListTotal(param);
			checked = evalBigMapper.selectEvalFileCheckedTotal(param);
			l = evalBigMapper.selectEvalFileList(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("checkedCount", checked);
		res.put("list", l);
		
		return res;
	}

	public int evaluationClsClear(Map param) {
		int res = 0;
		try {
			EgovMap map = datasetMapper.selectDatasetDetail(param);
			if(map != null && map.get("settype").toString().equals("1"))
				return -1;
			
			res = evalBigMapper.evaluationClsReset(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}

}
