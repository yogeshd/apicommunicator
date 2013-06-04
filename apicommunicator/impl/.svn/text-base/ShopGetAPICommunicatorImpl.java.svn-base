package com.rakuten.gep.checkout.api.apicommunicator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.rakuten.gep.checkout.api.apicommunicator.RestAPICaller;
import com.rakuten.gep.checkout.api.apicommunicator.ShopGetAPICommunicator;
import com.rakuten.gep.checkout.api.constants.CheckoutCreateConstants;
import com.rakuten.gep.checkout.api.exception.CheckoutAPIException;
import com.rakuten.gep.externalapi.shop.ShopService;

@Component
public class ShopGetAPICommunicatorImpl implements ShopGetAPICommunicator {

	private MessageSource apiDomainURL;

	/**
	 * @param apiDomainURL
	 *            the apiDomainURL to set
	 */
	@Autowired
	public void setApiDomainURL(MessageSource apiDomainURL) {
		this.apiDomainURL = apiDomainURL;
	}
	

	public ShopService getShopInfromation(String clientId, String shopId) throws CheckoutAPIException {
		String requestURL = null;
		ShopService shopService = null;
		try{
			requestURL = apiDomainURL.getMessage("shop-get-url", null, null) + "?shopId=" + shopId;
			shopService = (ShopService)RestAPICaller.get(clientId, requestURL,ShopService.class);
		}catch (Exception e) {
			throw new CheckoutAPIException(CheckoutCreateConstants.ERROR_IN_CALLING_SHOP_API, e);	
		}
		return shopService;
	}
}
