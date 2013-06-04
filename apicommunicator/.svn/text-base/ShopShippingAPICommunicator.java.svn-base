package com.rakuten.gep.checkout.api.apicommunicator;

import org.springframework.stereotype.Component;

import com.rakuten.gep.checkout.api.exception.CheckoutAPIException;
import com.rakuten.gep.checkout.api.exception.CheckoutRuntimeException;
import com.rakuten.gep.externalapi.merchant.shopshippingget.ShippingService;

/**
 * @author shubhamk
 * 
 */
@Component
public interface ShopShippingAPICommunicator {

	ShippingService getShippingInformation(String clientId, String shopShippingMethodId) throws CheckoutRuntimeException,CheckoutAPIException;
}
