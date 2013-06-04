package com.rakuten.gep.checkout.api.apicommunicator.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.rakuten.gep.checkout.api.apicommunicator.RestAPICaller;
import com.rakuten.gep.checkout.api.apicommunicator.ShopInventoryAPICommunicator;
import com.rakuten.gep.checkout.api.constants.CheckoutCreateConstants;
import com.rakuten.gep.checkout.api.dto.request.ShopInventoryDTO;
import com.rakuten.gep.checkout.api.dto.response.CheckoutBaseResponse;
import com.rakuten.gep.checkout.api.exception.CheckoutAPIException;
import com.sun.jersey.api.client.ClientResponse;

/**
 * @author soumils
 * 
 */
@Component
public class ShopInventoryAPICommunicatorImpl implements ShopInventoryAPICommunicator {

	private MessageSource apiDomainURL;

	/**
	 * @param apiDomainURL
	 *            the apiDomainURL to set
	 */
	@Autowired
	public void setApiDomainURL(MessageSource apiDomainURL) {
		this.apiDomainURL = apiDomainURL;
	}

	/**
	 * The method calls shop-inventory/bulk-cancel api to cancel bulk inventory
	 * and get status and message in response. 
	 * 
	 * @param shopInventoryDTO
	 * @return CheckoutBaseResponse
	 * @throws CheckoutAPIException
	 */
	@Override
	public CheckoutBaseResponse callShopInventoryBulkCancelAPI(String clientId, ShopInventoryDTO shopInventoryDTO) throws CheckoutAPIException {
		CheckoutBaseResponse checkoutBaseResponse = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			checkoutBaseResponse = new CheckoutBaseResponse();
			if (shopInventoryDTO != null) {
				String requestURL = apiDomainURL.getMessage("shop-inventory-bulk-cancel-url", null, null);
				ClientResponse responseFromAPI = RestAPICaller.post(clientId, requestURL, mapper.writeValueAsString(shopInventoryDTO));
				checkoutBaseResponse = responseFromAPI.getEntity(CheckoutBaseResponse.class);				
			}
		} catch (Exception e) {
			throw new CheckoutAPIException(CheckoutCreateConstants.ERROR_IN_CALLING_CANCEL_API, e);
		}
		return checkoutBaseResponse;
	}
	
	/**
	 * The method calls shop-inventory/bulk-consume api to consume bulk
	 * inventory and get status and message in response. 
	 * 
	 * @param shopInventoryDTO
	 * @return CheckoutBaseResponse
	 * @throws CheckoutAPIException
	 */
	@Override
	public CheckoutBaseResponse callShopInventoryBulkConsumeAPI(String clientId, ShopInventoryDTO shopInventoryDTO) throws CheckoutAPIException {
		CheckoutBaseResponse checkoutBaseResponse = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			checkoutBaseResponse = new CheckoutBaseResponse();
			if (shopInventoryDTO != null) {
				String requestURL = apiDomainURL.getMessage("shop-inventory-bulk-consume-url", null, null);
				ClientResponse responseFromAPI = RestAPICaller.post(clientId, requestURL, mapper.writeValueAsString(shopInventoryDTO));
				checkoutBaseResponse = responseFromAPI.getEntity(CheckoutBaseResponse.class);				
			}
		} catch (Exception e) {
			throw new CheckoutAPIException(CheckoutCreateConstants.ERROR_IN_CALLING_CONSUME_API, e);
		}
		return checkoutBaseResponse;
	}
	
}
