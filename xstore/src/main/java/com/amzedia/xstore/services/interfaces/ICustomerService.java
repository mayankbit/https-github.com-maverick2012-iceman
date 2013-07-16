/**
 * 
 */
package com.amzedia.xstore.services.interfaces;

import com.amzedia.xstore.XstoreException;
import com.amzedia.xstore.model.ResponseWrapper;

/**
 * @author Tarun Keswani
 * 
 */
public interface ICustomerService {

	/**
	 * 
	 * @param id
	 * @return {@link ResponseWrapper}
	 * @throws XstoreException
	 */
	ResponseWrapper getCustomer(int id) throws XstoreException;

}
