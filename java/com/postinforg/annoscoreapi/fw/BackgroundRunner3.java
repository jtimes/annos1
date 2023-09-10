package com.postinforg.annoscoreapi.fw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.postinforg.annoscoreapi.mapper.AssignmentBgMapper;
import com.postinforg.annoscoreapi.mapper.AssignmentMapper;
import com.postinforg.annoscoreapi.mapper.DatasetMapper;
import com.postinforg.annoscoreapi.mapper.EvalBig2Mapper;
import com.postinforg.annoscoreapi.mapper.EvalBigMapper;
import com.postinforg.annoscoreapi.util.AssignmentUtil;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Component
public class BackgroundRunner3 {

	Logger logger = LoggerFactory.getLogger(BackgroundRunner3.class);
	
	@Autowired AssignmentMapper assignmentMapper;
	@Autowired AssignmentBgMapper assignmentBgMapper;
	@Autowired AssignmentUtil assignmentUtil;
	@Autowired EvalBigMapper evalBigMapper;
	@Autowired EvalBig2Mapper evalBig2Mapper;
	@Autowired DatasetMapper datasetMapper;
	
	@Value("${media.assignmenthome}") private String assignmentHome;
	@Value("${spring.profiles.active}") private String activeProfiles;
	@Value("${bg.runtype}") private String runtype;
	
	@Scheduled(fixedRate = 1000 * 15)
	public void scheduler31() throws InterruptedException {
		if(activeProfiles.endsWith(BackgroundRunner.BG_MODE_PROFILE)) {
			if(runtype.equals("all") || runtype.equals("4")) {
				logger.debug(">>> call evalParse(");
				evalParse();
			}
		}
	}

	@Scheduled(fixedRate = 1000 * 60 * 3)
	public void scheduler32() throws InterruptedException {
		if(activeProfiles.endsWith(BackgroundRunner.BG_MODE_PROFILE)) {
			if(runtype.equals("all") || runtype.equals("5")) {
				logger.debug(">>> call assignmentDiskUsageUpdate()");
				assignmentDiskUsageUpdate();
			}
		}
	}
	
	private void assignmentDiskUsageUpdate() {
		try {
			Map param = new HashMap();
			List<EgovMap> l = assignmentBgMapper.assignmentDiskUsageCheck(param);
			if(l != null && l.size() > 0) {
				EgovMap assignment = l.get(0);
				String assignmentno = assignment.get("no").toString();
				String loc = assignmentHome + assignmentUtil.getAssignmentDirFromNo(assignmentno);
				
				String usage = assignmentUtil.dirUsage2(loc);
				param.put("assignmentno", assignmentno);
				param.put("usage", usage);
				assignmentMapper.updateAssignmentUsageDate(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean evalParse() {
		boolean result = false;
		if(evalDataParse(1)) result = true;	// class
		if(evalDataParse(2)) result = true;	// object
		if(evalDataParse(3)) result = true;	// file
		
		return result;
	}

	private boolean evalDataParse(int type) {
		try {
			Map param = new HashMap();
			param.put("status", "0");
			if(type == 1)
				param.put("type", "class");
			else if(type == 2)
				param.put("type", "object");
			else 
				param.put("type", "file");
			
			List<EgovMap> l = evalBigMapper.evaluationBigStatus(param);
			
			if(l != null && !l.isEmpty()) {
				logger.debug(">>>>>>>>>>> start parser : " + type);
				EgovMap evalBig = l.get(0);
				
				evalBig.put("status", "1");	/* 0:최초, 1:인식, 2:작업중, 5:FIN, 8:파일에러, 9:기타에러 */
				int res = evalBigMapper.updateEvalBigStatus(evalBig);
					
				if(res > 0) {
					String path = evalBig.get("path").toString();
					String files = evalBig.get("files").toString();
					String[] arr = files.split(",");
					
					List<File> fileList = getFileList(path, files, arr);
					
					if(fileList.isEmpty()) {
						evalBig.put("status", "8");
						evalBigMapper.updateEvalBigStatus(evalBig);
						return false;
					}
					
					EgovMap ass = assignmentMapper.getAssignmentDetail(evalBig);
					String datasetno = evalBig.get("datasetno").toString();
					String odatasetno = ass.get("datasetno").toString();
					Map statusMap = new HashMap();
					statusMap.put("datasetno", datasetno);
					statusMap.put("status", "9");
					
					// 파일 파싱
					if(type == 1) {	// class
						res = classDataUpdate(fileList
								, evalBig.get("assignmentno").toString()
								, evalBig.get("datasetno").toString(), arr);
						
						if(!datasetno.equals(odatasetno)) {
							datasetMapper.datasetEval(statusMap);
						}
					}
					else if(type == 2) {	// object
						res = objectDataUpdate(fileList
								, evalBig.get("assignmentno").toString()
								, evalBig.get("datasetno").toString()
								, arr);
						
						if(res > 0) {	
							// 오브젝트 후처리. obj ref 등록
//							evalBig.put("predFlag", "0");
//							evalBig2Mapper.evalObjRefLink(evalBig);
//							evalBig.put("predFlag", "1");
//							evalBig2Mapper.evalObjRefLink(evalBig);
							
							// 오브젝트 후처리. 파일별 분석 결과 입력
							evalBigMapper.deleteEvalBigFileRef(evalBig);
							evalBigMapper.deleteEvalBigFile(evalBig);
							evalBigMapper.insertEvalBigFile(evalBig);
							evalBigMapper.updateEvalClsObjCount(evalBig);
//							evalBigMapper.copyEvalFileRef(evalBig);
							evalBig.put("odatasetno", datasetno);
							evalBigMapper.copyEvalFileRef(evalBig);
							
							evalBig.put("evalresdate", "true");
							assignmentMapper.evalReqUpdate(evalBig);
							
							datasetMapper.datasetEval(statusMap);
							
						}
					}
					else {
						// file 타입 없어졌음
						// res = objectFileUpdate(fileList, evalBig.get("datasetno").toString(), arr);
					}
					
					if(res > 0) {
						evalBig.put("status", "5");
					}
					else {
						evalBig.put("status", "8");
					}
					evalBigMapper.updateEvalBigStatus(evalBig);
				}
				
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	private List<File> getFileList(String path, String files, String[] arr) {
		List<File> fileList = new ArrayList<File>();
		if(!path.equals("") && !files.equals("")) {
			if(!path.endsWith("/")) path = path + "/";
			for(String a : arr) {
				File f = new File(path + a);
				if(!f.exists() || f.isDirectory()) {
					fileList.clear();
					logger.debug(">>> file is not exist " + path + a);
					break;
				}
				fileList.add(f);
			}
		}
		return fileList;
	}

	private int objectDataUpdate(List<File> fileList
			, String assignmentno, String datasetno, String[] arr) {
		int res = 0;
		
		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
		param.put("datasetno", datasetno);
		try {
			if(fileList.size() > 0) {
				// exist object data clear
				param.put("isreset", "1");
				
				param.put("predFlag", 0);
				evalBigMapper.evaluationObjDsRefClear(param);
				param.put("predFlag", 1);
				evalBigMapper.evaluationObjDsRefClear(param);
				evalBigMapper.evaluationObjDsClear(param);
				
				for(int i=0; i<fileList.size(); i++) {
					File f = fileList.get(i);
					String filename = f.getCanonicalPath();
					logger.debug(">>> file name : " + filename);
//					String ap = filename.substring(filename.length()-7, filename.length()-5);
					
					BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
					Gson gson = new Gson();
					List list = gson.fromJson(bufferedReader, List.class);
					logger.debug(">>> " + arr[i] + " : " + list.size());
					
					if(list != null && list.size() > 0) {
						List l = new ArrayList();
						for(int j=0; j<list.size(); j++) {
							if(j%2000 == 1999) {
								param.remove("list");
								param.put("list", l);
								evalBigMapper.evaluationObjInsertBulk(param);
								res++;
								logger.debug("> insertObjBulk : " +j);
								l.clear();
							}
							l.add(list.get(j));
						}
						
						if(l.size() > 0) {
							param.put("list", l);
							evalBigMapper.evaluationObjInsertBulk(param);
							res++;
							logger.debug(">>>>> fin insertObjBulk ");
							
						}
						
						param.put("predFlag", 0);
						evalBig2Mapper.evalObjRefInit(param);
						param.put("predFlag", 1);
						evalBig2Mapper.evalObjRefInit(param);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private int classDataUpdate(List<File> fileList
			, String assignmentno, String datasetno, String[] arr) {
		int res = 0;
		
		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
		param.put("datasetno", datasetno);
		try {
			evalBigMapper.evaluationClsDsClear(param);
			
			for(int i=0; i<fileList.size(); i++) {
				File f = fileList.get(i);
				String filename = f.getCanonicalPath();
				logger.debug(">>> file name : " + filename);
				String ap = filename.substring(filename.length()-7, filename.length()-5);
				
				BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
				Gson gson = new Gson();
				Map json = gson.fromJson(bufferedReader, Map.class);
				
				if(json.containsKey("recall_precision_curve")) {
					json.remove("recall_precision_curve");
					json.put("recallPrecisionCurve", filename);
				}
				
				if(json.containsKey("interpolated_recall_precision_curve")) {
					json.remove("interpolated_recall_precision_curve");
				}
				
				// not exist then insert null
				// classFileName = arr[i].split("_")[0];
				String classFileName = arr[i];
				String[] aNames = classFileName.split("_");
				if(aNames.length > 0) {
					int nIdx = classFileName.indexOf(aNames[aNames.length-1]);
					classFileName = classFileName.substring(0, nIdx-1);
				}
				
				param.put("name", classFileName);
				List l = evalBigMapper.evaluationClsMin(param);
				if(l == null || l.isEmpty()) {
					logger.debug(">>> insert");
					evalBigMapper.evaluationClsInsert(param);
				}
				
				json.put("type", ap);
				json.put("name", classFileName);
				json.put("datasetno", datasetno);
				
				logger.debug(">>> evaluationClsUpdate : " + json);
				res = evalBigMapper.evaluationClsUpdate(json);
				
			}
			
			EgovMap ass = assignmentMapper.getAssignmentDetail(param);
			String odatasetno = ass.get("datasetno").toString();
			if(!odatasetno.equals(datasetno)) {
				evalBigMapper.updateEvalClsObjCount2(param);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	/*
	private int objectFileUpdate(List<File> fileList, String datasetno, String[] arr) {
		int res = 0;
		
		Map param = new HashMap();
		param.put("datasetno", datasetno);
		if(fileList.size() > 0) {
			try {
				// exist object data clear
				evalBigMapper.evaluationFileClear(param);
				
				for(int i=0; i<fileList.size(); i++) {
					File f = fileList.get(i);
					String filename = f.getCanonicalPath();
					logger.debug(">>> file name : " + filename);
					String ap = filename.substring(filename.length()-7, filename.length()-5);
					
					BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
					Gson gson = new Gson();
					List<Map> list = gson.fromJson(bufferedReader, List.class);
					logger.debug(">>> " + arr[i] + " : " + list.size());
					
					if(list != null && list.size() > 0) {
						List fileTableInsertList = new ArrayList();
						for(int j=0; j<list.size(); j++) {
							Map fBody = list.get(j);
							String fname = "";
							Object obj = fBody.get("summary");
							if(obj != null) {
								Object fn = ((Map)obj).get("filename");
								if(fn != null) {
									fname = fn.toString();
									obj = fBody.get("objects");
									int objCount = 0;
									if(obj != null) {
										List<Map> oList = (List<Map>)obj;
										logger.debug(">>> " + fname + " : " + oList.size());
										for(int k=0; k<oList.size(); k++) {
											Map ob = oList.get(k);
											if(ob.get("pred_flag").toString().startsWith("1")) {
												objCount ++;
											}
										}
										Map objCountResult = new HashMap();
										objCountResult.put("fileName", fname);
										objCountResult.put("objectCnt", objCount);
										fileTableInsertList.add(objCountResult);
									}
								}
							}
						}
						
						if(fileTableInsertList.size() > 0) {
							param.put("list", fileTableInsertList);
							res += evalBigMapper.evaluationFileInsertBulk(param);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}
*/

}
