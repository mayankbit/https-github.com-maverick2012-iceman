/**
 * 
 */
package com.sap.hcp.successfactors.lms.extensionfw.pojo;

import java.util.Date;
import java.util.List;



public class Offering {
	
	private Long id;
	private String legalEntity;
	private String itemCode;
	private String itemCode1;
	private String itemTitle;
	private Integer offeringCode;
	private String offeringId;
	private String itemSecondaryID;
	private String interInsIndicator;
	private String extInsIndicator;
	private Date offeringStartDate;
	private Date offeringEndDate;
	private String deliveryMethod;
	private String scheduleDesc;
	private Integer numberOfParticipants;
	private String scheduleOfferingContact;
	private String facilityName;
	private String locationName;
	private String facilityAddress;
	private String facilityPostal;
	private String facilityCity;
	private String facilityCountry;
	private String facilityDesc;
	private String facilityComments;
	private Date firstDayMorningStartDateTime;
	private Date firstDayMorningEndDateTime;
	private Date firstDayAfternoonStartDateTime;
	private Date firstDayAfternoonEndDateTime;
	private Date onlineFirstDayMorningStartDateTime;
	private Date onlineFirstDayMorningEndDateTime;
	private Date onlineFirstDayAfternoonStartDateTime;
	private Date onlineFirstDayAfternoonEndDateTime;
	private Date creationDate;
	private String daysOfTeaching;
	private String hoursPerInstructor;
	private String instructorID;
	private String instructorFName;
	private String instructorLName;
	private String instructorMName;
	private Double lengthOfOffering;
	private Date offeringCloseDate;
	private Date offeringCancelledDate;
	private String userProvidedValue;
	private String columnNo;
	private Integer totalCount;
	private String observations;
	private List<Instructor> instructor;
	
	private String externalInstructorID;

	private String customData;

	private Double creditHours;

	private Double cpeHours;

	private Double contactHours;

	private Date updatedOn;
	
	private boolean cetFlag;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLegalEntity() {
		return legalEntity;
	}

	public void setLegalEntity(String legalEntity) {
		this.legalEntity = legalEntity;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public Integer getOfferingCode() {
		return offeringCode;
	}

	public void setOfferingCode(Integer offeringCode) {
		this.offeringCode = offeringCode;
	}

	public Date getOfferingStartDate() {
		return (Date)offeringStartDate.clone();
	}

	public void setOfferingStartDate(Date offeringStartDate) {
		this.offeringStartDate = (Date)offeringStartDate.clone();
	}

	public Date getOfferingEndDate() {
		return (Date)offeringEndDate.clone();
	}

	public void setOfferingEndDate(Date offeringEndDate) {
		this.offeringEndDate =(Date)offeringEndDate.clone();
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public String getScheduleDesc() {
		return scheduleDesc;
	}

	public void setScheduleDesc(String scheduleDesc) {
		this.scheduleDesc = scheduleDesc;
	}

	public Integer getNumberOfParticipants() {
		return numberOfParticipants;
	}

	public void setNumberOfParticipants(Integer numberOfParticipants) {
		this.numberOfParticipants = numberOfParticipants;
	}

	public String getScheduleOfferingContact() {
		return scheduleOfferingContact;
	}

	public void setScheduleOfferingContact(String scheduleOfferingContact) {
		this.scheduleOfferingContact = scheduleOfferingContact;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public String getFacilityDesc() {
		return facilityDesc;
	}

	public void setFacilityDesc(String facilityDesc) {
		this.facilityDesc = facilityDesc;
	}

	public String getFacilityComments() {
		return facilityComments;
	}

	public void setFacilityComments(String facilityComments) {
		this.facilityComments = facilityComments;
	}
	
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getFacilityAddress() {
		return facilityAddress;
	}

	public void setFacilityAddress(String facilityAddress) {
		this.facilityAddress = facilityAddress;
	}

	public String getFacilityPostal() {
		return facilityPostal;
	}

	public void setFacilityPostal(String facilityPostal) {
		this.facilityPostal = facilityPostal;
	}

	public String getFacilityCity() {
		return facilityCity;
	}

	public void setFacilityCity(String facilityCity) {
		this.facilityCity = facilityCity;
	}
	
	public String getItemCode1() {
		return itemCode1;
	}

	public void setItemCode1(String itemCode1) {
		this.itemCode1 = itemCode1;
	}

	public String getFacilityCountry() {
		return facilityCountry;
	}

	public void setFacilityCountry(String facilityCountry) {
		this.facilityCountry = facilityCountry;
	}

	public Date getFirstDayMorningStartDateTime() {
		return firstDayMorningStartDateTime;
	}

	public void setFirstDayMorningStartDateTime(Date firstDayMorningStartDateTime) {
		this.firstDayMorningStartDateTime = firstDayMorningStartDateTime;
	}

	public Date getFirstDayMorningEndDateTime() {
		return firstDayMorningEndDateTime;
	}

	public void setFirstDayMorningEndDateTime(Date firstDayMorningEndDateTime) {
		this.firstDayMorningEndDateTime =firstDayMorningEndDateTime;
	}

	public Date getFirstDayAfternoonStartDateTime() {
		return firstDayAfternoonStartDateTime;
	}

	public void setFirstDayAfternoonStartDateTime(
			Date firstDayAfternoonStartDateTime) {
		this.firstDayAfternoonStartDateTime = firstDayAfternoonStartDateTime;
	}

	public Date getFirstDayAfternoonEndDateTime() {
		return firstDayAfternoonEndDateTime;
	}

	public void setFirstDayAfternoonEndDateTime(Date firstDayAfternoonEndDateTime) {
		this.firstDayAfternoonEndDateTime = firstDayAfternoonEndDateTime;
	}

	public Date getCreationDate() {
		return (Date)creationDate.clone();
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = (Date)creationDate.clone();
	}

	public String getDaysOfTeaching() {
		return daysOfTeaching;
	}

	public void setDaysOfTeaching(String daysOfTeaching) {
		this.daysOfTeaching = daysOfTeaching;
	}

	public String getHoursPerInstructor() {
		return hoursPerInstructor;
	}

	public void setHoursPerInstructor(String hoursPerInstructor) {
		this.hoursPerInstructor = hoursPerInstructor;
	}

	public String getInstructorID() {
		return instructorID;
	}

	public void setInstructorID(String instructorID) {
		this.instructorID = instructorID;
	}

	public String getInstructorFName() {
		return instructorFName;
	}

	public void setInstructorFName(String instructorFName) {
		this.instructorFName = instructorFName;
	}

	public String getInstructorLName() {
		return instructorLName;
	}

	public void setInstructorLName(String instructorLName) {
		this.instructorLName = instructorLName;
	}

	public String getInstructorMName() {
		return instructorMName;
	}

	public void setInstructorMName(String instructorMName) {
		this.instructorMName = instructorMName;
	}

	public Double getLengthOfOffering() {
		return lengthOfOffering;
	}

	public void setLengthOfOffering(Double lengthOfOffering) {
		this.lengthOfOffering = lengthOfOffering;
	}

	public Date getOfferingCloseDate() {
		return offeringCloseDate;
	}

	public void setOfferingCloseDate(Date offeringCloseDate) {
		this.offeringCloseDate = offeringCloseDate;
	}

	public Date getOfferingCancelledDate() {
		return offeringCancelledDate;
	}

	public void setOfferingCancelledDate(Date offeringCancelledDate) {
		this.offeringCancelledDate = offeringCancelledDate;
	}

	public String getUserProvidedValue() {
		return userProvidedValue;
	}

	public void setUserProvidedValue(String userProvidedValue) {
		this.userProvidedValue = userProvidedValue;
	}

	public String getColumnNo() {
		return columnNo;
	}

	public void setColumnNo(String columnNo) {
		this.columnNo = columnNo;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getCustomData() {
		return customData;
	}

	public void setCustomData(String customData) {
		this.customData = customData;
	}

	public Double getCreditHours() {
		return creditHours;
	}

	public void setCreditHours(Double creditHours) {
		this.creditHours = creditHours;
	}

	public Double getCpeHours() {
		return cpeHours;
	}

	public void setCpeHours(Double cpeHours) {
		this.cpeHours = cpeHours;
	}

	public Double getContactHours() {
		return contactHours;
	}

	public void setContactHours(Double contactHours) {
		this.contactHours = contactHours;
	}

	public Date getUpdatedOn() {
		return (Date)updatedOn.clone();
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = (Date)updatedOn.clone();
	}

	public String getOfferingId() {
		return offeringId;
	}

	public void setOfferingId(String offeringId) {
		this.offeringId = offeringId;
	}

	public String getItemSecondaryID() {
		return itemSecondaryID;
	}

	public void setItemSecondaryID(String itemSecondaryID) {
		this.itemSecondaryID = itemSecondaryID;
	}

	public String getInterInsIndicator() {
		return interInsIndicator;
	}

	public void setInterInsIndicator(String interInsIndicator) {
		this.interInsIndicator = interInsIndicator;
	}

	public Date getOnlineFirstDayMorningStartDateTime() {
		return onlineFirstDayMorningStartDateTime;
	}

	public void setOnlineFirstDayMorningStartDateTime(
			Date onlineFirstDayMorningStartDateTime) {
		this.onlineFirstDayMorningStartDateTime = onlineFirstDayMorningStartDateTime;
	}

	public Date getOnlineFirstDayMorningEndDateTime() {
		return onlineFirstDayMorningEndDateTime;
	}

	public void setOnlineFirstDayMorningEndDateTime(
			Date onlineFirstDayMorningEndDateTime) {
		this.onlineFirstDayMorningEndDateTime = onlineFirstDayMorningEndDateTime;
	}

	public Date getOnlineFirstDayAfternoonStartDateTime() {
		return onlineFirstDayAfternoonStartDateTime;
	}

	public void setOnlineFirstDayAfternoonStartDateTime(
			Date onlineFirstDayAfternoonStartDateTime) {
		this.onlineFirstDayAfternoonStartDateTime = onlineFirstDayAfternoonStartDateTime;
	}

	public Date getOnlineFirstDayAfternoonEndDateTime() {
		return onlineFirstDayAfternoonEndDateTime;
	}

	public void setOnlineFirstDayAfternoonEndDateTime(
			Date onlineFirstDayAfternoonEndDateTime) {
		this.onlineFirstDayAfternoonEndDateTime = onlineFirstDayAfternoonEndDateTime;
	}

	public List<Instructor> getInstructor() {
		return instructor;
	}

	public void setInstructor(List<Instructor> instructor) {
		this.instructor = instructor;
	}

	public String getExtInsIndicator() {
		return extInsIndicator;
	}

	public void setExtInsIndicator(String extInsIndicator) {
		this.extInsIndicator = extInsIndicator;
	}

	public String getExternalInstructorID() {
		return externalInstructorID;
	}

	public void setExternalInstructorID(String externalInstructorID) {
		this.externalInstructorID = externalInstructorID;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public boolean isCetFlag() {
		return cetFlag;
	}

	public void setCetFlag(boolean cetFlag) {
		this.cetFlag = cetFlag;
	}

	

}
