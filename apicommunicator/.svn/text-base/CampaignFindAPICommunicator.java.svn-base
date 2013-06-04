package com.rakuten.gep.checkout.api.apicommunicator;

import org.springframework.stereotype.Component;
import com.rakuten.gep.checkout.api.exception.CheckoutAPIException;
import com.rakuten.gep.checkout.api.util.ShippingFeeCalculationParameters;
import com.rakuten.gep.externalapi.campfind.CampaignFindService;

/**
 * @author shubhamk
 * 
 */
@Component
public interface CampaignFindAPICommunicator {

	CampaignFindService getCampaignDetails(ShippingFeeCalculationParameters shippingFeeCalculationParameters) throws CheckoutAPIException;

}
