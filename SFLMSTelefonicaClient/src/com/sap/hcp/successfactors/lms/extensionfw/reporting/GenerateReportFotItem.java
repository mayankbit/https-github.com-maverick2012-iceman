/**
 *  This class will generate report for Items
 */
package com.sap.hcp.successfactors.lms.extensionfw.reporting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sap.hcp.successfactors.lms.extensionfw.pojo.Item;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.Parametrised;

/**
 * @author I319792
 *
 */
public class GenerateReportFotItem {

	private Document document;
	private DocumentBuilder db;
	
	private Logger itemLogger = LoggerFactory.getLogger(GenerateReportFotItem.class);
	
	Map<String, String> parameter = new HashMap<String, String>();
	private String taxid = "";
	
	/**
	 * This method will generate report for items passed as data list.
	 * 
	 * @param datalist
	 * @param parameterizedList
	 * @param legalEntity
	 * @return Item report in XML document
	 */
	public Document generateXMLReport(List<Item> datalist,
			List<Parametrised> parameterizedList, String legalEntity) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		for(Parametrised parametrised : parameterizedList) {
			parameter.put(parametrised.getParamName(), parametrised.getParamValue());
		}
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			itemLogger.error("Parsing exception in class GenerateReportFotItem of method generateXMLReport --> " + e.getMessage());
		}

		document = db.newDocument();
		Element rootNode = document.createElement("AccionesFormativas");
		rootNode.setAttribute("xmlns", "http://www.fundaciontripartita.es/schemas");
		document.appendChild(rootNode);

		for(Item entry : datalist) {
			xmlForitem(entry, rootNode, legalEntity);
		}
		
		return document;
	}
	
	/**
	 * This method will fill all the XML tags for a single item.
	 * 
	 * @param item
	 * @param rootNode
	 * @param legalEntity
	 */
	private void xmlForitem(Item item, Element rootNode, String legalEntity) {
		
		if("Tsol".equals(legalEntity)) {
			taxid = parameter.get("CIFCentreTSOL");
		}
		if("TdE".equals(legalEntity)) {
			taxid = parameter.get("TaxCodeTde");
		}
		if("TME".equals(legalEntity)) {
			taxid = parameter.get("CIFplaceTME");
		}
		if(document!=null){
		Element trainingAction = document.createElement("AccionFormativa");
		rootNode.appendChild(trainingAction);
		
		Element itemCode = document.createElement("codAccion");
		if(item.getItemCode() != null) {
			itemCode.appendChild(document.createTextNode(item.getItemCode1()));
		}
		trainingAction.appendChild(itemCode);
		
		Element itemTitle = document.createElement("nombreAccion");
		if(item.getItemTitle() != null) {
			itemTitle.appendChild(document.createTextNode(item.getItemTitle()));
		}
		trainingAction.appendChild(itemTitle);
		
		Element offeringCode = document.createElement("codGrupoAccion");
		if(parameter.get("OfferingCode") != null) {
			offeringCode.appendChild(document.createTextNode(parameter.get("OfferingCode")));
		}
		trainingAction.appendChild(offeringCode);
		
		Element simpleMode = document.createElement("modalidadSimple");
		trainingAction.appendChild(simpleMode);
		
		Element selfStudyHrs = document.createElement("horas");
		
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
				selfStudyHrs.appendChild(document.createTextNode(String.valueOf(itemlen)));
			else
				selfStudyHrs.appendChild(document.createTextNode(String.valueOf(itemlen.intValue())));
		}
		simpleMode.appendChild(selfStudyHrs);
		
		Element deliveryMethod = document.createElement("modalidad");
		deliveryMethod.appendChild(document.createTextNode(item.getDelMethod()));
		simpleMode.appendChild(deliveryMethod);
		
		if("9".equals(item.getDelMethod().trim())||"10".equals(item.getDelMethod())) {
			Element mixedMode = document.createElement("modalidadMixta");
			trainingAction.appendChild(mixedMode);   

			if(item.getCreditHoursScheduled()!=null){
				Element creditHours = document.createElement("horasPr");
				Double creditHrs=item.getCreditHoursScheduled();
				if(creditHrs>creditHrs.intValue())    
					creditHours.appendChild(document.createTextNode(String.valueOf(creditHrs)));
				else
					creditHours.appendChild(document.createTextNode(String.valueOf(creditHrs.intValue())));

				mixedMode.appendChild(creditHours);
			}
			if(item.getCreditHoursOnline()!=null){
				Element contactHours = document.createElement("horasTe");
				Double contactHrs=item.getCreditHoursOnline();
				if(contactHrs>contactHrs.intValue())
					contactHours.appendChild(document.createTextNode(String.valueOf(contactHrs)));
				else
					contactHours.appendChild(document.createTextNode(String.valueOf(contactHrs.intValue())));
			
			mixedMode.appendChild(contactHours);
			}
		}

		
		
		if("10".equals(item.getDelMethod())||"9".equals(item.getDelMethod())) {
			Element uRL = document.createElement("uri");
			if(parameter.get("URISSF") != null) {
				uRL.appendChild(document.createTextNode(parameter.get("URISSF")));
			}
			trainingAction.appendChild(uRL);
			
			Element user = document.createElement("usuario");
			if(parameter.get("SSFUname") != null) {
				user.appendChild(document.createTextNode(parameter.get("SSFUname")));
			}
			trainingAction.appendChild(user);
			
			Element password = document.createElement("password");
			if(parameter.get("SSFPassword") != null) {
				password.appendChild(document.createTextNode(parameter.get("SSFPassword")));
			}
			trainingAction.appendChild(password);
		}

		
		
		if(item.getObservations() != null) {
			Element observations = document.createElement("observaciones");
			observations.appendChild(document.createTextNode(item.getObservations()));
			trainingAction.appendChild(observations);
		}
		
		if(parameter.get("TypeOfItem") != null) {
			Element typeOfParam = document.createElement("tipoAccion");
			typeOfParam.appendChild(document.createTextNode(parameter.get("TypeOfItem")));
			trainingAction.appendChild(typeOfParam);
		}
		
		if(item.getDifficultyLevel() != null) {
			Element difficultyLevel = document.createElement("nivelFormacion");
			difficultyLevel.appendChild(document.createTextNode(item.getDifficultyLevel()));
			trainingAction.appendChild(difficultyLevel);
		}
		
		if(item.getObjectives() != null) {
			Element goals = document.createElement("objetivos");
			goals.appendChild(document.createTextNode(item.getObjectives()));
			trainingAction.appendChild(goals);
		}
		
		if(item.getConIndex() != null) {
			Element userMaterial = document.createElement("contenidos");
			userMaterial.appendChild(document.createTextNode(item.getConIndex()));
			trainingAction.appendChild(userMaterial);
		}
		
		Element participants = document.createElement("empParticipantes");
		trainingAction.appendChild(participants);
		
		Element taxID = document.createElement("cif");
		if(taxid != null) {
			taxID.appendChild(document.createTextNode(taxid));
		}
		participants.appendChild(taxID);
		
		Element tradeUnion = document.createElement("infRLT");
		participants.appendChild(tradeUnion);
		
		Element informTU = document.createElement("informaRLT");
		if(parameter.get("TradeUnionAgr") != null) {
			informTU.appendChild(document.createTextNode(parameter.get("TradeUnionAgr")));
		}
		tradeUnion.appendChild(informTU);
		
		Element informTUV = document.createElement("informe");
		tradeUnion.appendChild(informTUV);
		
		Element confirmTU = document.createElement("valorinf");
		if(parameter.get("TradeUnionRep") != null) {
			confirmTU.appendChild(document.createTextNode(parameter.get("TradeUnionRep")));
		}
		informTUV.appendChild(confirmTU);
		}
	}
}
