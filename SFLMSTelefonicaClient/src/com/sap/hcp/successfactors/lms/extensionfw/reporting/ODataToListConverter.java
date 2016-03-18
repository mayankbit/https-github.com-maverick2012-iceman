package com.sap.hcp.successfactors.lms.extensionfw.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hcp.successfactors.lms.extensionfw.pojo.Instructor;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.Item;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.Offering;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.OfferingParticipant;

public class ODataToListConverter {
	
	private static final Logger logger = LoggerFactory.getLogger(ODataToListConverter.class);

	public static List<Item> convertToItemList(ODataFeed feed) {
		List<Item> itemList = new ArrayList<Item>();
		if (feed != null) {
			for (ODataEntry entry : feed.getEntries()) {
				Item item = oDataEntryToItemData(entry);
				itemList.add(item);
			}
		}
		return itemList;
	}

	public static Item oDataEntryToItemData(ODataEntry entry) {
		Item itemData = new Item();

		Double totalHours = null;
		Double scheduledHr = null;
		Double creditHours = null;
		

		Map<String, Object> entryDetails = entry.getProperties();
		
		if (entryDetails.get("TotalHours") != null) {
			totalHours = (Double)entryDetails.get("TotalHours");
		}
		if (entryDetails.get("ScheduledHr") != null) {
			scheduledHr = (Double) entryDetails.get("ScheduledHr");
		}
		if (entryDetails.get("CreditHours") != null) {
			creditHours = (Double) entryDetails.get("CreditHours");
		}

		String difficultyLevel = (String) entryDetails.get("DifficultyLevel");
		Calendar date = (Calendar) entryDetails.get("ItemCreatedOn");
		itemData.setId((Long) entryDetails.get("Id"));
		Calendar updatedOn = (Calendar) entryDetails.get("UpdatedOn");
		itemData.setModifiedOn(updatedOn.getTime());
		// itemData.setModifiedByUser((String)
		// entryDetails.get("ModifiedByUser"));
		itemData.setItemCode((String) entryDetails.get("ItemCode"));
		itemData.setItemCode1((String) entryDetails.get("ItemCode1"));
		itemData.setItemTitle((String) entryDetails.get("ItemTitle"));
		itemData.setItemLength(totalHours);
		itemData.setDelMethod((String) entryDetails.get("DeliveryMethod"));
		itemData.setCreditHoursScheduled(creditHours);
		//field will come from offering cpe hrs
		itemData.setCreditHoursOnline(totalHours);
		itemData.setObservations((String) entryDetails.get("InstructorMaterials"));
		itemData.setObjectives((String) entryDetails.get("ComponentGoals"));
		itemData.setConIndex((String) entryDetails.get("Comments"));
		itemData.setItemCreatedOn(date.getTime());
		itemData.setLegalEntity((String) entryDetails.get("LegalEntity"));
		if("BASIC".equals(difficultyLevel)) {
			itemData.setDifficultyLevel("0");
		} /*else if("MEDIUM".equals(difficultyLevel) || "ADVANCED".equals(difficultyLevel)) {
			itemData.setDifficultyLevel("1");
		}*/ else {
			itemData.setDifficultyLevel("1");
		}
		
		return itemData;
	}

	public static List<Offering> convertToOfferingList(ODataFeed feed) {
		List<Offering> offeringList = new ArrayList<Offering>();
		if (feed != null) {
			for (ODataEntry entry : feed.getEntries()) {
				Offering offering = oDataEntryToOfferingData(entry);
				offeringList.add(offering);
			}
		}
		return offeringList;
	}

	public static Offering oDataEntryToOfferingData(ODataEntry entry) {
		Offering offeringData = new Offering();
		Map<String, Object> entryDetails = entry.getProperties();
		
		Calendar creationDate = (Calendar) entryDetails.get("CreationDate");
		Calendar startDate = (Calendar) entryDetails.get("OfferingStartDate");
		Calendar endDate = (Calendar) entryDetails.get("OfferingEndDate");
		Calendar cancelledDate=(Calendar) entryDetails.get("OfferingCancelledDate");
		Calendar closeDate=(Calendar) entryDetails.get("OfferingCloseDate");
				
		
		Calendar morningStartDate = (Calendar) entryDetails.get("FirstDayMorningStartDateTime");
		Calendar morningEndDate = (Calendar) entryDetails.get("FirstDayMorningEndDateTime");
		Calendar afterNoonStartDate = (Calendar) entryDetails.get("FirstDayAfternoonStartDateTime");
		Calendar afterNoonEndDate = (Calendar) entryDetails.get("FirstDayAfternoonEndDateTime");
		Calendar updatedOn=(Calendar) entryDetails.get("updatedOn");
//		Calendar morning
		offeringData.setId((Long) entryDetails.get("Id"));
		offeringData.setOfferingCode((Integer)entryDetails.get("OfferingCode"));
		offeringData.setItemCode((String) entryDetails.get("ItemCode"));
		offeringData.setContactHours((Double)entryDetails.get("ContactHours"));
		offeringData.setCpeHours((Double) entryDetails.get("CpeHours"));
		offeringData.setCreditHours((Double) entryDetails.get("CreditHours"));
		//offeringData.setDaysOfTeaching((Double) entryDetails.get("DaysOfTeaching"));
		Calendar tempStartDate = (Calendar) startDate.clone();
		Calendar tempEndDate = (Calendar) endDate.clone();
		if(tempStartDate != null || tempEndDate != null) {
			offeringData.setDaysOfTeaching(OfferingUtil.getWorkingDate(tempStartDate, tempEndDate));
		}
		offeringData.setLengthOfOffering((Double) entryDetails.get("LengthOfOffering"));
		offeringData.setNumberOfParticipants((Integer) entryDetails.get("NumberOfParticipants"));
		offeringData.setTotalCount((Integer) entryDetails.get("TotalCount"));
		offeringData.setCreationDate(creationDate.getTime());
		if(updatedOn!=null){
		offeringData.setUpdatedOn(updatedOn.getTime());
		}
		offeringData.setDeliveryMethod((String) entryDetails.get("DeliveryMethod"));
		offeringData.setFacilityAddress((String) entryDetails.get("FacilityAddress"));
		offeringData.setFacilityCity((String) entryDetails.get("FacilityCity"));
		offeringData.setFacilityCountry((String) entryDetails.get("FacilityCountry"));
		offeringData.setFacilityName((String) entryDetails.get("FacilityName"));
		offeringData.setFacilityPostal((String) entryDetails.get("FacilityPostal"));
		offeringData.setFacilityDesc((String) entryDetails.get("FacilityDesc"));
		offeringData.setFacilityComments((String) entryDetails.get("FacilityComments"));
		if(entryDetails.get("ScheduleOfferingContact") != null)
		offeringData.setScheduleOfferingContact((String) entryDetails.get("ScheduleOfferingContact"));
		if(cancelledDate!=null){
			offeringData.setOfferingCancelledDate(cancelledDate.getTime());
		}
		if(closeDate!=null){
			offeringData.setOfferingCloseDate(closeDate.getTime());
		}
		if(afterNoonEndDate!= null) {
			//afterNoonEndDate.add(Calendar.HOUR, 2);
			offeringData.setFirstDayAfternoonEndDateTime(afterNoonEndDate.getTime());
		}
		if(afterNoonStartDate!= null){
			//afterNoonStartDate.add(Calendar.HOUR, 2);
			offeringData.setFirstDayAfternoonStartDateTime(afterNoonStartDate.getTime());
		}
		if(morningEndDate!= null) {
			if(afterNoonStartDate == null) {
				Calendar c = (Calendar) morningEndDate.clone();
				c.set(Calendar.HOUR, 15);
				if(c.before(morningEndDate)){
					offeringData.setFirstDayMorningEndDateTime(c.getTime());
					Calendar c1 = (Calendar) c.clone();
					c1.add(Calendar.MINUTE, 1);
					offeringData.setFirstDayAfternoonStartDateTime(c1.getTime());
					offeringData.setFirstDayAfternoonEndDateTime(morningEndDate.getTime());
					offeringData.setCetFlag(true);
				} else {
					offeringData.setFirstDayMorningEndDateTime(morningEndDate.getTime());
				}
			} else {
				offeringData.setFirstDayMorningEndDateTime(morningEndDate.getTime());
			}
			
			//morningEndDate.add(Calendar.HOUR, 2);
			
			
		}
		if(morningStartDate!= null) {
			//morningStartDate.add(Calendar.HOUR, 2);
			offeringData.setFirstDayMorningStartDateTime(morningStartDate.getTime());
			
		}
	
		if("ONLINE".equals(offeringData.getDeliveryMethod())){
			offeringData.setNumberOfParticipants(80);
		} else {
			offeringData.setNumberOfParticipants(25);
		}
		/*if(!"ONSITE".equals(offeringData.getDeliveryMethod())){
			String hoursPerInst = (String) entryDetails.get("HoursPerInstructor");
			if(hoursPerInst != null) {
				offeringData.setHoursPerInstructor(OfferingUtil.getHour(hoursPerInst));
			}
			String intOrExt = (String) entryDetails.get("InterInsIndicator");
			if("yes".equals(intOrExt)) {
				offeringData.setInstructorID((String) entryDetails.get("InstructorID"));
			} else {
				offeringData.setInstructorID((String) entryDetails.get("InstructorID"));
			}
			offeringData.setInstructorFName((String) entryDetails.get("InstructorFName"));
			offeringData.setInstructorLName((String) entryDetails.get("InstructorLName"));
			offeringData.setInstructorMName((String) entryDetails.get("InstructorMName"));
		}*/
		//offeringData.setInterInsIndicator("true");
		//2 feb 
		 offeringData.setInterInsIndicator("false");
		//
		offeringData.setInstructor(getInstructor((ODataFeed)entry.getProperties().get("InstructorDetails"), offeringData) );
		//logger.error("SetInstructor"+offeringData.getInstructor()+offeringData.getInstructor());
		//5 feb
		int flag=0;
		if(offeringData.getInstructor()!=null){
			//logger.error("BIT2me is here");
			List<Instructor> instructor=offeringData.getInstructor();
			for(Instructor in:instructor){
				
				if(checkInternal(in.getInstructorID())){
					flag=1;
				//	logger.error("white1");
				}
			}
			if(flag==1){
				offeringData.setInterInsIndicator("true");
				//logger.error("white2");
			}
		}
		else
		{
			offeringData.setInterInsIndicator("NOT");
		}
		
		
		
		
		
		
		
		//
		offeringData.setLegalEntity((String) entryDetails.get("LegalEntity"));
		offeringData.setLocationName((String) entryDetails.get("LocationName"));
		offeringData.setObservations((String) entryDetails.get("Observations"));
		/*offeringData.setOfferingCancelledDate("OfferingCancelledDate");
		offeringData.setOfferingCloseDate("OfferingCloseDate");*/
		offeringData.setOfferingEndDate(endDate.getTime());
		offeringData.setOfferingStartDate(startDate.getTime());
		offeringData.setScheduleDesc((String) entryDetails.get("ScheduleDesc"));
		offeringData.setScheduleOfferingContact((String) entryDetails.get("ScheduleOfferingContact"));
		offeringData.setUpdatedOn(new Date((new Date()).getTime() - (1000 * 60 * 60 * 5)));
		offeringData.setOfferingId((String) entryDetails.get("OfferingID"));
		String instIndicator = (String) entryDetails.get("InterInsIndicator");
		/*if("yes".equals(instIndicator)) {
			offeringData.setExtInsIndicator("true");
			offeringData.setInterInsIndicator("false");
		} else if("no".equals(instIndicator)) {
			offeringData.setInterInsIndicator("true");
			offeringData.setExtInsIndicator("false");
		} else {
			offeringData.setInterInsIndicator("true");
			offeringData.setExtInsIndicator("true");
		}*/
		offeringData.setItemSecondaryID((String) entryDetails.get("ItemSecondaryID"));
		return offeringData;
	}
	
	private static List<Instructor> getInstructor(ODataFeed feed, Offering offer) {
		List<Instructor> instructorList = new ArrayList<Instructor>();
		if (feed != null) {
			for (ODataEntry entry : feed.getEntries()) {
				Instructor instructor = oDataEntryToInstructorData(entry, offer);
				instructorList.add(instructor);
			}
		} 
		return instructorList;
	}
	
	private static Instructor oDataEntryToInstructorData(ODataEntry entry, Offering offer) {
		Map<String, Object> entryDetails = entry.getProperties();
		Instructor inst = new Instructor();
		inst.setHoursPerInstructor((String) entryDetails.get("HoursPerInstructor"));
		inst.setInstructorFName((String) entryDetails.get("InstructorFName"));
		inst.setInstructorID((String) entryDetails.get("InstructorID"));
		inst.setInstructorLName((String) entryDetails.get("InstructorLName"));
		inst.setInstructorMName((String) entryDetails.get("InstructorMName"));
//		int flag=0;
//		logger.error("BIT1it is here ");
//		if("true".equals(offer.getInstructor())) {
//			logger.error("BIT2it is here ");
//			//if(checkExternal(inst.getInstructorID())) {
//				//offer.setInterInsIndicator("false");
//			//}
//		}
//		//2 feb mayank
//		
//		if(offer.getInstructor()!=null){
//			logger.error("BIT2me is here");
//			List<Instructor> instructor=offer.getInstructor();
//			for(Instructor in:instructor){
//				logger.error("offerid"+offer.getId());
//				logger.error("offeritemcode"+" "+offer.getItemCode());
//				logger.error("Instructor Id"+"   "+ in.getInstructorID());
//				if(checkInternal(in.getInstructorID())){
//					flag=1;
//					logger.error("white1");
//				}
//			}
//			if(flag==1){
//				offer.setInterInsIndicator("true");
//				logger.error("white2");
//			}
//		}
		//
		
		return inst;
	}

	public static List<OfferingParticipant> convertToOfferingParticipantList(
			ODataFeed feed) {
		List<OfferingParticipant> offeringParticipantList = new ArrayList<OfferingParticipant>();
		if (feed != null) {
			for (ODataEntry entry : feed.getEntries()) {
				OfferingParticipant offeringParticipant = oDataEntryToOfferingParticipantData(entry);
				offeringParticipantList.add(offeringParticipant);
			}
		}
		return offeringParticipantList;
	}

	public static OfferingParticipant oDataEntryToOfferingParticipantData(
			ODataEntry entry) {
		OfferingParticipant offeringParticipantData = new OfferingParticipant();
		Map<String, Object> entryDetails = entry.getProperties();
		offeringParticipantData.setId((Long) entryDetails.get("Id"));
		offeringParticipantData.setTenant((String) entryDetails.get("Tenant"));
		offeringParticipantData.setParticipantId((String) entryDetails
				.get("ParticipantId"));
		offeringParticipantData.setTypeDocument((int) entryDetails
				.get("TypeDocument"));
		offeringParticipantData.setName((String) entryDetails.get("Name"));
		offeringParticipantData.setFirstSurname((String) entryDetails
				.get("FirstSurName"));
		offeringParticipantData.setSecondSurname((String) entryDetails
				.get("SecondSurName"));
		offeringParticipantData.setSex((String) entryDetails.get("Sex"));
		offeringParticipantData.setDateOfBirth((Date) entryDetails
				.get("DateOfBirth"));
		offeringParticipantData.setCreatedDate((Date) entryDetails
				.get("CreatedDate"));
		offeringParticipantData.setEmail((String) entryDetails.get("Email"));
		offeringParticipantData.setPhoneNumber((String) entryDetails
				.get("PhoneNumber"));
		offeringParticipantData.setCustomData((String) entryDetails
				.get("CustomData"));

		return offeringParticipantData;
	}
	
//	private static boolean checkExternal(String instructorId) {
//		if(instructorId.length() != 7)
//			return true;
//		else
//			return false;
//	}
	
	//2 feb
	private static boolean checkInternal(String instructorId) {
		if(instructorId.length() == 7){
		//	logger.error("white3"+"  "+instructorId.length());
			return true;
		}
		else{
		//	logger.error("white8"+instructorId);
			return false;
		}
	}
	//

}
