package com.postinforg.annoscoreapi.fw;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.postinforg.annoscoreapi.mapper.AssignmentBgMapper;
import com.postinforg.annoscoreapi.mapper.AssignmentMapper;
import com.postinforg.annoscoreapi.mapper.DatasetMapper;
import com.postinforg.annoscoreapi.model.ClsInfo;
import com.postinforg.annoscoreapi.model.UploadzipFile;
import com.postinforg.annoscoreapi.util.AssignmentUtil;
import com.postinforg.annoscoreapi.util.FileImageUtil;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Component
public class BackgroundRunner {

	Logger logger = LoggerFactory.getLogger(BackgroundRunner.class);
	
	@Autowired AssignmentMapper assignmentMapper;
	@Autowired AssignmentBgMapper assignmentBgMapper;
	@Autowired AssignmentUtil assignmentUtil;
	@Autowired DatasetMapper datasetMapper;
	
	@Value("${media.assignmenthome}") private String assignmentHome;
	@Value("${spring.profiles.active}") private String activeProfiles;
	@Value("${bg.runtype}") private String runtype;
	
	
	public static final String BG_MODE_PROFILE = "bg";
	public static final boolean SEARCH_ONLY = false;

	@Scheduled(fixedRate = 1000 * 30)
	public void scheduler() throws InterruptedException {
		if(activeProfiles.endsWith(BG_MODE_PROFILE)) {
			if(runtype.equals("all") || runtype.equals("1")) {
				logger.debug(">>> call assignmentParse()");
				assignmentParse();
			}
		}
	}

	private EgovMap checkNewIncoming() {
		try {
			List<EgovMap> alist = assignmentBgMapper.assignmentAliveList(null);
			List<EgovMap> ilist = assignmentBgMapper.assignmentIncomingList(null);
			
			int ilistIndex = 0;
			boolean ilistIndexFound = false;
			for(EgovMap asm : alist) {
				String asmno = asm.get("no").toString();
				String asmpath = assignmentHome + assignmentUtil.getAssignmentDirFromNo(asmno)
					+ AssignmentUtil.DIR_ASSIGNMENT_INC;
				File asmDir = new File(asmpath);
				ilistIndexFound = false;
				
				if(asmDir.exists() && asmDir.isDirectory()) {
					List<File> zipList = new ArrayList<File>();
					searchZipList(asmDir, zipList);
					logger.debug(">>> found zip files : " + zipList);
					
					for(File zip : zipList) {
						String zipPathName = zip.getCanonicalPath();
						logger.debug(">>> zipPathName : " + zipPathName);
						zipPathName = zipPathName.replaceAll("\\\\", "/").replaceAll(" ", "");
						if(zipPathName.indexOf(":") > 0) {
							zipPathName = zipPathName.substring(zipPathName.indexOf(":") + 1);
						}
						
						boolean incomingFound = false;
						for(int i=ilistIndex; i<ilist.size(); i++) {
							EgovMap incoming = ilist.get(i);
							String iasmno = incoming.get("assignmentno").toString();

							if(asmno.equals(iasmno)) {
								if(!ilistIndexFound) {
									ilistIndex = i;
									ilistIndexFound = true;
								}
								
								if(incoming.get("zippath").equals(zipPathName)) {
									incomingFound = true;
								}
							}
							
							if(asmno.compareTo(iasmno) < 0) break;
						}
						
						if(!incomingFound) {
							String filename = zip.getName().toLowerCase();
							filename = filename.substring(0, filename.length()-4).replaceAll(" ", "");
							
							String extractDir = assignmentHome + assignmentUtil.getAssignmentDirFromNo(asmno)
							+ AssignmentUtil.DIR_ASSIGNMENT_EXT;
							
							String frompath = extractDir + filename;
							
							String topath = assignmentHome + assignmentUtil.getAssignmentDirFromNo(asmno)
//								+ AssignmentUtil.DIR_ASSIGNMENT_STO + filename;
								+ AssignmentUtil.DIR_ASSIGNMENT_STO;

							checkMkDir(frompath);
							checkMkDir(topath);
							
							EgovMap incoming = new EgovMap();
							incoming.put("assignmentno", asmno);
							incoming.put("zippath", zipPathName);
							incoming.put("fromLocation", frompath);
							incoming.put("extractDir", extractDir);
							incoming.put("toLocation", topath);
							incoming.put("filename", filename);
							
							assignmentBgMapper.assignmentIncomingAdd(incoming);
							return incoming;
						}
					}
				}
				// else logger.error("<<< not exist " + asmpath);
			}
		}
		catch(Exception e) { e.printStackTrace(); }
		
		return null;
	}
	
	private void searchZipList(File asmDir, List<File> zipList) {
		File[] flist = asmDir.listFiles();
		if(flist != null && flist.length > 0) {
			for(int i=0; i<flist.length; i++) {
				if(flist[i].isDirectory()) {
					searchZipList(flist[i], zipList);
				}
				else {
					if(flist[i].getName().toLowerCase().endsWith("zip")) {
						zipList.add(flist[i]);
					}
				}
			}
		}
	}

	/* 과제 기초 데이터 적재 v1.2 마지막 요구사항 */
	private boolean assignmentParse() {
		EgovMap incoming = checkNewIncoming();
		if(incoming == null) return false;
		
		try {
			// int res = updateIncomingStatus(incoming, "1", "N");	/* 1: 압축 해제중 */
			logger.debug(">>> new incoming : " + incoming);
			
			String zipDir = incoming.get("zippath").toString();
//			String fromDir = incoming.get("fromLocation").toString();
			String extractDir = incoming.get("extractDir").toString();
//			String toDir = incoming.get("toLocation").toString();
			String filename = incoming.get("filename").toString();
			boolean unzipResult = FileImageUtil.runUnzipCommand(activeProfiles, zipDir, extractDir, filename);
			
			if(unzipResult) {
				updateIncomingStatus(incoming, "3");	/* 3: unzip 완료 - 라벨링 정리 */
				
				/* 핵심 함수 */
				if(rebuildAnnotations(incoming)) {
					updateIncomingStatus(incoming, "5");	/* 5: 완료 */
				}
				else {
					updateIncomingStatus(incoming, "9");	/* 9: 기타 에러 */
				}
				return true;
			}
			else {
				updateIncomingStatus(incoming, "7");	/* 7: unzip 에러 */
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	private boolean rebuildAnnotations(EgovMap bgtask) {
		String uploadpath = bgtask.get("fromLocation").toString();
		String storedLocation = bgtask.get("toLocation").toString();
		String filename = bgtask.get("filename").toString();
		
		List<UploadzipFile> list = new ArrayList<UploadzipFile>();
		Map imgMap = new HashMap<String, UploadzipFile>();
		Map jsonMap = new HashMap<String, UploadzipFile>();
		
		File dir = new File(uploadpath);
		// logger.debug(">>> uploadpath check : " + uploadpath);
		if(dir.isDirectory()) {
//			String saveRootLocation = assignmentUtil.getAssignmentBgTempLocation(uploadpath);
//			String saveRootLocation = "";
			BgCounter bgCounter = new BgCounter();
			
			/* 핵심 함수 */
			labelingMerge(dir, list, imgMap, jsonMap, bgCounter);
			
			
			if(list.size() > 0) {
				annotationRebuilding(storedLocation, filename, list);
//				FileImageUtil.deleteAll(dir);
			}
			
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			logger.debug("> bgCounter : " + bgCounter.toString());
			logger.debug("> list size : " + list.size());
			logger.debug("> else data imgMap : " + imgMap.size());
			logger.debug("> else data jsonMap : " + jsonMap.size());
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			
			
			Map param = new HashMap();
			param.put("assignmentno", bgtask.get("assignmentno"));
			param.put("location", storedLocation);
			List<UploadzipFile> insertList = new ArrayList<UploadzipFile>();
			List<ClsInfo> insertDetailList = new ArrayList<ClsInfo>();
			
			int labelInsertCount = 0;
			int labelDetailInsertCount = 0;
			try {
				for(int i=0; i<list.size(); i++) {
					UploadzipFile uf = list.get(i);
	
					insertList.add(uf);
					if(uf.getClsList() != null && !uf.getClsList().isEmpty()) insertDetailList.addAll(uf.getClsList());
					if(i%2000 == 1999) {
						logger.debug("> insertUploadFileBulk : " +i);
						param.put("list", insertList);
						param.put("listDetail", insertDetailList);
						try {
							if(SEARCH_ONLY) {
								logger.debug("> insertUploadFile Sample : " +insertList.get(0));
								logger.debug("> insertUploadFile Sample : " +insertDetailList.get(0));
							}
							else {
								labelInsertCount += assignmentMapper.insertUploadFileBulk(param);
								
								if(!insertDetailList.isEmpty())
									labelDetailInsertCount += assignmentMapper.insertUploadFileBulkDetail(param);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						insertList.clear();
						insertDetailList.clear();
					}
				}
				
				logger.debug("> insertUploadFileBulk additional " );
				if(insertList.size() > 0) {
					param.put("list", insertList);
					param.put("listDetail", insertDetailList);
					
					if(SEARCH_ONLY) {
						logger.debug("> insertUploadFile Sample : " +insertList.get(0));
						logger.debug("> insertUploadFile Sample : " +insertDetailList.get(0));
					}
					else {
						labelInsertCount += assignmentMapper.insertUploadFileBulk(param);
						
						if(!insertDetailList.isEmpty())
							labelDetailInsertCount += assignmentMapper.insertUploadFileBulkDetail(param);
					}
					logger.debug(">>>>>>>>>>>>>>>>>>> fin insertUploadFileBulk ");
					logger.debug("> labelInsertCount : " + labelInsertCount);
					logger.debug("> labelDetailInsertCount : " + labelDetailInsertCount);
				}
				
				
				EgovMap ass = assignmentMapper.getAssignmentDetail(param);
				if(ass != null) {
					param.put("datasetno", ass.get("datasetno"));
					datasetMapper.copyLabelingLinkNew(param);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		else {
			logger.error("<<<<< directory not exitst : " + uploadpath);
		}

		return false;
	}

	private void labelingMerge(File srcDir, List<UploadzipFile> list, Map imgMap, Map jsonMap
			, BgCounter bgCounter) {
		File[] files = srcDir.listFiles();
		for(File fl : files) {
			
			String fname = fl.getName();
			
			if(fname.equals(".") || fname.equals("..")) {
				continue;
			}
			else if(fl.isDirectory()) {
				if(fl.getName().toUpperCase().equals("__MACOSX")) {
					FileImageUtil.deleteAll(fl, true);
					logger.debug(">>> delete directory : __MACOSX ");
				}
				else {
					labelingMerge(fl, list, imgMap, jsonMap, bgCounter);
				}
				bgCounter.dirCounter++;
				logger.debug(">>> is directory : " + fname);
			}
			else {	/* is file */
//				logger.debug(">>> check : " + fname);
//				System.out.print(".");
				String[] arr = fname.split("[.]");
				if(arr.length > 1) {
					bgCounter.fileCounter++;
					String ext = arr[arr.length-1];
					String keyname = fname.substring(0, fname.indexOf("." + ext));
					ext = ext.toLowerCase();
					
					if(ext.equals("json")) {
						if(jsonMap.containsKey(keyname)) {
							logger.warn("<<< exist filename : " + fname);
						}
						else {
							if(imgMap.containsKey(keyname)) {
								UploadzipFile vo = (UploadzipFile)imgMap.get(keyname);
								vo.setJsonPath(fl.getParent());
								vo.setJsonName(fname);
								list.add(vo);
//								imgMap.remove(keyname);
							}
							else {
								jsonMap.put(keyname, new UploadzipFile(fl.getParent(), fname, null, null));
							}
						}
					}
					else if(ext.equals("jpg") || ext.equals("jpeg") || ext.equals("gif")
							|| ext.equals("png")) {
						
						if(imgMap.containsKey(keyname)) {
							logger.warn("<<< exist filename : " + fname);
						}
						else {
							if(jsonMap.containsKey(keyname)) {
								UploadzipFile vo = (UploadzipFile)jsonMap.get(keyname);
								vo.setImagePath(fl.getParent());
								vo.setImageName(fname);
								list.add(vo);
//								jsonMap.remove(keyname);
							}
							else {
								imgMap.put(keyname, new UploadzipFile(null, null, fl.getParent(), fname));
							}
						}
					}
					else {
						bgCounter.elseCounter++;
						logger.warn("<<< else type filename : " + fname);
					}
				}
				else {
					logger.warn("<<< filename has no ext : " + fname);
				}
			}
		}	// for loop
	}
	
	private void annotationRebuilding(String annotationLocation, String filename, List<UploadzipFile> list) {
		
		String jsonLoc = annotationLocation + AssignmentUtil.DIR_LABELING_SJSN + filename + "/";
		String imgLoc = annotationLocation + AssignmentUtil.DIR_LABELING_SIMG + filename + "/";
		jsonLoc = jsonLoc.replaceAll("//", "/");
		imgLoc = imgLoc.replaceAll("//", "/");
		
		int dirCounter = 0;
		String saveHgtLoc = newSaveLocation(jsonLoc, dirCounter);
		String saveImgLoc = newSaveLocation(imgLoc, dirCounter);
		
		ObjectMapper mapper = new ObjectMapper();
		
		for(int i=0; i<list.size(); i++) {
			UploadzipFile upfile = list.get(i);
			
			File src = new File(upfile.getJsonPath() + File.separatorChar + upfile.getJsonName());
			FileImageUtil.getJsonLabelData(src, upfile, mapper, saveHgtLoc);		// json 검증
			
			File dest = new File(saveHgtLoc + File.separatorChar + upfile.getJsonName());
			if(src.exists()) {
				if(SEARCH_ONLY) {
					if(i%10000 == 9999) {
						logger.debug("> json src : " + upfile.getJsonPath() + File.separatorChar + upfile.getJsonName());
						logger.debug("> json dest : " + saveHgtLoc + File.separatorChar + upfile.getJsonName());
					}
				}
				else {
					// src.renameTo(dest);
					try {
						Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				upfile.setJsonPath(saveHgtLoc);
			}
			else {
				logger.debug("< json file not exist : " + upfile.getJsonPath() + File.separatorChar + upfile.getJsonName());
			}
			
			src = new File(upfile.getImagePath() + File.separatorChar + upfile.getImageName());
			FileImageUtil.getExifLabelData(src, upfile); // image 검증
			
			dest = new File(saveImgLoc + File.separatorChar + upfile.getImageName());
			if(src.exists()) {
				if(SEARCH_ONLY) {
					if(i%10000 == 9999) {
						logger.debug("> img src : " + upfile.getImagePath() + File.separatorChar + upfile.getImageName());
						logger.debug("> img dest : " + saveImgLoc + File.separatorChar + upfile.getImageName());
					}
				}
				else {
					// src.renameTo(dest);
					try {
						Files.copy(src.toPath(),  dest.toPath() , StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				upfile.setImagePath(saveImgLoc);
			}
			else {
				logger.debug("< img file not exist : " + upfile.getImagePath() + File.separatorChar + upfile.getImageName());
			}
			
			if(i % 10000 == 9999) {
				dirCounter ++;
				saveHgtLoc = newSaveLocation(jsonLoc, dirCounter);
				saveImgLoc = newSaveLocation(imgLoc, dirCounter);
			}
		}
	}
	
	private String newSaveLocation(String base, int no) {
		String res = base;
		if(!res.endsWith("/")) res += "/"; 
		
		res += String.format("%05d", no);
		logger.debug("> new directory : " + res);
	
		checkMkDir(res);
		return res;
	}

	private int updateIncomingStatus(Map param, String status) throws Exception {
		param.put("status", status);
		return assignmentBgMapper.updateAssignmentIncomingStatus(param);
	}

	private boolean checkMkDir(String dir) {
		File f = new File(dir);
		if(f.exists() && f.isDirectory()) return true;
		
		logger.debug(">>> mkdir : " + dir);
		return f.mkdirs();
	}
}
