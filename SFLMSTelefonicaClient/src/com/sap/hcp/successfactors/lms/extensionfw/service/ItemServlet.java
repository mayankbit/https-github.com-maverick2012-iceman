package com.sap.hcp.successfactors.lms.extensionfw.service;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.w3c.dom.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.Item;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.ItemListId;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.Offering;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.OfferingListId;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.OverviewScreen;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.Parametrised;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.ReportInfo;
import com.sap.hcp.successfactors.lms.extensionfw.reporting.GenerateReportFotItem;  

@Controller
public class ItemServlet {

	@Autowired
	NewOfferingService newofferingservice;

	@Autowired
	ItemService itemservice;

	@Autowired
	ReportStructService reportStructService;

	@Autowired
	TagMappingService tagmappingservice;

	@Autowired
	FileService fileservice;

	@Autowired
	ParametrisedService parameterised;

	@Autowired
	ReportInfoService reportInfoService;

	@Autowired
	ItemListIdService itemListIdService;
	
	List<Parametrised> parameterizedList = null; 
	
	private static final Logger logger = LoggerFactory.getLogger(ItemServlet.class);

	@RequestMapping(value = ApiValues.ITEMREPORT, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void generateItemReport(@PathVariable String id,
			@PathVariable String legalEntity, @PathVariable String date,
			@PathVariable String runId,
			HttpServletResponse response, HttpServletRequest request) throws IOException {

		ServletOutputStream out = response.getOutputStream();
		List<Item> itemList;
		Map<String, String> itemidmap = new HashMap<String, String>();
		HttpSession batman = request.getSession(false);
		if ("none".equalsIgnoreCase(runId)) {
			if(batman.getAttribute("itemList") != null)
				itemList = (List<Item>)batman.getAttribute("itemList");
			else
				itemList = itemservice.getItemData(id, legalEntity, date, runId);
			if(batman.getAttribute("itemidmap") != null){
				itemidmap = (Map<String, String>)batman.getAttribute("itemidmap");
			}
			else{ 
				List<ItemListId> itemidlist;
				itemidlist = itemListIdService.getAll();
				itemidmap = convertListToMap(itemidlist);
			}
			itemList = getListData(itemList);
			itemList = getListFinalData(itemList,date);
			List<Item> tempList = new ArrayList<Item>();
			tempList.addAll(itemList);
			
			if (tempList != null) { 
				for (Item item : tempList) {
					String key = matrixKeyMaker(String.valueOf(item.getId()), legalEntity.toLowerCase());
					if(itemidmap.get(key) != null && itemidmap.get(key).equalsIgnoreCase("x")){
						itemList.remove(item);
					}
				}
			}
		} else {
			ReportInfo obj = reportInfoService.getById(Long.parseLong(runId));
			//if(date != null && !date.equalsIgnoreCase("none"))
//			{
//				itemList = itemservice.getItemData(obj.getCriteriaId(), obj.getLegalEntity(), obj.getDate(), runId);
//			}
//			else
			{
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				String date2 = formatter.format(obj.getCreatedDate());
				StringBuilder sb = new StringBuilder();
				sb.append("15-08-1947-");
				sb.append(date2);
				itemList = itemservice.getItemData(obj.getCriteriaId(), obj.getLegalEntity(), sb.toString(), runId);
			}
		
			itemList = getListData(itemList);
			itemList = getListFinalData(itemList,date);
		/*	List<ItemListId> itemListIds = itemListIdService.getByReportId(Long.parseLong(runId));
			List<Long> itemIds = new ArrayList<Long>();
			logger.error("M1"+itemListIds.size());
			for(ItemListId itemListId : itemListIds) {
				itemIds.add(itemListId.getItemId());
			}
			logger.error("M2"+itemIds.size());
			itemList = itemservice.getItemByItemIds(itemIds);
			logger.error("M3"+itemList.size());
			itemList = getListData(itemList);
			logger.error("M4"+itemList.size());
			itemList = getListFinalData(itemList);
			logger.error("M5"+itemList.size());*/
		}

		parameterizedList = parameterised.getAllParametrisedData();

		GenerateReportFotItem gr = new GenerateReportFotItem();
		Document document = gr.generateXMLReport(itemList, parameterizedList,
				legalEntity);

		DOMSource domSource = new DOMSource(document);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		StreamResult result = new StreamResult(bos);
		TransformerFactory tf = TransformerFactory.newInstance();
		/*tf.setAttribute("http://javax.xml.XMLConstants/feature/secure-processing", true);
		tf.setAttribute("http://xml.org/sax/features/external-general-entities", false);
		tf.setAttribute("http://xml.org/sax/features/external-parameter-entities", false);*/
		
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "true");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.transform(domSource, result);
		} catch (TransformerException e) {
			logger.error("Item report xml transfromation exception in class --> ItemServlet method --> generateItemReport " + e.getMessage());
		}

		// if report successfully created, then create an entry in the
		// reportInfo table

		ReportInfo rinfo = new ReportInfo();
		rinfo.setDate(date);
		rinfo.setCreatedDate(new Date(System.currentTimeMillis()));
		if (id != null)
			rinfo.setCriteriaId(id);
		if (legalEntity != null)
			rinfo.setLegalEntity(legalEntity);
		rinfo.setReportType("Item");
		reportInfoService.save(rinfo);
		List<ItemListId> saveList = new ArrayList<ItemListId>();
		for (Item item : itemList) {
			ItemListId ili = new ItemListId();
			ili.setItemId(item.getId());
			ili.setLegalEntity(legalEntity);
			ili.setReportId(rinfo.getId());
			saveList.add(ili);
		}
		itemListIdService.save(saveList);
		batman.removeAttribute("itemList");
		batman.removeAttribute("itemidmap");
		byte[] array = bos.toByteArray();
		out.write(array, 0, array.length);
		response.setContentType("application/xml; charset=utf-8");
	}

	@RequestMapping(value = ApiValues.ITEM, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getItemData(@PathVariable String id,
			@PathVariable String legalEntity, @PathVariable String date,
			@PathVariable String runId,
			HttpServletResponse response,  HttpServletRequest request) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		List<Item> itemDataList;
		logger.error("DT1"+date);
		Map<String, String> itemidmap = new HashMap<String, String>();
		if ("none".equalsIgnoreCase(runId)) {
			HttpSession batman = request.getSession(false);
			if(batman.getAttribute("itemList") != null)
				itemDataList = (List<Item>)batman.getAttribute("itemList");
			else
				itemDataList = itemservice.getItemData(id, legalEntity, date, runId);
			if(batman.getAttribute("itemidmap") != null){
				itemidmap = (Map<String, String>)batman.getAttribute("itemidmap");
			}
			else{
				List<ItemListId> itemidlist;
				itemidlist = itemListIdService.getAll();
				itemidmap = convertListToMap(itemidlist);
			}
			itemDataList = getListData(itemDataList);
			logger.error("DT2"+date);
			logger.error("MK2"+itemDataList.size());
			itemDataList = getListFinalData(itemDataList,date);
			logger.error("MK1"+itemDataList.size());
			List<Item> tempList = new ArrayList<Item>();
			tempList.addAll(itemDataList);
			
			if (tempList != null) { 
				for (Item item : tempList) {
					String key = matrixKeyMaker(String.valueOf(item.getId()), legalEntity.toLowerCase());
					if(itemidmap.get(key) != null && itemidmap.get(key).equalsIgnoreCase("x")){
						itemDataList.remove(item);
					}
				}
			}
		} else {
			ReportInfo obj = reportInfoService.getById(Long.parseLong(runId));
			if(date != null && !date.equalsIgnoreCase("none")){
				itemDataList = itemservice.getItemData(obj.getCriteriaId(), obj.getLegalEntity(), obj.getDate(), runId);
			}
			else{
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				String date2 = formatter.format(obj.getCreatedDate());
				StringBuilder sb = new StringBuilder();
				sb.append("15-08-1947-");
				sb.append(date2);
				itemDataList = itemservice.getItemData(obj.getCriteriaId(), obj.getLegalEntity(), sb.toString(), runId);
			}
		
			itemDataList = getListData(itemDataList);
			itemDataList = getListFinalData(itemDataList,date);
		}
		List<JsonObject> jsonList = new ArrayList<JsonObject>();
		getJsonList(jsonList,itemDataList, legalEntity);
		response.setContentType("application/json; charset=utf-8");
		return gson.toJson(jsonList);
	}

	@RequestMapping(value = ApiValues.OVERVIEWSCREEN_TTEMS, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getItemOverview(@PathVariable String id,
			@PathVariable String legalEntity, @PathVariable String date,
			HttpServletResponse response, HttpServletRequest request)
			throws IOException {
		logger.error("item time marker 0: "+new Date(System.currentTimeMillis()));
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		logger.error("DT5"+date);
		List<Item> itemDataList;
		itemDataList = itemservice.getItemData(id, legalEntity, date, "none");
		List<ItemListId> itemidlist;
		itemidlist = itemListIdService.getAll();
		Map<String, String> itemidmap = new HashMap<String, String>();
		itemidmap = convertListToMap(itemidlist);
		HttpSession batman = request.getSession(true);
		batman.setAttribute("itemList", itemDataList);
		batman.setAttribute("itemidmap", itemidmap);
		List<OverviewScreen> overviewScreenList = new ArrayList<OverviewScreen>();
		itemDataList = getListData(itemDataList);
		logger.error("DT4"+date);
		itemDataList = getListFinalData(itemDataList,date);
		createOverviewList(itemDataList, overviewScreenList, legalEntity, itemidmap);
		logger.error("item time marker 3: "+new Date(System.currentTimeMillis()));
		response.setContentType("application/json; charset=utf-8");
		return gson.toJson(overviewScreenList);
	}

	private void createOverviewList(List<Item> itemDataList,
			List<OverviewScreen> overviewScreenList, String legalEntity, Map<String, String> itemidmap) {

		List<Item> itemDataListTsol = new ArrayList<Item>();
		List<Item> itemDataListTdE = new ArrayList<Item>();
		List<Item> itemDataListTME = new ArrayList<Item>();
		itemDataListTsol.addAll(itemDataList);
		itemDataListTdE.addAll(itemDataList);
		itemDataListTME.addAll(itemDataList);
		for (Item item : itemDataList) {
			String key = matrixKeyMaker(String.valueOf(item.getId()), "tsol".toLowerCase());
				if (itemidmap.get(key) != null && itemidmap.get(key).equalsIgnoreCase("x")) {
					itemDataListTsol.remove(item);
				}
				key = matrixKeyMaker(String.valueOf(item.getId()), "tde".toLowerCase());
				if (itemidmap.get(key) != null && itemidmap.get(key).equalsIgnoreCase("x")) {
					itemDataListTdE.remove(item);
				}
				key = matrixKeyMaker(String.valueOf(item.getId()), "tme".toLowerCase());
				if (itemidmap.get(key) != null && itemidmap.get(key).equalsIgnoreCase("x")) {
					itemDataListTME.remove(item);
				}
		}

		OverviewScreen overviewScreen1 = new OverviewScreen();
		OverviewScreen overviewScreen2 = new OverviewScreen();
		OverviewScreen overviewScreen3 = new OverviewScreen();
		overviewScreen1.setReportType("Item");
		overviewScreen1.setLegalEntity("Tsol");
		overviewScreen1.setNoUnRepRec(itemDataListTsol.size());
		overviewScreen2.setReportType("Item");
		overviewScreen2.setLegalEntity("TdE");
		overviewScreen2.setNoUnRepRec(itemDataListTdE.size());
		overviewScreen3.setReportType("Item");
		overviewScreen3.setLegalEntity("TME");
		overviewScreen3.setNoUnRepRec(itemDataListTME.size());
		if ("TME".equals(legalEntity) && itemDataListTME.size() > 0) {
			overviewScreenList.add(overviewScreen3);
		} else if ("TdE".equals(legalEntity) && itemDataListTdE.size() > 0) {
			overviewScreenList.add(overviewScreen2);
		} else if ("Tsol".equals(legalEntity) && itemDataListTsol.size() > 0) {
			overviewScreenList.add(overviewScreen1);
		} else if ("none".equals(legalEntity)) {
			if (itemDataListTsol.size() > 0) {
				overviewScreenList.add(overviewScreen1);
			}
			if (itemDataListTdE.size() > 0) {
				overviewScreenList.add(overviewScreen2);
			}
			if (itemDataListTME.size() > 0) {
				overviewScreenList.add(overviewScreen3);
			}
		}

	}

	private List<Item> getListFinalData(List<Item> itemDataList,String date) { 
		logger.error("D5"+date); 
		List<Offering> dataList;
		dataList = newofferingservice.getOfferingData("none", "none", date,
				"none", false);
		List<Item> itemFinalDataList = new ArrayList<Item>();

		for (Item item : itemDataList) {
			if(item.getDelMethod().equalsIgnoreCase("online")){
				itemFinalDataList.add(item);
				continue;
			}
			String itemcode = item.getItemCode();
			boolean flag = false;
			for (Offering offering : dataList) {
				if (offering.getItemCode().equalsIgnoreCase(itemcode)) {
					item.setCreditHoursOnline(offering.getCpeHours());
					flag = true;
					break;
				}
			}
			if (flag) {
				itemFinalDataList.add(item);
			}
		}
		return (itemFinalDataList);

	}

	
	private List<Item> getListData(List<Item> itemDataList) {
		List<Item> itemFinalDataList = new ArrayList<Item>();
		for (Item item : itemDataList) {
			if(item.getLegalEntity() != null){
				if(item.getLegalEntity().equalsIgnoreCase("FT"))
					itemFinalDataList.add(item);
			}
		}
		return itemFinalDataList;
	}
	
	
	private void getJsonList(List<JsonObject> jsonList, List<Item> items, String legalEntity){
		
		for(Item item : items) {
			JsonObject jsonobject = new JsonObject();
			setParameters(jsonobject, item, legalEntity);
			Format formatter = new SimpleDateFormat("dd/MM/yyyy");  
			
			jsonobject.addProperty("id", item.getId());
			if(item.getModifiedOn() != null) {
				String dte=formatter.format(item.getModifiedOn());
				jsonobject.addProperty("modifiedOn", dte);
			}
			if(item.getItemCreatedOn() != null) {
				String dte= formatter.format(item.getItemCreatedOn());
				jsonobject.addProperty("itemCreatedOn",dte);
			}
			jsonobject.addProperty("itemCode", item.getItemCode());
			jsonobject.addProperty("itemCode1", item.getItemCode1());
			jsonobject.addProperty("itemTitle", item.getItemTitle());
			Double itemlen = null;
			if(item.getCreditHoursScheduled() != null && item.getCreditHoursOnline() != null){
				itemlen = (Double)(item.getCreditHoursScheduled().doubleValue() + item.getCreditHoursOnline().doubleValue());
			}
			else if(item.getCreditHoursScheduled() != null){
				itemlen = (Double)(item.getCreditHoursScheduled().doubleValue());
			}
			else if(item.getCreditHoursOnline() != null){
				itemlen = (Double)(item.getCreditHoursOnline().doubleValue());
			}
			if(itemlen != null){
				if(itemlen>itemlen.intValue())
					jsonobject.addProperty("itemLength", itemlen.doubleValue()); 
				else
					jsonobject.addProperty("itemLength", itemlen.intValue());
			}
			jsonobject.addProperty("delMethod", item.getDelMethod());
			jsonobject.addProperty("legalEntity", item.getLegalEntity());
			if(item.getCreditHoursScheduled() != null) {
				jsonobject.addProperty("creditHoursScheduled", item.getCreditHoursScheduled());
			}
			if(item.getCreditHoursOnline() != null) {
				jsonobject.addProperty("creditHoursOnline", item.getCreditHoursOnline());
			}
			jsonobject.addProperty("observations", item.getObservations());
			jsonobject.addProperty("objectives", item.getObjectives());
			jsonobject.addProperty("conIndex", item.getConIndex());
			if(item.getStartDate() != null) {
				String dte=formatter.format(item.getStartDate());
				jsonobject.addProperty("startDate",dte);
			}
			jsonobject.addProperty("difficultyLevel", item.getDifficultyLevel());
			jsonList.add(jsonobject);
		}
		
	}
	
	private void setParameters(JsonObject jsonObject, Item item, String legalEntity) {
		parameterizedList = parameterised.getParametrisedDataByType("Items");
		String taxid = null;
		for(Parametrised param : parameterizedList) {
			if("URISSF".equals(param.getParamName()) || "SSFPassword".equals(param.getParamName()) || "SSFUname".equals(param.getParamName())) {
				if("9".equals(item.getDelMethod()) || "10".equals(item.getDelMethod())){
					jsonObject.addProperty(param.getParamName(), param.getParamValue());
				}
			} else if ("CIFCentreTSOL".equals(param.getParamName()) && "Tsol".equals(legalEntity)) {
				taxid = param.getParamValue();
				jsonObject.addProperty("taxId", taxid);
				
			} else if("TaxCodeTde".equals(param.getParamName()) && "TdE".equals(legalEntity)) {
				taxid = param.getParamValue();
				jsonObject.addProperty("taxId", taxid);	
			} else if("CIFplaceTME".equals(param.getParamName()) && "TME".equals(legalEntity)) {
				taxid = param.getParamValue();
				jsonObject.addProperty("taxId", taxid);
			} else {
				jsonObject.addProperty(param.getParamName(), param.getParamValue());
			}
		}
		
	}
	
	public Map<String, String> convertListToMap(List<ItemListId> list){
		Map<String, String> itemidmap = new HashMap<String, String>();
		for(ItemListId itemid : list){
			String key = matrixKeyMaker(String.valueOf(itemid.getItemId()), itemid.getLegalEntity().toLowerCase());
			itemidmap.put(key,"x");
		}
		return itemidmap;
	}
	
	public String matrixKeyMaker(String string1, String string2){
		StringBuilder key = new StringBuilder(string1);
		key.append("-");
		key.append(string2);
		return key.toString();
	}
}
