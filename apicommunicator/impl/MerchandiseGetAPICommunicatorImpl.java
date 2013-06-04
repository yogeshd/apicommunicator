package com.rakuten.gep.checkout.api.apicommunicator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import com.rakuten.gep.checkout.api.apicommunicator.MerchandiseGetAPICommunicator;
import com.rakuten.gep.checkout.api.apicommunicator.RestAPICaller;
import com.rakuten.gep.checkout.api.constants.CheckoutCommonConstants;
import com.rakuten.gep.checkout.api.constants.CheckoutCreateConstants;
import com.rakuten.gep.checkout.api.dto.request.MerchandiseRequestDTO;
import com.rakuten.gep.checkout.api.exception.CheckoutAPIException;
import com.rakuten.gep.checkout.api.exception.CheckoutRuntimeException;
import com.rakuten.gep.checkout.api.util.CheckoutUtils;
import com.rakuten.gep.externalapi.item.bulkget.BulkGetService;

@Component
public class MerchandiseGetAPICommunicatorImpl implements MerchandiseGetAPICommunicator {

	private MessageSource messageSource;
	private MessageSource apiDomainURL;

	/**
	 * @param apiDomainURL
	 *            the apiDomainURL to set
	 */
	@Autowired
	public void setApiDomainURL(MessageSource apiDomainURL) {
		this.apiDomainURL = apiDomainURL;
	}

	@Autowired
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @param merchandiseRequestDTO
	 * @return BulkGetService
	 * @throws CheckoutAPIException
	 * 
	 */
	public BulkGetService getMerchandiseDetails(String clientId, MerchandiseRequestDTO merchandiseRequestDTO) throws CheckoutRuntimeException, CheckoutAPIException {
		String requestURL = null;
		BulkGetService bulkGetService = null;
		try {
			requestURL = apiDomainURL.getMessage("shop-merchandise-get-url", null, null);
		} catch (NoSuchMessageException e) {
			CheckoutUtils.throwCheckoutRuntimeException("shop-merchandise-get-url URL is not set in the property file ");
		}
		try {
			bulkGetService = (BulkGetService) RestAPICaller.post(clientId, requestURL, merchandiseRequestDTO, BulkGetService.class);
		} catch (Exception e) {
			throw new CheckoutAPIException("Error In method getMerchandiseDetails() while getting the results", e);
		}
		if (bulkGetService.getHttpStatus() == CheckoutCommonConstants.STATUS_SUCCESS) {
			return bulkGetService;
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.INVALID_MERCHANDISE_RESPONSE, null, null));
		}
		return bulkGetService;
	}
}
