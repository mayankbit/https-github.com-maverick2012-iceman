/**
 * 
 */
package com.sap.hcp.successfactors.lms.extensionfw.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.EmployeeListId;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.ItemListId;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.OfferingListId;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.ReportInfo;


/**
 * @author I319792
 *
 */
@Controller
public class ReportInfoServlet {
	
	@Autowired
	ReportInfoService reportInfoService;
	
	@Autowired
	ItemListIdService itemlistid;
	
	@Autowired
	OfferingListIdService offeringlistidservice;
	
	@Autowired
	EmployeeListIdService employeelistidservice;
	
	
	
	/********************** temp code ****************************/
	@RequestMapping(value = ApiValues.DELETE_ME, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void deleteitemlistid(){
		itemlistid.delete();
	}
	
	@RequestMapping(value = ApiValues.DELETE_OFFERING_ME, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void deleteofferinglistid(){
		offeringlistidservice.delete();
	}
	
	
	
	@RequestMapping(value = ApiValues.DELETE_EMPLOYEE, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void deleteemployeelistid(){
		employeelistidservice.delete();
	}
	
	@RequestMapping(value = ApiValues.GET_ALL_EMPLOYEE_LIST_ID, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String gelemployeelistid(HttpServletRequest request, HttpServletResponse response){
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		List<EmployeeListId> merilist = employeelistidservice.getAll();
		response.setContentType("application/json; charset=utf-8");
		return gson.toJson(merilist);
	}

	@RequestMapping(value = ApiValues.GET_ALL_ITEM_LIST_ID, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String gelitemlistid(HttpServletRequest request, HttpServletResponse response){
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		List<ItemListId> merilist = itemlistid.getAll();
		response.setContentType("application/json; charset=utf-8");
		return gson.toJson(merilist);
	}
	
	@RequestMapping(value = ApiValues.GET_ALL_OFFERING_LIST_ID, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getofferinglistid(HttpServletRequest request, HttpServletResponse response){
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		List<OfferingListId> merilist = offeringlistidservice.getAll();
		response.setContentType("application/json; charset=utf-8");
		return gson.toJson(merilist);
	}
	
	@RequestMapping(value = ApiValues.DELETE_REPORT_HISTORY, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void deletereporthistory(){
		reportInfoService.delete();
	}
	/********************** temp code ****************************/
	
	
	@RequestMapping(value = ApiValues.REPORT_INFO, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getReportInfo(@PathVariable String legalEntity,@PathVariable String id, @PathVariable String date,	HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		List<ReportInfo> reportInfo = reportInfoService.getReportByParam(id, legalEntity, date);
		response.setContentType("application/json; charset=utf-8");
		return gson.toJson(reportInfo);
	}
	
	@RequestMapping(value = ApiValues.REPORT_INFO_GET_BY_TYPE, method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getReportInfoByType(@PathVariable String reportType,	HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.registerTypeAdapter(Date.class,
				new UTCDateTypeAdapter()).create();
		List<ReportInfo> reportInfo = reportInfoService.getByReportType(reportType);
		response.setContentType("application/json; charset=utf-8");
		return gson.toJson(reportInfo);
	}
	
	@RequestMapping(value = ApiValues.REPORT_INFO_DELETE_ALL, method = RequestMethod.GET)
	@ResponseBody
	public void deleteReport(){
		reportInfoService.delete();
	}

}
