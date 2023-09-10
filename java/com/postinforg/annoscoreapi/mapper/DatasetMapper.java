package com.postinforg.annoscoreapi.mapper;

import java.util.List;
import java.util.Map;

import egovframework.rte.psl.dataaccess.util.EgovMap;


public interface DatasetMapper {

	int selectDatasetTotal(Map param) throws Exception;
	List<EgovMap> selectDatasetList(Map param) throws Exception;
	EgovMap selectDataset(Map param) throws Exception;

	int selectDatasetLabelingTotal(Map param) throws Exception;
	List<EgovMap> selectDatasetLabelingList(Map param) throws Exception;
	List<EgovMap> selectDatasetLabelingList0(Map param) throws Exception;
	
	int datasetAdd(Map param) throws Exception;
	int datasetDel(Map param) throws Exception;
	
	int datasetLabelingAdd(Map param) throws Exception;
	int datasetLabelingDel(Map param) throws Exception;
	
	int copyLabelingLink(Map param) throws Exception;
	int datasetEval(Map param) throws Exception;
	
	int datasetLabelingDelAll(Map param) throws Exception;
	int datasetLabelingAddAll(Map param) throws Exception;
	int datasetLabelingAddFilter(Map param) throws Exception;
	int datasetLabelingAddChecked(Map param) throws Exception;
	int datasetLabelingFileAddChecked(Map param) throws Exception;
	
	EgovMap selectDatasetLabelingDetail(Map param) throws Exception;
	int datasetUpdate(Map param) throws Exception;

	EgovMap datasetLabelingCount(Map param) throws Exception;
	int datasetLabelingAddRandom(Map param) throws Exception;
	List<EgovMap> datasetReportList(Map param) throws Exception;
	EgovMap selectDatasetDetail(Map param) throws Exception;
	int datasetLabelingClear(EgovMap param) throws Exception;
	int deleteDataset(EgovMap param) throws Exception;
	int evalNeedTime(Map param) throws Exception;
	void copyLabelingLinkNew(Map param) throws Exception;
	
}
