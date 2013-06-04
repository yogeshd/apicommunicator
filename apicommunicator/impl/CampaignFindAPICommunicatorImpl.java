package com.rakuten.gep.checkout.api.apicommunicator.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import com.rakuten.gep.checkout.api.apicommunicator.CampaignFindAPICommunicator;
import com.rakuten.gep.checkout.api.apicommunicator.RestAPICaller;
import com.rakuten.gep.checkout.api.constants.CheckoutCommonConstants;
import com.rakuten.gep.checkout.api.constants.CheckoutCreateConstants;
import com.rakuten.gep.checkout.api.exception.CheckoutAPIException;
import com.rakuten.gep.checkout.api.exception.CheckoutRuntimeException;
import com.rakuten.gep.checkout.api.util.CheckoutUtils;
import com.rakuten.gep.checkout.api.util.ShippingFeeCalculationParameters;
import com.rakuten.gep.externalapi.campfind.Campaign;
import com.rakuten.gep.externalapi.campfind.CampaignFindService;
import com.rakuten.gep.externalapi.campfind.Discount;

/**
 * @author soumils
 * 
 */
@Component
public class CampaignFindAPICommunicatorImpl implements CampaignFindAPICommunicator {

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

	@Override
	public CampaignFindService getCampaignDetails(ShippingFeeCalculationParameters shippingFeeCalculationParameters) throws CheckoutAPIException {
		String requestURL = null;
		CampaignFindService campaignFindService = null;
		String clientId = shippingFeeCalculationParameters.getClientId();
		String merchantId = shippingFeeCalculationParameters.getMerchantId();
		String shopId = shippingFeeCalculationParameters.getShopId();
		String shopShippingMethodId = shippingFeeCalculationParameters.getShopShippingMethodId();
		String dateTime = shippingFeeCalculationParameters.getActiveTime();
		String activeTime = dateTime.replace(" ", "+");
		try {
			requestURL = apiDomainURL.getMessage("campaign-find-url", null, null) + "?merchantId=" + merchantId + "&shopId=" + shopId + "&shopShippingMethodId=" + shopShippingMethodId + "&isEnabled=" + true + "&activeTime=" + activeTime;
		} catch (NoSuchMessageException e) {
			CheckoutUtils.throwCheckoutRuntimeException("campaign-find-url URL is not set in the property file ");
		}
		try {
			campaignFindService = (CampaignFindService) RestAPICaller.get(clientId, requestURL, CampaignFindService.class);
		} catch (Exception e) {
			throw new CheckoutAPIException("Error In method getCampaignDetails() while getting the results", e);
		}
		/**
		 * Validating the status of the campaign.find response.
		 */
		if (campaignFindService.getHttpStatus() == CheckoutCommonConstants.STATUS_SUCCESS) {
			if (validateCampaignResponse(campaignFindService)) {
				return campaignFindService;
			} else {
				CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.INVALID_SHIPPING_FEE_CAMPAIGN_RESPONSE, null, null));
			}
		} else if (campaignFindService.getHttpStatus() == CheckoutCommonConstants.STATUS_NO_DATA_FOUND) {
			return campaignFindService;
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.INVALID_SHIPPING_FEE_CAMPAIGN_RESPONSE, null, null));
		}
		return campaignFindService;
	}

	/**
	 * @param campaignFindService
	 * @return boolean
	 * @throws CheckoutRuntimeException
	 * @throws CheckoutAPIException
	 * 
	 *             The method validates the campaign response. It checks whether
	 *             campaign has valid data in response.
	 * 
	 */
	private boolean validateCampaignResponse(CampaignFindService campaignFindService) throws CheckoutRuntimeException, CheckoutAPIException {
		if (campaignFindService != null && campaignFindService.getCampaigns() != null && !campaignFindService.getCampaigns().isEmpty()) {
			if (campaignFindService.getCampaigns().size() > 1) {
				CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.INVALID_SHIPPING_FEE_CAMPAIGN_LIST_SIZE, null, null));
			} else {
				Campaign campaign = campaignFindService.getCampaigns().get(0);
				if (campaign != null) {
					String minimumSpend = campaign.getMinimumSpend();
					Discount discount = campaign.getDiscount();
					String campaignType = campaign.getCampaignType();
					return (validateCampaignType(campaignType) && validateMinimumSpendAmount(minimumSpend) && validateDiscount(discount));
				} else {
					CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.INVALID_SHIPPING_FEE_CAMPAIGN_RESPONSE, null, null));
				}
			}
		}
		return false;
	}

	/**
	 * @param campaignType
	 * @return boolean
	 * @throws CheckoutRuntimeException
	 * 
	 *             The method validates campaignType. It checks whether it is
	 *             null or not and having correct possible values.
	 * 
	 */
	private boolean validateCampaignType(String campaignType) throws CheckoutRuntimeException {
		if (StringUtils.isNotBlank(campaignType) && (("SHOP_SHIPPING").equalsIgnoreCase(campaignType) || ("SHOP_ITEM").equalsIgnoreCase(campaignType))) {
			return true;
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.INVALID_SHIPPING_FEE_CAMPAIGN_TYPE, null, null));
			return false;
		}
	}

	/**
	 * @param minimumSpend
	 * @return boolean
	 * @throws CheckoutRuntimeException
	 * 
	 *             The method validates minimumSpendAmout. It checks whether it
	 *             is null or not and is number.
	 * 
	 */
	private boolean validateMinimumSpendAmount(String minimumSpend) throws CheckoutRuntimeException {
		if (NumberUtils.isNumber(minimumSpend) && !StringUtils.contains(minimumSpend, "-")) {
			return true;
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.INVALID_SHIPPING_FEE_CAMPAIGN_MINIMUM_SPEND_AMOUNT, null, null));
			return false;
		}
	}

	/**
	 * @param discount
	 * @return boolean
	 * @throws CheckoutRuntimeException
	 * 
	 *             The method validates discount. It checks whether there is
	 *             valid discount data (i.e. valid discountType and valid
	 *             discount value)
	 * 
	 */
	private boolean validateDiscount(Discount discount) throws CheckoutRuntimeException {
		if (discount != null && ("PERCENT_OFF").equalsIgnoreCase(discount.getDiscountType()) && NumberUtils.isNumber(discount.getValue()) && !StringUtils.contains(discount.getValue(), "-")) {
			return true;
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.INVALID_SHIPPING_FEE_CAMPAIGN_DISCOUNT, null, null));
			return false;
		}
	}
}
