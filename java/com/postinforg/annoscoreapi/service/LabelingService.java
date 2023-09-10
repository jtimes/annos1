package com.postinforg.annoscoreapi.service;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.postinforg.annoscoreapi.controller.CommonController;
import com.postinforg.annoscoreapi.mapper.AssignmentMapper;
import com.postinforg.annoscoreapi.mapper.LabelingMapper;
import com.postinforg.annoscoreapi.util.AssignmentUtil;
import com.postinforg.annoscoreapi.util.FileImageUtil;
import com.postinforg.annoscoreapi.util.JsonFileValidator;
import com.postinforg.annoscoreapi.util.LabelingUtil;
import com.postinforg.annoscoreapi.util.StrNumUtil;

import egovframework.rte.psl.dataaccess.util.EgovMap;

@Service
@Transactional(rollbackFor = Exception.class)
public class LabelingService {
	
	@Resource LabelingMapper labelingMapper;
	@Resource AssignmentMapper assignmentMapper;
	
	@Autowired JsonFileValidator jsonFileValidator;
	@Autowired AssignmentUtil assignmentUtil;
	
	@Value("${spring.profiles.active}") private String activeProfiles;
	@Value("${media.assignmenthome}") private String assignmentHome;
	@Value("${media.datahome}") private String datahome;
	
	Logger logger = LoggerFactory.getLogger(LabelingService.class);
	
	public Map getLabelingList(Map param) {
		Map res = new HashMap();
		List<EgovMap> l = null;
		int total = 0;
		try {
			if(param.containsKey("gttype") && param.get("gttype").toString().equals("hgt")) {
				total = labelingMapper.selectLabelingHgtTotal(param);
				l = labelingMapper.selectLabelingHgtList(param);
				
				res.put("checkedCount", labelingMapper.selectLabelingHgtChecked(param));
			}
			else if(param.containsKey("gttype") && param.get("gttype").toString().equals("mgt")) {
				total = labelingMapper.selectLabelingMgtTotal(param);
				l = labelingMapper.selectLabelingMgtList(param);
			}
			else {
				if(!param.containsKey("datasetno")) {
					EgovMap asm = assignmentMapper.getAssignmentDetail(param);
					param.put("datasetno", asm.get("datasetno"));
				}
				
				total = labelingMapper.selectLabelingTotal(param);
				l = labelingMapper.selectLabelingList(param);
				
				int cCount = labelingMapper.getLabelingListChecked(param);
				res.put("checkedCount", cCount);
				
				String et = labelingMapper.getEt(param);
				res.put("et", et);
			}
			if(Integer.parseInt(param.get("limit").toString()) <= 20) { 
				ObjectMapper mapper = new ObjectMapper();
				for(EgovMap map : l) {
					LabelingUtil.checkImageAttribute(map, mapper, jsonFileValidator);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		res.put("totalcount", total);
		res.put("list", l);
		return res;
	}

	private void checkValidAddImageAttribute(EgovMap labeling, ObjectMapper mapper) {
		boolean isJsonValid = false;
		Map exif = null;
		
		if(labeling.get("labelingdir") == null || labeling.get("labelingdir").toString().equals("") ||
				labeling.get("labelingfile") == null || labeling.get("labelingfile").toString().equals("")
				) {
		}
		else {
			String path = labeling.get("labelingdir").toString() + "/" + labeling.get("labelingfile").toString();
			logger.debug("> path : " + path);
			File file = new File(path);
			if(file.exists() && file.isFile()) {
				try {
					String jsonBody = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
					Map data = mapper.readValue(jsonBody, Map.class);
					if(jsonFileValidator.isValid(data)) isJsonValid = true;
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				logger.debug("> not exist : " + path);
			}
			
			path = labeling.get("imagedir").toString() + "/" + labeling.get("imagefile").toString();
			file = new File(path);
			if(file.exists() && file.isFile()) {
				try {
					exif = FileImageUtil.getAllExifData(file);
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				logger.debug("> not exist : " + path);
			}
			if(labeling.containsKey("imagedir")) {
				path = labeling.get("imagedir").toString();
				labeling.put("imagedir", path.replace("aaihq/", ""));
			}
		}
		
		labeling.put("isJsonValid", isJsonValid);
		labeling.put("exif", exif);
	}

	public int labelingGtAdd(String[] anos, String assignmentno) {
		Map param = new HashMap();
		param.put("anos", anos);
		param.put("assignmentno", assignmentno);
		
		int result = 0;
		try {
			EgovMap assignment = assignmentMapper.getAssignmentDetail(param);
			if(assignment == null) return -1;
			
			List<EgovMap> anList = labelingMapper.labelingCheckList(param);
			if(anList != null && anList.size() > 0) {
				for(EgovMap eMap : anList) {
					assignmentUtil.gtCopy(eMap, assignment, assignmentHome);
				}
				param.put("anos", anList);
				result = labelingMapper.labelingGtAdd(param);
				
//				String loc = assignmentHome + assignmentUtil.getAssignmentDirFromNo(assignmentno);
//				param.put("usage", assignmentUtil.dirUsage2(loc));
//				assignmentMapper.updateAssignmentUsageDate(param);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int labelingGtAddRandom(String assignmentno, int cnt) {
		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
		param.put("limit", cnt);
		
		int result = 0;
		try {
			EgovMap assignment = assignmentMapper.getAssignmentDetail(param);
			if(assignment == null) return -1;
			
			int annoTotal = Integer.parseInt(assignment.get("labelingTotal").toString());
			int hgtCount = Integer.parseInt(assignment.get("hgtCount").toString());
			if(annoTotal < hgtCount + cnt) {
				return -2;
			}
			
			List<EgovMap> anList = labelingMapper.labelingRandomList(param);
			if(anList != null && anList.size() > 0) {
				for(EgovMap eMap : anList) {
					if(!assignmentUtil.gtCopy(eMap, assignment, assignmentHome)) {
						return -3;
					}
				}
				param.put("anos", anList);
				result = labelingMapper.labelingGtAdd(param);
				
//				String loc = assignmentHome + assignmentUtil.getAssignmentDirFromNo(assignmentno);
//				param.put("usage", assignmentUtil.dirUsage2(loc));
//				assignmentMapper.updateAssignmentUsageDate(param);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int labelingGtDel(String[] anos, String assignmentno) {
		Map param = new HashMap();
		param.put("anos", anos);
		param.put("assignmentno", assignmentno);
		
		int result = 0;
		try {
			List<EgovMap> anList = labelingMapper.labelingGtList(param);
			if(anList != null && anList.size() > 0) {
				result = labelingMapper.labelingGtDel(param);
				for(EgovMap eMap : anList) {
					assignmentUtil.gtDel(eMap);
				}
				
//				Map assignment = assignmentMapper.getAssignmentDetail(param);
//				String loc = assignmentHome + assignmentUtil.getAssignmentDirFromNo(assignmentno);
//				param.put("usage", assignmentUtil.dirUsage2(loc));
//				assignmentMapper.updateAssignmentUsageDate(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Map getLabelingInfo(Map param) {
		Map res = new HashMap();
		try {
			Map anno = labelingMapper.getLabeling(param);
			
			if(anno != null) {
				setLabelingInfo(res, anno);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	private void setLabelingInfo(Map res, Map anno) throws IOException, JsonParseException, JsonMappingException {
		String path2 = anno.get("imagedir").toString();
		if(!path2.endsWith("/")) path2 += "/";
		path2 += anno.get("imagefile").toString();
		
		res.put("imgpath", path2);
		res.put("labelingfile", anno.get("labelingfile"));
		res.put("labelingdir", anno.get("labelingdir"));

		if(anno.containsKey("labelingdir") && anno.containsKey("labelingfile") &&
				anno.get("labelingdir") != null && anno.get("labelingfile") != null) {
			String path = anno.get("labelingdir").toString();
			if(!path.endsWith("/")) path += "/";
			path += anno.get("labelingfile").toString();
			logger.debug(">>> json path : " + path);
			
			File file = new File(path);
			if(file.exists() && file.isFile()) {
				String jsonBody = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
				Map data = new ObjectMapper().readValue(jsonBody, Map.class);
				res.put("data", data);
				res.put("jsonpath", path);
			}
		}
	}
	

	public Map getGtInfo(Map param) {
		Map res = new HashMap();
		try {
			EgovMap anno = labelingMapper.getGtLabeling(param);
			if(anno != null) {
				setLabelingInfo(res, anno);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public String saveLabeling(Map anno, String data, boolean isGt) {
		String orgfilename = "";
		
		logger.debug(">>> anno : " + anno);
		logger.debug(">>> anno.labelingfile : " + anno.get("labelingfile"));
		
		if(isGt) orgfilename = anno.get("hgtfile").toString();
		else orgfilename = anno.get("labelingfile").toString();
		
		String newfilename = StrNumUtil.getNewName(orgfilename);
		
		String rootPath = "";
		if(isGt) rootPath = anno.get("hgtdir").toString();
		else rootPath = anno.get("labelingdir").toString();
		
		String path1 = "";
		if(path1.endsWith("/")) path1 = rootPath + orgfilename;
		else path1 = rootPath + "/" + orgfilename;
//		String path2 = rootPath + newfilename;
		logger.debug(">>> org path : " + path1);
//		logger.debug(">>> new path : " + path2);
		
		Map dmap = null;
		String jsonBody = null;
		try {
			jsonBody = URLDecoder.decode(data, "UTF-8");
			dmap = new ObjectMapper().readValue(jsonBody, Map.class);
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error(">>> data parsing error : " + data);
		}
		
		if(dmap == null) {
			return "";
		}
		
		logger.debug(">>> decoded data : " + jsonBody);
		
		File ofile = new File(path1);
		if(!ofile.exists() || !ofile.isFile()) {
			logger.error(">> " + ofile + " < not exist");
			return "fe";
		}

		try {
			FileUtils.writeStringToFile(ofile, data, CommonController.CHAR_SER);
			return newfilename;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "";
	}

	public int labelingMgtReset(String assignmentno) {
		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
		
		int result = 0;
		try {
			result = labelingMapper.labelingMgtReset(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public int labelingMgtUpdate(String anos, String mgtdir, String mgtfile) {
		Map param = new HashMap();
		param.put("labelingno", anos);
		param.put("mgtdir", mgtdir);
		param.put("mgtfile", mgtfile);
		
		int result = 0;
		try {
			result = labelingMapper.labelingMgtUpdate(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int labelingMgtUpdateBulk(String mgtinfo) {
		Map param = new HashMap();
		
		List<Map> l = new ArrayList<Map>();
		String[] mgts = mgtinfo.split(",");
		for(String mgt : mgts) {
			if(!mgt.equals("")) {
				String[] args = mgt.split("[|]");
				if(args.length == 3) {
					Map m = new HashMap();
					m.put("labelingno", args[0]);
					m.put("mgtdir", args[1]);
					m.put("mgtfile", args[2]);
					l.add(m);
				}
			}
		}
		
		int result = 0;
		if(l.size() > 0) {
			param.put("list", l);
			try {
				result = labelingMapper.labelingMgtUpdateBulk(param);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public int labelingGtAddAll(String assignmentno) {
		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
//		EgovMap assignment = null;
//		try {
//			assignment = assignmentMapper.getAssignmentDetail(param);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
		
//		if(assignment == null) return -1;
		
		String srcDir = assignmentHome + assignmentUtil.getAssignmentDirFromNo(assignmentno)
			+ assignmentUtil.DIR_ASSIGNMENT_STO;
		String newDir = assignmentHome + assignmentUtil.getAssignmentDirFromNo(assignmentno) 
			+ assignmentUtil.DIR_ASSIGNMENT_HGT;
		//	+ "/model-data/";
		
		logger.debug("> srcDir " + srcDir);
		logger.debug("> newDir " + newDir);
		
		int res = 0;
		try {
			res = labelingMapper.labelingGtDelAll(param);
			if(FileImageUtil.deleteAll(new File(newDir), true)) {
				if(FileImageUtil.runCopyCommand(activeProfiles, srcDir, newDir)) {
//					param.put("location", location);
					param.put("srcDir", srcDir);
					param.put("newDir", newDir);
					res = labelingMapper.labelingGtAddAll(param);
				}
				else {
					res = -3;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			res = -4;
		}
		
		return res;
	}

	public int labelingGtDelAll(String assignmentno) {
		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
		
		String hgtDir = assignmentHome + assignmentUtil.getAssignmentDirFromNo(assignmentno);
		String hgtImgDir = hgtDir + assignmentUtil.DIR_ASSIGNMENT_IMG;
		String hgtLabelDir = hgtDir + assignmentUtil.DIR_ASSIGNMENT_HGT;
		
		int res = 0;
		try {
			res = labelingMapper.labelingGtDelAll(param);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(res > 0)
			res = assignmentUtil.gtDelAll(hgtImgDir, hgtLabelDir);
		return res;
	}

	public int labelingGtAddChecked(String assignmentno) {
		Map param = new HashMap();
		param.put("assignmentno", assignmentno);
		
		int result = 0;
		try {
			EgovMap assignment = assignmentMapper.getAssignmentDetail(param);
			if(assignment == null) return -1;
			param.put("datasetno", assignment.get("datasetno"));
			
			int total = labelingMapper.getLabelingListChecked(param);
			if(total < 1) return -2;
			
			int resultCount = 0;
			int limit = 100;
			int offset = 0;
			param.put("limit", limit);
			param.put("offset", offset);
			
			for(int i=0; resultCount < total; i++) {
				List<EgovMap> anList = labelingMapper.selectLabelingCheckedList(param);
				if(anList != null && anList.size() > 0) {
					for(EgovMap eMap : anList) {
						assignmentUtil.gtCopy(eMap, assignment, assignmentHome);
					}
					param.put("anos", anList);
					result = labelingMapper.labelingGtAdd(param);
					
					resultCount += anList.size();
				}
				offset += limit;
				param.put("offset", offset);
			}
			result = 1;
		} catch (Exception e) {
			result = 0;
			e.printStackTrace();
		}
		
		return result;
	}
	

}
