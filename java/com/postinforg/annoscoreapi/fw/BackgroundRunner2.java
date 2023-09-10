package com.postinforg.annoscoreapi.fw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.postinforg.annoscoreapi.mapper.EvalBig2Mapper;
import com.postinforg.annoscoreapi.mapper.EvalBigMapper;
import com.postinforg.annoscoreapi.mapper.ExportMapper;
import com.postinforg.annoscoreapi.mapper.LabelingMapper;
import com.postinforg.annoscoreapi.model.Export;
import com.postinforg.annoscoreapi.util.AssignmentUtil;
import com.postinforg.annoscoreapi.util.StrNumUtil;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Component
public class BackgroundRunner2 {

	Logger logger = LoggerFactory.getLogger(BackgroundRunner2.class);
	
	@Autowired AssignmentUtil assignmentUtil;
	@Autowired EvalBigMapper evalBigMapper;
	@Autowired EvalBig2Mapper evalBig2Mapper;
	@Autowired ExportMapper exportMapper;
	@Autowired LabelingMapper labelingMapper;
	
	@Value("${spring.profiles.active}") private String activeProfiles;
	@Value("${bg.runtype}") private String runtype;
	
	@Scheduled(fixedRate = 1000 * 23)
	public void scheduler21() throws InterruptedException {
		if(activeProfiles.endsWith(BackgroundRunner.BG_MODE_PROFILE)) {
			if(runtype.equals("all") || runtype.equals("2")) {
				logger.debug(">>> call exportCheck(zip)");
				exportCheck("zip");
			}
		}
	}
	
	@Scheduled(fixedRate = 1000 * 17)
	public void scheduler22() throws InterruptedException {
		if(activeProfiles.endsWith(BackgroundRunner.BG_MODE_PROFILE)) {
			if(runtype.equals("all") || runtype.equals("3")) {
				logger.debug(">>> call exportCheck(csv)");
				exportCheck("csv");
			}
		}
	}
	
	private boolean exportCheck(String bgtype) {
		try {
			/* 업데이트 후 20초 ~ 1시간 이내 작업만 처리 */
			Map param = new HashMap();
			param.put("limit", 10);
			param.put("offset", 0);
			param.put("status", "0");
			if(bgtype.equals("zip")) param.put("bgtype", "zip"); 
			else param.put("bgtype", "csv"); 
			
			List<EgovMap> l = exportMapper.selectExportList(param);
			if(l != null && !l.isEmpty()) {
				logger.debug(">>>>>>>>>>>> export ... ");
				EgovMap export = l.get(0);
				
				param.put("no", export.get("no"));
				String status = export.get("status").toString();
				String menu = export.get("menu").toString();
				
				if(!status.equals("0")) {
					logger.debug("<<< status is not 0 : " + status);
					return false;
				}
				
				param.put("status", 3);
				int res = exportMapper.exportUpdate(param);
				
				/* 핵심 함수 */
				if(menu.equals("zip")) {
					res = exportZip(export);
				}
				else {	// csv
					res = exportCsv(export, menu);
				}
				
				if(res > 0) {
					param.put("status", 5);
					param.put("currentCount", res);
				}
				else param.put("status", 9);
				
				res = exportMapper.exportUpdate(param);
				return true;
			}
		} catch (Exception e) {
			logger.error("<<< export error : " + e.getMessage());
			e.printStackTrace();
		}
		
		return false;
	}

	private int exportZip(Map export) {
		String subdir = export.get("subdir").toString();
		String filename = export.get("filename").toString();
		String ziptype = export.get("ziptype") == null ? "" : export.get("ziptype").toString();
		
		int tasklimit = StrNumUtil.getIntVal0(export, "tasklimit");
		if(tasklimit == 0) tasklimit = Export.DEFAULT_TASK_LIMIT;
		logger.debug(">>> tasklimit : " + tasklimit);
		
		int splitsize = StrNumUtil.getIntVal0(export, "splitsize");
		if(splitsize == 0) splitsize = Export.DEFAULT_SPLIT_SIZE;
		logger.debug(">>> splitsize : " + splitsize);
		
		BufferedWriter bufferedWriter = null;
		
		try {
			File f = new File(subdir);
			if(!f.exists() || !f.isDirectory()) f.mkdirs();
			
			int checkedCount = labelingMapper.selectLabelingTotal(export);
			logger.debug(">>> checked count for zip " + checkedCount);
		
			if(checkedCount < 1) return 0;
			export.put("status", 3);
			export.put("totalCount", checkedCount);
			
			int offset = 0;
			export.put("limit", tasklimit);
//			export.put("checked", "1");
			List<EgovMap> l = null;
			
			String textFileName = subdir + filename + "_index.txt";
			logger.debug(">>> textFileName : " + textFileName);
			
			bufferedWriter = new BufferedWriter(new FileWriter(textFileName, false));
			
			int resultCount = 0;
			String splitDir = "";
			String append = "";
			for(int i=0; resultCount < checkedCount || i*tasklimit < checkedCount; i++) {
				offset = tasklimit * i;		
				export.put("offset", offset);
				l = labelingMapper.selectLabelingList(export);
				
				splitDir = subdir + String.format("%05d", i);
				f = new File(splitDir);
				if(!f.exists() || !f.isDirectory()) f.mkdirs();
				
				append = zipAppend(l, offset, splitDir, ziptype);
				bufferedWriter.write(append);
				
				resultCount += l.size();
				export.put("currentCount", resultCount/2);
				exportMapper.exportUpdate(export);
				
//				if (i == 1) {	// 테스트 후 삭제!
//					break;
//				}
			}
			
			bufferedWriter.close();
			resultCount = checkedCount / 2;
			export.put("currentCount", resultCount);
			export.put("status", 4);	// 압축중...
			exportMapper.exportUpdate(export);
			
			// file zip start
			String commands = "zip -s " + splitsize + " -r -1 "
					+ filename + ".zip * -x *.txt";
			
			ProcessBuilder pb = new ProcessBuilder();
			pb.directory(new File(subdir));
			pb.command("bash", "-c", commands);
			logger.debug(">>> zip command >> " + commands);
			Process process = pb.start();
			BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			long zipCounter = 0;
			String str = null;
			while((str = stdOut.readLine()) != null) {
				zipCounter++;
				if(zipCounter % 10000 == 0) {
					logger.debug("> zipCounter : " + zipCounter + " : " + str);
					resultCount += 5000;
					export.put("currentCount", resultCount);
					exportMapper.exportUpdate(export);
				}
			}
			
			return checkedCount;
		}
		catch(Exception e) {
			logger.error("<<< exportZip error " + e.getMessage());
			if(bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e1) { e1.printStackTrace(); }
			}
			e.printStackTrace();
		}
		
		return 0;
	}

	
	private String zipAppend(List<EgovMap> l, int offset, String splitDir, String ziptype) {
		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<l.size(); i++) {
			EgovMap map = l.get(i);
			
			File srcImg = new File(map.get("imagedir") + "/" + map.get("imagefile"));
			File destImg = new File(splitDir + "/" + map.get("imagefile"));
			File srcJson = new File(map.get("labelingdir") + "/" + map.get("labelingfile"));
			File destJson = new File(splitDir + "/" + map.get("labelingfile"));
			
			if(!srcImg.exists()) {
				logger.error("<<< not exist " + srcImg.toPath());
			}
			else if(!srcJson.exists()) {
				logger.error("<<< not exist " + srcJson.toPath());
			}
			else {
				try {
					if(!ziptype.equals("json"))
						Files.copy(srcImg.toPath(), destImg.toPath(), StandardCopyOption.REPLACE_EXISTING);
					
					Files.copy(srcJson.toPath(), destJson.toPath(), StandardCopyOption.REPLACE_EXISTING);
					
					sb.append((offset+i+1) + " ")
					.append(splitDir+"/")
					.append(map.get("labelingfile"))
					.append("\n")
					;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	private int exportCsv(Map export, String menu) {
		String subdir = export.get("subdir").toString();
		String filename = export.get("filename").toString();
		
		String append = "";
		BufferedWriter bufferedWriter = null;
		try {
			File f = new File(subdir);
			if(!f.exists() || !f.isDirectory()) f.mkdirs();
			
//			textFileName = assignmentHome + AssignmentUtil.getAssignmentDirFromNo(assno) 
			String textFileName = subdir + filename;
			logger.debug(">>> textFileName : " + textFileName);
			
			bufferedWriter = new BufferedWriter(new FileWriter(textFileName, false));
			
			int checkedCount = 0;
			if(menu.equals("ds")) {	// 라벨링 원본
				checkedCount = labelingMapper.selectLabelingTotal(export);
				append = "no,pic,pf,is,iw,ih,ir,ift,lfs,lft,ls,aiou,asc,idev,oc,cc,fpc,fnc\n";
			}
			else if(menu.equals("eds")) {
				checkedCount = evalBigMapper.selectEvalFileListTotal(export);
				append = "no,pic,pf,is,iw,ih,ir,ift,lfs,lft,ls,aiou,asc,idev,oc,cc,fpc,fnc\n";
			}
			else if(menu.equals("oa")) {
				checkedCount = evalBig2Mapper.selectEvalObjSimpleListTotal(export);
				append = "ObjNo,FileName,Cls,BlurCoef,Diagonal,Height,Width,Histogram,ObjId,Ratio,ShapeType\n";
			}
			else if(menu.equals("oe")) {
				checkedCount = evalBig2Mapper.selectEvalObjSimplePredListTotal(export);
				append = "ObjNo,Filename,Cls,Correct,Total,Iou,Confidence,Tp50,Tp60,Tp70,Tp80,Tp90,Fp50,Fp60,Fp70,Fp80,Fp90\n";
			}
			else if(menu.equals("ce")) {
				checkedCount = evalBigMapper.selectEvalClsListTotal(export);
				append = "name,objCnt,ap50,ap55,ap60,ap65,ap70,ap75,ap80,ap85,ap90,ap95\n";
			}
			bufferedWriter.write(append);
			
			if(checkedCount < 1) return 0;
			export.put("status", 3);
			export.put("totalCount", checkedCount);
			
			int offset = 0;
			export.put("limit", Export.DEFAULT_CSV_LIMIT);
			// export.put("orderColumn", "no");
//			export.put("checked", "1");
			List<EgovMap> l = null;
			
			int result = 0;
			for(int i=0; result < checkedCount || i*Export.DEFAULT_CSV_LIMIT < checkedCount; i++) {
				offset = Export.DEFAULT_CSV_LIMIT * i;		
				export.put("offset", offset);
					
				if(menu.equals("ds")) {	// 라벨링 원본
					l = labelingMapper.selectLabelingList(export);
					append = labelingAppend(l);
				}
				else if(menu.equals("eds")) {
					l = evalBigMapper.selectEvalFileList(export);
					append = labelingAppend(l);
				}
				else if(menu.equals("oa")) {
					l = evalBig2Mapper.selectEvalObjSimpleList(export);
					append = objAppend(l);
				}
				else if(menu.equals("oe")) {
					l = evalBig2Mapper.selectEvalObjSimplePredList(export);
					append = objPredAppend(l);
				}
				else if(menu.equals("ce")) {
					l = evalBigMapper.selectEvalClsList(export);
					append = clsAppend(l);
				}
				
				bufferedWriter.write(append);
				
//				logger.debug(">>> selected count (" + i + ") "+ l.size());
				result += l.size();
				export.put("currentCount", result);
				int res = exportMapper.exportUpdate(export);
			}

			bufferedWriter.close();
			logger.debug("> total result : " + result);
			return checkedCount;
			
		} catch(Exception e) {
			if(bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e1) { e1.printStackTrace(); }
			}
			
			e.printStackTrace(); 
		}
		
		return 0;
	}

	private String labelingAppend(List<EgovMap> l) {
		StringBuffer sb = new StringBuffer();
		for(EgovMap emap : l) {
			sb.append(StrNumUtil.getValue(emap, "evalno")).append(",")
			.append(StrNumUtil.getValue(emap, "imagedir")).append(",")
			.append(StrNumUtil.getValue(emap, "imagefile")).append(",")
			.append(StrNumUtil.getFileSizeK(emap, "imagesize")).append(",")
			.append(StrNumUtil.getValue(emap, "imagewidth")).append(",")
			.append(StrNumUtil.getValue(emap, "imageheight")).append(",")
			.append(StrNumUtil.getDoubleValue(emap, "imageratio")).append(",")
			.append(StrNumUtil.getValue(emap, "imagedate")).append(",")
			.append(StrNumUtil.getFileSizeK(emap, "labelingsize")).append(",")
			.append(StrNumUtil.getValue(emap, "labelingdate")).append(",")
			.append(StrNumUtil.getValidValue(emap, "labelingvalidity")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "aiou")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "asc")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ascDev")).append(",")
			.append(StrNumUtil.getValue(emap, "objCount")).append(",")
			.append(StrNumUtil.getValue(emap, "classCount")).append(",")
			.append(StrNumUtil.getValue(emap, "fpc")).append(",")
			.append(StrNumUtil.getValue(emap, "fnc")).append("\n")
			;
		}
		return sb.toString();
	}
	
	private String clsAppend(List<EgovMap> l) {
		StringBuffer sb = new StringBuffer();
		for(EgovMap emap : l) {
			sb.append(StrNumUtil.getValue(emap, "name")).append(",")
			.append(StrNumUtil.getValue(emap, "objCount")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ap50")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ap55")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ap60")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ap65")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ap70")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ap75")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ap80")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ap85")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ap90")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ap95")).append("\n")
			;
		}
		return sb.toString();
	}
	
	private String objAppend(List<EgovMap> l) {
		StringBuffer sb = new StringBuffer();
		for(EgovMap emap : l) {
			sb.append(StrNumUtil.getValue(emap, "eno")).append(",")
			.append(StrNumUtil.getValue(emap, "fileName")).append(",")
			.append(StrNumUtil.getValue(emap, "cls")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "blurCoef")).append(",")
			.append(StrNumUtil.getValue(emap, "diagonal")).append(",")
			.append(StrNumUtil.getValue(emap, "height")).append(",")
			.append(StrNumUtil.getValue(emap, "width")).append(",")
			.append(StrNumUtil.getValue(emap, "histogram")).append(",")
			.append(StrNumUtil.getValue(emap, "objId")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "ratio")).append(",")
			.append(StrNumUtil.getValue(emap, "shapeType")).append("\n")
			;
		}
		return sb.toString();
	}

	private String objPredAppend(List<EgovMap> l) {
		StringBuffer sb = new StringBuffer();
		for(EgovMap emap : l) {
			sb.append(StrNumUtil.getValue(emap, "eno")).append(",")
			.append(StrNumUtil.getValue(emap, "fileName")).append(",")
			.append(StrNumUtil.getValue(emap, "cls")).append(",")
			.append(StrNumUtil.getValue(emap, "correct")).append(",")
			.append(StrNumUtil.getValue(emap, "total")).append(",")
			.append(StrNumUtil.getDoubleValue3(emap, "iou")).append(",")
			.append(StrNumUtil.getValue(emap, "confidence")).append(",")
			.append(StrNumUtil.getIntValue(emap, "tp50")).append(",")
			.append(StrNumUtil.getIntValue(emap, "tp60")).append(",")
			.append(StrNumUtil.getIntValue(emap, "tp70")).append(",")
			.append(StrNumUtil.getIntValue(emap, "tp80")).append(",")
			.append(StrNumUtil.getIntValue(emap, "tp90")).append(",")
			.append(StrNumUtil.getIntValue(emap, "fp50")).append(",")
			.append(StrNumUtil.getIntValue(emap, "fp60")).append(",")
			.append(StrNumUtil.getIntValue(emap, "fp70")).append(",")
			.append(StrNumUtil.getIntValue(emap, "fp80")).append(",")
			.append(StrNumUtil.getIntValue(emap, "fp90")).append("\n")
			;
		}
		return sb.toString();
	}
	
}
