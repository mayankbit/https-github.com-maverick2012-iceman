package com.sap.hcp.successfactors.lms.extensionfw.service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.Item;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.Offering;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.OfferingListId;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.OverviewScreen;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.Parametrised;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.ReportInfo;
import com.sap.hcp.successfactors.lms.extensionfw.reporting.GenerateReportForNewOffering;

@Controller
public class NewOfferingServlet {
	
	private static final String NEW_OFFERING = "New Offering";
	
	private static final String ONLINE = "ONLINE";
	
	private static final String ONSITE = "ONSITE";
	
	private static final String VIRTUAL = "VIRTUAL";
	
	private static final String NEW_OFFERING_LIST = "newOfferingList";
	
	private static final String TAX_ID_PLATFORM = "taxIdPlatform";

	private static final String ONSITE_TAX_ID_FACILITY = "onsiteTaxIDFacility";
	
	private static final String ONSITE_FACILITY = "onsiteFacility";
	
	private static final String ONSITE_TAX_ID_CODE_LOC = "onSiteTaxIDCodeLoc";
	
	private static final String OFFERING_ID_MAP = "offeringidmap";
	
	@Autowired
	NewOfferingService newofferingservice;

	@Autowired
	ReportStructService reportStructService;

	@Autowired
	TagMappingService tagmappingservice;

	@Autowired
	FileService fileservice;

	@Autowired
	ReportInfoService reportInfoService;

	@Autowired
	ParametrisedService parameterised;

	@Autowired
	OfferingListIdService offeringlistidservice;

	@Autowired
	ItemService itemservice;

	List<Parametrised> parameterizedList = null;
	
	Map<String, Item> mayankkamap = new HashMap<String, Item>();
	
	private static final Logger logger = LoggerFactory.getLogger(NewOfferingServlet.class);


	@RequestMapping(value = ApiValues.NEWOFFERING, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getOfferingData(@PathVariable String id,
			@PathVariable String legalEntity, @PathVariable String date,
			@PathVariable String days, @PathVariable String runId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		List<Offering> dataList = null;
		Map<String, String> offeringidmap = new HashMap<String, String>();
		HttpSession batman = request.getSession(false);
		if("none".equalsIgnoreCase(runId)){
			if(batman.getAttribute(NEW_OFFERING_LIST) != null){
				logger.error("data is coming from session");
				dataList = (List<Offering>)batman.getAttribute(NEW_OFFERING_LIST);
			}
			else{
				logger.error("data is coming from database");
				dataList = newofferingservice.getOfferingData(id, legalEntity, date, days, true);
			}
			if(batman.getAttribute(OFFERING_ID_MAP) != null){
				offeringidmap = (Map<String, String>)batman.getAttribute(OFFERING_ID_MAP);
			}
			else{
				List<OfferingListId> offeringidlist;
				offeringidlist = offeringlistidservice.getAll();
				offeringidmap = convertListToMap(offeringidlist);
			}
			List<Offering> tempList = new ArrayList<Offering>();
			tempList.addAll(dataList);
			if (tempList != null) {
				for (Offering offering : tempList) {
					String key = matrixKeyMaker(String.valueOf(offering.getId()), legalEntity.toLowerCase());
					if(offeringidmap.get(key) != null && offeringidmap.get(key).equalsIgnoreCase("x")){
						dataList.remove(offering);
					}
				}
			}
			tempList = null;
		}
		else{
			List<OfferingListId> offeringListIds = offeringlistidservice.getByReportId(Long.parseLong(runId));
			List<Long> offringIds = new ArrayList<Long>();
			for(OfferingListId offeringListId : offeringListIds) {
				offringIds.add(offeringListId.getOfferingId());
			}
			dataList = newofferingservice.getOfferingByOfferingIds(offringIds);
		}
		Map<String, String> mp = new HashMap<String, String>();
		mp = getItemOfferingListData();
		List<Offering> finalDataList = new ArrayList<Offering>();
		for (Offering offering : dataList) {
			String bool = null;
			bool = mp.get(offering.getItemCode());
			if (bool != null && "true".equals(bool)){
					offering.setItemCode1(mayankkamap.get(offering.getItemCode()).getItemCode1());
					offering.setItemTitle(mayankkamap.get(offering.getItemCode()).getItemTitle());
					logger.error("P1"+offering.getItemCode1());
					logger.error("P2"+offering.getItemCode1());
			}
				finalDataList.add(offering);
		}
		dataList = null;
		List<JsonObject> jsonList = new ArrayList<JsonObject>();
		getJsonList(jsonList,finalDataList, legalEntity);
		response.setContentType("application/json; charset=utf-8");
		return gson.toJson(jsonList);
	}

	@RequestMapping(value = ApiValues.OVERVIEWSCREEN_NEW_OFFERING, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getOfferingOverview(@PathVariable String id,
			@PathVariable String legalEntity, @PathVariable String date,
			@PathVariable String days, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		logger.error("offering time marker 0: "+new Date(System.currentTimeMillis()));
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		List<Offering> dataList;
		dataList = newofferingservice.getOfferingData(id, legalEntity, date,
				days, true);
		Map<String, String> mp = getItemOfferingListData();
		List<Offering> finalDataList = new ArrayList<Offering>();
		for (Offering offer : dataList) {
			logger.error("T1"+offer.getOfferingId());
			logger.error("T2"+offer.getItemCode());
			String bool;
			bool = mp.get(offer.getItemCode());
			if (bool != null && "true".equals(bool)){
//				finalDataList.add(offer);
				logger.error("T3"+offer.getOfferingId());
				logger.error("T4"+offer.getItemCode());
			}
			finalDataList.add(offer);
		}
		HttpSession batman = request.getSession(true);
		batman.setAttribute(NEW_OFFERING_LIST, finalDataList);
		List<OfferingListId> offeringlistid;
		Map<String, String> offeringidmap = new HashMap<String, String>();
		offeringlistid = offeringlistidservice.getAll();
		offeringidmap = convertListToMap(offeringlistid);
		batman.setAttribute(OFFERING_ID_MAP, offeringidmap);
		List<OverviewScreen> overviewScreenList = new ArrayList<OverviewScreen>();
		createOverviewList(finalDataList, overviewScreenList, legalEntity, offeringidmap);
		logger.error("offering time marker 3: "+new Date(System.currentTimeMillis()));
		response.setContentType("application/json; charset=utf-8");
		return gson.toJson(overviewScreenList);
	}

	@RequestMapping(value = ApiValues.NEWOFFERINGREPORT, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void generateNewOfferingReport(@PathVariable String id,
			@PathVariable String legalEntity, @PathVariable String date,
			@PathVariable String days, @PathVariable String runId, HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		ServletOutputStream out = response.getOutputStream();
		parameterizedList = parameterised.getAllParametrisedData();
		List<Offering> dataList = null;
		Map<String, String> offeringidmap = new HashMap<String, String>();
		HttpSession batman = request.getSession(false);
		if("none".equalsIgnoreCase(runId)){
			if(batman.getAttribute(NEW_OFFERING_LIST) != null){
				logger.error("data is coming from session");
				dataList = (List<Offering>)batman.getAttribute(NEW_OFFERING_LIST);
			}
			else{
				logger.error("data is coming from database");
				dataList = newofferingservice.getOfferingData(id, legalEntity, date, days, true);
			}
			if(batman.getAttribute(OFFERING_ID_MAP) != null){
				offeringidmap = (Map<String, String>)batman.getAttribute(OFFERING_ID_MAP);
			}
			else{
				List<OfferingListId> offeringidlist;
				offeringidlist = offeringlistidservice.getAll();
				offeringidmap = convertListToMap(offeringidlist);
			}
			List<Offering> tempList = new ArrayList<Offering>();
			tempList.addAll(dataList);
			if (tempList != null) {
				for (Offering offering : tempList) {
					String key = matrixKeyMaker(String.valueOf(offering.getId()), legalEntity.toLowerCase());
					if(offeringidmap.get(key) != null && offeringidmap.get(key).equalsIgnoreCase("x")){
						dataList.remove(offering);
					}
				}
			}
			tempList = null;
		}
		else{
			//ReportInfo obj = reportInfoService.getById(Long.parseLong(runId));
			List<OfferingListId> offeringListIds = offeringlistidservice.getByReportId(Long.parseLong(runId));
			List<Long> offringIds = new ArrayList<Long>();
			for(OfferingListId offeringListId : offeringListIds) {
				offringIds.add(offeringListId.getOfferingId());
			}
			dataList = newofferingservice.getOfferingByOfferingIds(offringIds);
			/*if(!date.equalsIgnoreCase("none") && date!= null){
				dataList = newofferingservice.getOfferingData(obj.getCriteriaId(), obj.getLegalEntity(), obj.getDate(), runId, true);
			}
			else{
				SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
				String date2 = formatter.format(obj.getCreatedDate());
				StringBuilder sb = new StringBuilder();
				sb.append("15-08-1947-");
				sb.append(date2);
				dataList = newofferingservice.getOfferingData(obj.getCriteriaId(), obj.getLegalEntity(), sb.toString(), runId, true);
			}*/
		}
		Map<String, String> mp = new HashMap<String, String>();
		mp = getItemOfferingListData(); 

		List<Offering> finalDataList = new ArrayList<Offering>();
		for (Offering offer : dataList) {
			String bool = null;
			bool = mp.get(offer.getItemCode());
			if (bool != null && "true".equals(bool))
				
				//change name of map
					offer.setItemCode1(mayankkamap.get(offer.getItemCode()).getItemCode1());
					offer.setItemTitle(mayankkamap.get(offer.getItemCode()).getItemTitle());
				finalDataList.add(offer);
		}

		GenerateReportForNewOffering gr = new GenerateReportForNewOffering();
		Document document = gr.generateXMLReport(finalDataList, parameterizedList,
				legalEntity);

		DOMSource domSource = new DOMSource(document);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		StreamResult result = new StreamResult(bos);
		TransformerFactory tf = TransformerFactory.newInstance();
		/*tf.setAttribute(
				"http://javax.xml.XMLConstants/feature/secure-processing", true);
		tf.setAttribute(
				"http://xml.org/sax/features/external-general-entities", false);
		tf.setAttribute(
				"http://xml.org/sax/features/external-parameter-entities",
				false);*/
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "true");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "4");
			transformer.transform(domSource, result);
		} catch (TransformerException e) {
			logger.error("XML transformation Exception in class NewOfferingServlet --> method generateNewOfferingReport() --> " + e.getMessage());
		}

		ReportInfo rinfo = new ReportInfo();
		rinfo.setDate(date);
		rinfo.setCreatedDate(new Date(System.currentTimeMillis()));
		if (id != null)
			rinfo.setCriteriaId(id);
		if (legalEntity != null)
			rinfo.setLegalEntity(legalEntity);
		rinfo.setReportType(NEW_OFFERING);
		reportInfoService.save(rinfo);
		logger.error("report ingo id" + rinfo.getId());
		//
		List<OfferingListId> saveList = new ArrayList<OfferingListId>();
		for (Offering offering : dataList) {
			OfferingListId oli = new OfferingListId();
			oli.setOfferingId(offering.getId());
			oli.setLegalEntity(legalEntity);
			oli.setReportId(rinfo.getId());
			saveList.add(oli);
		}
		offeringlistidservice.save(saveList);
		//
		batman.removeAttribute(NEW_OFFERING_LIST);
		batman.removeAttribute(OFFERING_ID_MAP);
		byte[] array = bos.toByteArray();

		out.write(array, 0, array.length);

		response.setContentType("application/xml; charset=utf-8");

	}

	private void createOverviewList(List<Offering> offeringDataList,
			List<OverviewScreen> overviewScreenList, String legalEntity, Map<String, String> offeringidmap) {
		List<Offering> offeringTsol = new ArrayList<Offering>();
		List<Offering> offeringTdE = new ArrayList<Offering>();
		List<Offering> offeringTME = new ArrayList<Offering>();
		offeringTsol.addAll(offeringDataList);
		offeringTdE.addAll(offeringDataList);
		offeringTME.addAll(offeringDataList);
		for(Offering offering : offeringDataList) {
			String key = matrixKeyMaker(String.valueOf(offering.getId()), "tsol".toLowerCase());
			if (offeringidmap.get(key) != null && offeringidmap.get(key).equalsIgnoreCase("x")) {
				offeringTsol.remove(offering);
			}
			key = matrixKeyMaker(String.valueOf(offering.getId()), "tde".toLowerCase());
			if (offeringidmap.get(key) != null && offeringidmap.get(key).equalsIgnoreCase("x")) {
				offeringTdE.remove(offering);
			}
			key = matrixKeyMaker(String.valueOf(offering.getId()), "tme".toLowerCase());
			if (offeringidmap.get(key) != null && offeringidmap.get(key).equalsIgnoreCase("x")) {
				offeringTME.remove(offering);
			}
		}
		OverviewScreen overviewScreen1 = new OverviewScreen();
		OverviewScreen overviewScreen2 = new OverviewScreen();
		OverviewScreen overviewScreen3 = new OverviewScreen();
		overviewScreen1.setReportType(NEW_OFFERING);
		overviewScreen1.setLegalEntity("Tsol");
		logger.error("Tsol" + offeringTsol.size());
		overviewScreen1.setNoUnRepRec(offeringTsol.size());
		overviewScreen2.setReportType(NEW_OFFERING);
		overviewScreen2.setLegalEntity("TdE");
		logger.error("TDE" + offeringTsol.size());
		overviewScreen2.setNoUnRepRec(offeringTdE.size());
		overviewScreen3.setReportType(NEW_OFFERING);
		overviewScreen3.setLegalEntity("TME");
		logger.error("TME" + offeringTsol.size());
		overviewScreen3.setNoUnRepRec(offeringTME.size());
		if ("TME".equals(legalEntity) && !offeringTME.isEmpty()) {
			overviewScreenList.add(overviewScreen3);
			//logger.error("mayank4"+ "  "+overviewScreenList.size());
		} else if ("TdE".equals(legalEntity) && !offeringTdE.isEmpty()) {
			overviewScreenList.add(overviewScreen2);
			//logger.error("mayank5"+ "  "+overviewScreenList.size());
		} else if ("Tsol".equals(legalEntity) && !offeringTsol.isEmpty()) {
			overviewScreenList.add(overviewScreen1);
			///logger.error("mayank6"+ "  "+overviewScreenList.size());
		} else if ("none".equals(legalEntity)) {
			if (!offeringTsol.isEmpty()) {
				overviewScreenList.add(overviewScreen1);
			//	logger.error("mayank7"+ "  "+overviewScreenList.size());
			}
			if (!offeringTdE.isEmpty()) {
				overviewScreenList.add(overviewScreen2);
			//	logger.error("mayank8"+ "  "+overviewScreenList.size());
			}
			if (!offeringTME.isEmpty()) {
				overviewScreenList.add(overviewScreen3);
			//	logger.error("mayank9"+ "  "+overviewScreenList.size());
			}
		}
		
		
	}

	@RequestMapping(value = ApiValues.INVALIDNEWOFFERING, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getInvalidOfferingData(@PathVariable String code,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		List<Parametrised> param = parameterised.getAllParametrisedData();
		List<Offering> dataList;
		dataList = newofferingservice.getInvalidOfferingData(code, param);
	//	logger.error("sp1"+"  "+dataList.size());
		Map<String, String> mp = new HashMap<String, String>();
		mp = getItemOfferingListData();
		//logger.error("L4"+mp.size());
		List<Offering> finalDataList = new ArrayList<Offering>();
		for (Offering offering : dataList) {
			String bool = null;
			bool = mp.get(offering.getItemCode());
		//	logger.error("sp2"+"  "+bool);
			if (bool != null && "true".equals(bool)){
				offering.setItemCode1(mayankkamap.get(offering.getItemCode()).getItemCode1());
				offering.setItemTitle(mayankkamap.get(offering.getItemCode()).getItemTitle());
			}
				finalDataList.add(offering);
		}
		dataList = null;
		//logger.error("sp3"+"  "+finalDataList.size());
		response.setContentType("application/json; charset=utf-8");
		return gson.toJson(finalDataList);
	}

	private Map<String, String> getItemOfferingListData() {
		// by default " FT" legal entity is being passed
		//logger.error("dps1");
		List<Item> itemDataList = itemservice.getItemData("none", "none", "none", "none");
	//	logger.error("L1"+itemDataList.size());
		Map<String, String> mp = new HashMap<String, String>();
		for (Item item : itemDataList) {
			logger.error("L2"+item.getItemCode());
			mp.put((String)item.getItemCode(), "true");
			mayankkamap.put((String)item.getItemCode(), item);
		}
		logger.error("L3"+mp.size());
		return mp;

	}

	private void getJsonList(List<JsonObject> jsonList, List<Offering> offers, String legalEntity) {

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		for (Offering offer : offers) {
			JsonObject jsonobject = new JsonObject();
			setParameters(jsonobject, offer, legalEntity);
			Calendar meraCalendar = Calendar.getInstance();
			Format formatter = new SimpleDateFormat("dd/MM/yyyy");  
			DateFormat formtCet=new SimpleDateFormat("HH:mm");
			formtCet.setTimeZone(TimeZone.getTimeZone("CET"));
			DateFormat formt=new SimpleDateFormat("HH:mm");

			jsonobject.addProperty("id", offer.getId());
			jsonobject.addProperty("legalEntity", offer.getLegalEntity());
			jsonobject.addProperty("itemCode", offer.getItemCode());
			jsonobject.addProperty("itemCode1", offer.getItemCode1());
			jsonobject.addProperty("itemTitle", offer.getItemTitle());
			if (offer.getOfferingStartDate() != null) {
				meraCalendar.setTime(offer.getOfferingStartDate());
				int year = meraCalendar.get(Calendar.YEAR);
				int month = (meraCalendar.get(Calendar.MONTH)+1);
				int date = meraCalendar.get(Calendar.DATE);
				StringBuilder sb = new StringBuilder();
				sb.append(date+"/"+month+"/"+year);
				jsonobject.addProperty("offeringStartDate",sb.toString());
			}
			if (offer.getOfferingEndDate() != null) {
				meraCalendar.setTime(offer.getOfferingEndDate());
				int year = meraCalendar.get(Calendar.YEAR);
				int month = (meraCalendar.get(Calendar.MONTH)+1);
				int date = meraCalendar.get(Calendar.DATE);
				StringBuilder sb = new StringBuilder();
				sb.append(date+"/"+month+"/"+year);
				jsonobject.addProperty("offeringEndDate",sb.toString());
			}
			jsonobject.addProperty("deliveryMethod", offer.getDeliveryMethod());
			jsonobject.addProperty("scheduleDesc", offer.getItemTitle());
			jsonobject.addProperty("numberOfParticipants",offer.getNumberOfParticipants());
			jsonobject.addProperty("scheduleOfferingContact",
					offer.getScheduleOfferingContact());
			if(!ONLINE.equals(offer.getDeliveryMethod())) {
				jsonobject.addProperty("facilityName", offer.getFacilityName());
				jsonobject.addProperty("facilityComments", offer.getFacilityComments());
				jsonobject.addProperty("facilityDesc", offer.getFacilityDesc());
				jsonobject.addProperty("locationName", offer.getLocationName());
				jsonobject.addProperty("facilityAddress",
						offer.getFacilityAddress());
				jsonobject.addProperty("facilityPostal", offer.getFacilityPostal());
				jsonobject.addProperty("facilityCity", offer.getFacilityCity());
				jsonobject.addProperty("facilityCountry",
						offer.getFacilityCountry());
				
				jsonobject.addProperty("creditHours", offer.getCreditHours());
				if (offer.getFirstDayMorningStartDateTime() != null) {
					String dte=formtCet.format(offer.getFirstDayMorningStartDateTime());
					jsonobject.addProperty("firstDayMorningStartDateTime",dte);
				}
				if (offer.getFirstDayMorningEndDateTime() != null) {
					String dte;
				//	if(offer.isCetFlag()) 
					{
						dte=formtCet.format(offer.getFirstDayMorningEndDateTime());
					}
//					//else 
//					{
//						dte=formtCet.format(offer.getFirstDayMorningEndDateTime());
//					}
					jsonobject.addProperty("firstDayMorningEndDateTime",dte);
					
				}
				if (offer.getFirstDayAfternoonStartDateTime() != null) {
					String dte=formtCet.format(offer
							.getFirstDayAfternoonStartDateTime());
					jsonobject.addProperty("firstDayAfternoonStartDateTime",dte);
				}

				if (offer.getFirstDayAfternoonEndDateTime() != null) {
					String dte=formtCet.format(offer
							.getFirstDayAfternoonEndDateTime());
					jsonobject.addProperty("firstDayAfternoonEndDateTime",
							dte);
				}
				jsonobject.addProperty("daysOfTeaching", offer.getDaysOfTeaching());
			}
			if("ONLINE".equals(offer.getDeliveryMethod()) || "BLENDED".equals(offer.getDeliveryMethod())) {
				JsonElement element=gson.toJsonTree(offer.getInstructor());
				jsonobject.add("instructors", element);
				if (offer.getOnlineFirstDayMorningStartDateTime() != null) {
					String dte=formtCet.format(offer.getOnlineFirstDayMorningStartDateTime());
					jsonobject.addProperty("onlineFirstDayMorningStartDateTime",dte);
				}
				if (offer.getOnlineFirstDayMorningEndDateTime() != null) {
					String dte = formtCet.format(offer.getOnlineFirstDayMorningEndDateTime());
					jsonobject
					.addProperty("onlineFirstDayMorningEndDateTime", dte);
				}
				if (offer.getFirstDayAfternoonStartDateTime() != null) {
					String dte=formtCet.format(offer
							.getFirstDayAfternoonStartDateTime());
					jsonobject.addProperty("onlineFirstDayAfternoonStartDateTime",dte);
				}

				if (offer.getFirstDayAfternoonEndDateTime() != null) {
					String dte=formtCet.format(offer
							.getFirstDayAfternoonEndDateTime());
					jsonobject.addProperty("onlineFirstDayAfternoonEndDateTime",
							dte);
				}
				jsonobject.addProperty("onlineDaysOfTeaching", offer.getDaysOfTeaching());
				jsonobject.addProperty("observations", offer.getObservations());
			}

			if (offer.getCreationDate() != null) {
				String dte=formatter.format(offer.getCreationDate());
				jsonobject.addProperty("creationDate",dte);
			}
			
			jsonobject.addProperty("hoursPerInstructor",
					offer.getHoursPerInstructor());
			jsonobject.addProperty("instructorID", offer.getInstructorID());

			jsonobject.addProperty("instructorFName",
					offer.getInstructorFName());
			jsonobject.addProperty("instructorLName",
					offer.getInstructorLName());
			jsonobject.addProperty("instructorMName",
					offer.getInstructorMName()); 
			Double offerLength=offer.getLengthOfOffering();
			if(offerLength>offerLength.intValue())
				jsonobject.addProperty("lengthOfOffering",offer.getLengthOfOffering());
			else
				jsonobject.addProperty("lengthOfOffering",offerLength.intValue());

			if (offer.getOfferingCloseDate() != null) {
				String dte=formatter.format(offer.getOfferingCloseDate());
				jsonobject.addProperty("offeringCloseDate",dte);
			}
			if (offer.getOfferingCancelledDate() != null) {
				String dte=formatter.format(offer.getOfferingCancelledDate());
				jsonobject.addProperty("offeringCancelledDate",dte
						);
			}
			jsonobject.addProperty("userProvidedValue",
					offer.getUserProvidedValue());
			jsonobject.addProperty("columnNo", offer.getColumnNo());
			jsonobject.addProperty("totalCount", offer.getTotalCount());
			jsonobject.addProperty("customData", offer.getCustomData());
			
			jsonobject.addProperty("cpeHours", offer.getCpeHours());
			jsonobject.addProperty("contactHours", offer.getContactHours());
			jsonobject.addProperty("offeringid", offer.getOfferingId());
			jsonobject.addProperty("itemSecondaryID", offer.getItemSecondaryID());
			if("true".equals(offer.getInterInsIndicator())) {
				//ogger.error("white4"+offer.getItemCode1());
				jsonobject.addProperty("interInsIndicator", "Propios");
			} else {
				//logger.error("white5"+offer.getItemCode1());
				jsonobject.addProperty("interInsIndicator", "Centro");
			}
			//jsonobject.addProperty("interInstructor", offer.getExtInsIndicator());
			
			if (offer.getUpdatedOn() != null) {
				String dte=formatter.format(offer.getUpdatedOn());
				jsonobject.addProperty("updatedOn",dte
						);
			}
			jsonList.add(jsonobject);
		}
	}

	private void setParameters(JsonObject jsonObject, Offering offer, String legalEntity) {

		parameterizedList = parameterised
				.getParametrisedDataByType(NEW_OFFERING);

		for (Parametrised parameter : parameterizedList) {
			if ("ResPersonNamePf".equals(parameter.getParamName()) || "AddressFacilityPf".equals(parameter.getParamName())
					|| "AddressPFPOCode".equals(parameter.getParamName()) || "AddressPFCity".equals(parameter.getParamName())
					|| "AddressPFCountry".equals(parameter.getParamName()) || "AddressPFTelephone".equals(parameter.getParamName())) {
				if(ONLINE.equals(offer.getDeliveryMethod()) || "BLENDED".equals(offer.getDeliveryMethod())) {
					jsonObject.addProperty(parameter.getParamName(), parameter.getParamValue());
				}
				
			} else	if(!ONLINE.equals(offer.getDeliveryMethod())) {
				if("Tsol".equals(legalEntity) && "TaxIDFacilityTsol".equals(parameter.getParamName())) {
					jsonObject.addProperty(ONSITE_TAX_ID_FACILITY, parameter.getParamValue());
				} else if("TdE".equals(legalEntity) && "TaxIDFacilityTdE".equals(parameter.getParamName())) {
					jsonObject.addProperty(ONSITE_TAX_ID_FACILITY, parameter.getParamValue());
				} else if("TME".equals(legalEntity) && "TaxIDFacilityTME".equals(parameter.getParamName())) {
					jsonObject.addProperty(ONSITE_TAX_ID_FACILITY, parameter.getParamValue());
				} else if ("Tsol".equals(legalEntity) && "nombreCentroTsol".equals(parameter.getParamName())) {
					jsonObject.addProperty(ONSITE_FACILITY, parameter.getParamValue());
				} else if ("TdE".equals(legalEntity) && "nombreCentroTdE".equals(parameter.getParamName())) {
					jsonObject.addProperty(ONSITE_FACILITY, parameter.getParamValue());
				} else if("TME".equals(legalEntity) && "nombreCentroTME".equals(parameter.getParamName())) {
					jsonObject.addProperty(ONSITE_FACILITY, parameter.getParamValue());
				} else if("Tsol".equals(legalEntity) && "TaxIDCodeLocTsol".equals(parameter.getParamName())) {
					jsonObject.addProperty(ONSITE_TAX_ID_CODE_LOC, parameter.getParamValue());
				} else if("TdE".equals(legalEntity) && "TaxIDCodeLocTdE".equals(parameter.getParamName())) {
					jsonObject.addProperty(ONSITE_TAX_ID_CODE_LOC, parameter.getParamValue());
				} else if("TME".equals(legalEntity) && "TaxIDCodeLocTME".equals(parameter.getParamName())) {
					jsonObject.addProperty(ONSITE_TAX_ID_CODE_LOC, parameter.getParamValue());
				} else {
					jsonObject.addProperty(parameter.getParamName(), parameter.getParamValue());
				}
			} else if (ONLINE.equals(offer.getDeliveryMethod()) || "BLENDED".equals(offer.getDeliveryMethod())) {
				if("Tsol".equals(legalEntity) && "TaxIdPlatformTsol".equals(parameter.getParamName())) {
					jsonObject.addProperty(TAX_ID_PLATFORM, parameter.getParamValue());
				} else if("TdE".equals(legalEntity) && "TaxIdPlatformTdE".equals(parameter.getParamName())) {
					jsonObject.addProperty(TAX_ID_PLATFORM, parameter.getParamValue());
				} else if("TME".equals(legalEntity) && "TaxIdPlatformTME".equals(parameter.getParamName())) {
					jsonObject.addProperty(TAX_ID_PLATFORM, parameter.getParamValue());
				}
			}
		}

	}
	
	public Map<String, String> convertListToMap(List<OfferingListId> list){
		Map<String, String> offeringidmap = new HashMap<String, String>();
		for(OfferingListId offeringid : list){
			String key = matrixKeyMaker(String.valueOf(offeringid.getOfferingId()), offeringid.getLegalEntity().toLowerCase());
			offeringidmap.put(key,"x");
		}
		return offeringidmap;
	}
	
	public String matrixKeyMaker(String string1, String string2){
		StringBuilder key = new StringBuilder(string1);
		key.append("-");
		key.append(string2);
		return key.toString();
	}
	

}
