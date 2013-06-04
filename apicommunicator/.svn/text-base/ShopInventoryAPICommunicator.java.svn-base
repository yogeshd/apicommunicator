package com.rakuten.gep.checkout.api.apicommunicator;


import org.springframework.stereotype.Component;

import com.rakuten.gep.checkout.api.dto.request.ShopInventoryDTO;
import com.rakuten.gep.checkout.api.dto.response.CheckoutBaseResponse;
import com.rakuten.gep.checkout.api.exception.CheckoutAPIException;

/**
 * @author soumils
 * 
 */
@Component
public interface ShopInventoryAPICommunicator {

	CheckoutBaseResponse callShopInventoryBulkConsumeAPI(String clientId, ShopInventoryDTO shopInventoryDTO) throws CheckoutAPIException;
	
	CheckoutBaseResponse callShopInventoryBulkCancelAPI(String clientId, ShopInventoryDTO shopInventoryDTO) throws CheckoutAPIException;
}
