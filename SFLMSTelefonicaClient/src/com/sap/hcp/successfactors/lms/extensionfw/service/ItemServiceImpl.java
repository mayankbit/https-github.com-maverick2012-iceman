package com.sap.hcp.successfactors.lms.extensionfw.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.NamingException;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.hcp.successfactors.lms.extensionfw.pojo.Item;
import com.sap.hcp.successfactors.lms.extensionfw.reporting.ItemUtil;
import com.sap.hcp.successfactors.lms.extensionfw.reporting.ODataToListConverter;
import com.sap.hcp.successfactors.lms.extensionfw.multitenancy.CurrentTenantResolver;
import com.sap.hcp.successfactors.lms.extensionfw.odata.ODataClientService;
import com.sap.hcp.successfactors.lms.extensionfw.odata.ODataClientServiceImpl;

@Service
public class ItemServiceImpl implements ItemService{

	private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

	private static final String XS_ITEM_TABLE = "Items";

	ODataClientService oDataService;
	
	@Autowired
	CurrentTenantResolver currentTenantResolver;

	private synchronized ODataClientService getODataService() throws EntityProviderException, IOException, NamingException {

		if (oDataService == null ) {
			oDataService = new ODataClientServiceImpl(currentTenantResolver);
			oDataService.getEdm();
		}

		return oDataService;

	}

	@Override
	public List<Item> getItemData(String id, String legalEntity, String date, String runId) {
			List<Item> allItemData = new ArrayList<Item>();
			try { 
				ODataClientService oDataAccess = getODataService();
				List <ODataFeed> bigFeed = new ArrayList<ODataFeed>();
				ODataFeed feed = null;
				String filter = null;
				int flag=0;
				if(!"none".equals(id)) {
					filter = "ItemCode1 eq '" + id + "'";
					feed = oDataAccess.readFeed(XS_ITEM_TABLE, null, filter,
							null);
					bigFeed.add(feed);
				} else {
					logger.error("item time marker 1: "+new Date(System.currentTimeMillis()));
					filter="UpdatedOn eq datetime'9999-12-31T05:00:00' and LegalEntity eq 'FT'";
					feed = oDataAccess.readFeed(XS_ITEM_TABLE, null, filter,
							null);
			/*		feed = oDataAccess.readFeed(XS_ITEM_TABLE, null, filter,
							null,null,1000);
					int skip = 1000; 
					do{
					    feed = null;
						feed = oDataAccess.readFeed(XS_ITEM_TABLE, null, filter,
								null,null,1000,skip);
						skip = skip + 1000;
						bigFeed.add(feed);
						flag=0;
						if(feed.getEntries().size() != 0)
							flag = 1;
					}while(flag==1);*/
				}
				bigFeed.add(feed);
				logger.error("item time marker 2: "+new Date(System.currentTimeMillis()));
				List<Item> meriList = new ArrayList<Item>();
				meriList=removeDuplicates(bigFeed);
				if(meriList != null) {
					for (Item item : meriList) {
						if(validate(id, legalEntity, date, item)) {
							if(item.getLegalEntity()!= null && item.getLegalEntity().equalsIgnoreCase("FT")){
									String deliveryCode = ItemUtil.getDeliveryMethod(item.getDelMethod());
									item.setDelMethod(deliveryCode); 
									allItemData.add(item);
							}
						}
					}
				}
			} catch (IOException | NamingException | ODataException e) {
				logger.error("Something wrong getting OData ref", e);
			}
			return allItemData;
	}
	
	private List<Item> removeDuplicates(List<ODataFeed> bigFeed){
		List<Item> itemList = new ArrayList<Item>();
		for(ODataFeed feed:bigFeed)
		{
		for (ODataEntry entry : feed.getEntries()){
			Item itemData = ODataToListConverter.oDataEntryToItemData(entry);
			itemList.add(itemData);
		}
		}
		Map<String, Item> map = new HashMap<String, Item>();
		List<Item> tempList = new ArrayList<Item>();
		for(Item item : itemList){
			if(map.get(item.getItemCode()) == null){
				map.put(item.getItemCode(), item);
			}
			else{
				if(map.get(item.getItemCode()).getItemCreatedOn().compareTo(item.getItemCreatedOn()) > 0){
					continue;
				}
				else{
					map.put(item.getItemCode(), item);
				}
			}
		}
		for(Map.Entry<String, Item> entry : map.entrySet()){
			tempList.add(entry.getValue());
		}
		return tempList;
	}
	private boolean validate(String id, String legalEntity, String date, Item itemData) {
		if(!"none".equals(id) && !"none".equals(legalEntity)) {
			return validateDate(date, itemData);
		} else if(!"none".equals(id)){
			return validateDate(date, itemData);
		} else if(!"none".equals(legalEntity)) {
			return validateDate(date, itemData);
		} else {
			return validateDate(date, itemData);
		}
		
	}
	
	private boolean validateDate(String date, Item itemData) {
		return true;
/*		if(!"none".equals(date)) {
			try {
				Date startDate = changeDateFormat(date.substring(0, 10));
				Date endDate = changeDateFormat(date.substring(11));
				endDate = new Date(endDate.getTime() + 1000 * 60 * 60 * 24);
				if(startDate.before(itemData.getItemCreatedOn()) || startDate.equals(itemData.getItemCreatedOn())) {
					if(endDate.after(itemData.getItemCreatedOn()) || endDate.equals(itemData.getItemCreatedOn())) {
						return true;	
					}
					else {
						return false;
					}
					
				} else {
					return false;
				}
				
			} catch (ParseException e) {
				return false;
			}
								
		}  else {
			return true;
		}*/
	}
	
	private Date changeDateFormat(String dateString) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

		try {

			Date date = formatter.parse(dateString);
			return date;

		} catch (ParseException e) {
			throw e;
		}

	}
	
	@Override
	public ODataFeed getItemData() {
		ODataFeed feed = null;
		try {
			ODataClientService oDataAccess = getODataService();
			feed = oDataAccess.readFeed(XS_ITEM_TABLE, null, null, null);
		} catch (IOException | NamingException | ODataException e) {
			logger.error("Something wrong getting OData ref", e);
		}
		return feed;
	}

	
	@Override
	public void addCustomFieldValue(Item itemCustomValue) {
		try {
			ODataClientService oDataAccess = getODataService();

			Map<String, Object> data = extendedDataToOData(itemCustomValue);

			oDataAccess.patchEntry(itemCustomValue.getId(),
					XS_ITEM_TABLE, data);

		} catch (Exception e) {
			logger.error("Something wrong getting OData ref", e);
		}

	}

	private Map<String, Object> extendedDataToOData(Item itemCustomvalue) {
		Map<String, Object> odata = new HashMap<String, Object>();

		odata.put("CustomData", itemCustomvalue.getCustomData());
		odata.put("Id", itemCustomvalue.getId());
		return odata;
	}
	
	@Override
	public List<Item> getItemByItemIds(List<Long> itemIds) {
		List<Item> itemList = new ArrayList<Item>();

		
		if (itemIds.isEmpty())
			return itemList;
		try {
			ODataClientService oDataAccess = getODataService();
			ODataFeed feed;
			List<ODataFeed> bigfeed = new ArrayList<ODataFeed>();
			StringBuilder sb = new StringBuilder();
			boolean whetherFirst = true;
			int count = 0;
			for(Long itemId : itemIds) {
				if(!whetherFirst) {
					sb.append(" or Id eq ");
					sb.append(itemId);
				} else {
					sb.append("Id eq ");
					sb.append(itemId);
					whetherFirst = false;
				}
				count++;
				if(count == 10){
					String filter = sb.toString();
					feed = oDataAccess.readFeed(XS_ITEM_TABLE, null, filter, null);
					bigfeed.add(feed);
					sb = new StringBuilder();
					whetherFirst = true;
					count = 0;
				}
			}
			for(ODataFeed finalfeed : bigfeed){
				
				itemList.addAll(ODataToListConverter.convertToItemList(finalfeed));
				
			}
		} catch (IOException | NamingException | ODataException e) {
			logger.error("Something wrong getting Item Data by ID's", e);
		}
		return itemList;
		
	
	}
	
}
