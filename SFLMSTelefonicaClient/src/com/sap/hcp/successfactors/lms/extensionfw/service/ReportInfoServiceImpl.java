/**
 * 
 */
package com.sap.hcp.successfactors.lms.extensionfw.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sap.hcp.successfactors.lms.extensionfw.multitenancy.CurrentTenantResolver;
import com.sap.hcp.successfactors.lms.extensionfw.pojo.ReportInfo;

/**
 * @author I319792
 *
 */
@Service
@Transactional
public class ReportInfoServiceImpl implements ReportInfoService {
	
private static final Logger logger = LoggerFactory.getLogger(ReportInfoServiceImpl.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private CurrentTenantResolver currentTenantResolver;

	@Override
	public List<ReportInfo> getAll() {
	
		return null;
	}

	@Override
	public ReportInfo save(ReportInfo reportInfo) {
		entityManager.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, currentTenantResolver.getCurrentTenantId());
		
		entityManager.persist(reportInfo);
		
		return reportInfo;
	}

	@Override
	public void delete() {
		entityManager.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, currentTenantResolver.getCurrentTenantId());
		entityManager.createQuery("delete from ReportInfo reportinfo").executeUpdate();
	}

	@Override
	public ReportInfo getById(Long id) {
		entityManager.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, currentTenantResolver.getCurrentTenantId());
		ReportInfo r;
		r = entityManager.find(ReportInfo.class, id);
		return r;
	}

	@Override
	public List<ReportInfo> getByReportType(String reportType) {
		entityManager.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, currentTenantResolver.getCurrentTenantId());
		Query query=entityManager.createQuery("Select reportInfo from ReportInfo reportInfo where reportInfo.reportType=:reportType");
		query.setParameter("reportType", reportType); 
	    List<ReportInfo> queryReportInfo=query.getResultList();
		return queryReportInfo;
	}
	
	@Override
	public List<ReportInfo> getByLegalEntity(String legalEntity) {
		entityManager.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, currentTenantResolver.getCurrentTenantId());
		Query query=entityManager.createQuery("Select reportInfo from ReportInfo reportInfo where reportInfo.legalEntity=:legalEntity");
		query.setParameter("reportType", legalEntity); 
	    List<ReportInfo> queryReportInfo=query.getResultList();
		return queryReportInfo;
	}
	
	@Override
	public List<ReportInfo> getReportByParam(String id, String legalEntity, String date) {
		entityManager.setProperty(PersistenceUnitProperties.MULTITENANT_PROPERTY_DEFAULT, currentTenantResolver.getCurrentTenantId());
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("Select reportInfo from ReportInfo reportInfo where 1=1 ");
		/*if(!"none".equalsIgnoreCase(id)) {
			queryBuilder.append(" where reportInfo.id=:id");
			if(!"none".equalsIgnoreCase(legalEntity)) {
				queryBuilder.append(" and reportInfo.legalEntity=:legalEntity");
			} 
			
		} else if(!"none".equalsIgnoreCase(legalEntity)) {
			queryBuilder.append(" where reportInfo.legalEntity=:legalEntity");
		}*/
		Date startDate = null;
		Date endDate = null;
		boolean flag1 = false, flag2 = false, flag3 = false;
		if(!id.equalsIgnoreCase("none")){
			queryBuilder.append(" and reportInfo.id = :arg1"); flag1= true;
		}
		if(!legalEntity.equalsIgnoreCase("none")){
			queryBuilder.append(" and reportInfo.legalEntity = :arg2"); flag2= true;
		}
		if(!date.equalsIgnoreCase("none")){
			try {
				startDate = changeDateFormat(date.substring(0, 10).replaceAll("-", "."));
				endDate = changeDateFormat(date.substring(11).replaceAll("-", "."));
				logger.error(startDate+" aman "+endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(startDate != null && endDate != null){
				queryBuilder.append(" and reportInfo.createdDate BETWEEN :arg3 AND :arg4"); flag3= true;
			}
		}
		logger.error(queryBuilder.toString());
		Query query=entityManager.createQuery(queryBuilder.toString());
		if(flag1)
			query.setParameter("arg1", id);
		if(flag2)
			query.setParameter("arg2", legalEntity);
		if(flag3){
			query.setParameter("arg3", startDate, TemporalType.DATE).setParameter("arg4", endDate, TemporalType.DATE);
		}
	    List<ReportInfo> queryReportInfo = (List<ReportInfo>) query.getResultList();
		return queryReportInfo;
	}
	
	private Date changeDateFormat(String dateString) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

		try {

		Date date = formatter.parse(dateString);
		return date;

		} catch (ParseException e) {
		throw e;
		}

		}

}
