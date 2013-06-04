package com.rakuten.gep.checkout.api.apicommunicator.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import com.rakuten.gep.checkout.api.apicommunicator.RestAPICaller;
import com.rakuten.gep.checkout.api.apicommunicator.ShopShippingAPICommunicator;
import com.rakuten.gep.checkout.api.constants.CheckoutCommonConstants;
import com.rakuten.gep.checkout.api.constants.CheckoutCreateConstants;
import com.rakuten.gep.checkout.api.exception.CheckoutAPIException;
import com.rakuten.gep.checkout.api.exception.CheckoutRuntimeException;
import com.rakuten.gep.checkout.api.util.CheckoutUtils;
import com.rakuten.gep.externalapi.merchant.shopshippingget.RateSchedule;
import com.rakuten.gep.externalapi.merchant.shopshippingget.ShippingService;

/**
 * @author shubhamk
 * 
 */
@Component
public class ShopShippingAPICommunicatorImpl implements ShopShippingAPICommunicator {

	private MessageSource apiDomainURL;
	private MessageSource messageSource;

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

	public ShippingService getShippingInformation(String clientId, String shopShippingMethodId) throws CheckoutRuntimeException, CheckoutAPIException {
		String requestURL = null;
		ShippingService shippingService = null;
		try {
			requestURL = apiDomainURL.getMessage("shop-shipping-get-url", null, null) + "?shopShippingMethodId=" + shopShippingMethodId;
		} catch (NoSuchMessageException e) {
			CheckoutUtils.throwCheckoutRuntimeException("shop-shipping-get-url URL is not set in the property file ");
		}
		try {
			shippingService = (ShippingService) RestAPICaller.get(clientId, requestURL, ShippingService.class);
		} catch (Exception e) {
			throw new CheckoutAPIException("Error In method getShippingInformation() while getting the results", e);
		}
		if (shippingService.getHttpStatus() == CheckoutCommonConstants.STATUS_SUCCESS) {
			RateSchedule rateSchedule = shippingService.getShop().getShippingMethod().getRateSchedule();
			if (validateRateSchedule(rateSchedule)) {
				return shippingService;
			}
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.INVALID_SHOP_SHIPPING_RESPONSE, null, null));
		}
		return shippingService;
	}

	/**
	 * @param rateSchedule
	 * @return
	 * @throws CheckoutAPIException
	 * 
	 *             Validate the rate schedule in the shop shipping response.
	 */
	private boolean validateRateSchedule(RateSchedule rateSchedule) throws CheckoutAPIException, CheckoutRuntimeException {
		if (rateSchedule != null) {
			if (validateDataInRateSchedule(rateSchedule)) {
				return true;
			}
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.RATE_SCHEDULE_IS_NOT_AVAILABLE_IN_SHOP_SHIPPING_RESPONSE, null, null));
		}
		return false;
	}

	/**
	 * @param rateSchedule
	 * @return boolean
	 * @throws CheckoutAPIException
	 * 
	 *             Validate the data in the rate schedule i.e fees,
	 *             destination,unit code
	 */
	private boolean validateDataInRateSchedule(RateSchedule rateSchedule) throws CheckoutAPIException, CheckoutRuntimeException {
		if (validateUnit(rateSchedule) && validateFee(rateSchedule) && validateDestination(rateSchedule)) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param rateSchedule
	 * @return boolean
	 * @throws CheckoutAPIException
	 *             ,CheckoutRuntimeException
	 */
	private boolean validateDestination(RateSchedule rateSchedule) throws CheckoutAPIException, CheckoutRuntimeException {
		if (rateSchedule.getDestinations() != null && !rateSchedule.getDestinations().isEmpty()) {
			if (rateSchedule.getDestinations().size() > 0) {
				return true;
			} else {
				CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.DESTINATION_HAS_NO_DATA_IN_SHOP_SHIPPING_RESPONSE, null, null));
			}
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.DESTINATION_IS_MISSING_IN_SHOP_SHIPPING_RESPONSE, null, null));
		}
		return false;
	}

	/**
	 * @param rateSchedule
	 * @return boolean
	 * 
	 *         validate the fees in the shop shipping response.
	 */
	private boolean validateFee(RateSchedule rateSchedule) throws CheckoutRuntimeException, CheckoutAPIException {
		if (rateSchedule.getFees() != null && !rateSchedule.getFees().isEmpty() && !rateSchedule.getFees().iterator().next().isEmpty()) {
			if (rateSchedule.getFees().size() > 0) {
				return true;
			} else {
				CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.FEES_HAS_NO_DATA_IN_SHOP_SHIPPING_RESPONSE, null, null));
			}
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.FEES_IS_MISSING_IN_SHOP_SHIPPING_RESPONSE, null, null));
		}
		return false;
	}

	/**
	 * @param rateSchedule
	 * @return boolean
	 * @throws CheckoutAPIException
	 *             ,CheckoutRuntimeException
	 * 
	 *             validate the unit in the shop shipping response.
	 */
	private boolean validateUnit(RateSchedule rateSchedule) throws CheckoutRuntimeException, CheckoutAPIException {
		if (rateSchedule.getUnit() != null) {
			if (validateRateType(rateSchedule)) {
				if (rateSchedule.getRateType().equalsIgnoreCase("package")) {
					if (validateUnitCodeForTypePackage(rateSchedule)) {
						return true;
					}
				} else {
					return true;
				}
			}
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.UNIT_IN_SHOP_SHIPPING_RESPONSE_IS_NULL, null, null));
		}
		return false;
	}

	/**
	 * @param rateSchedule
	 * @return boolean
	 * @throws CheckoutAPIException
	 *             ,CheckoutRuntimeException
	 * 
	 *             validating the unit code for the package rate type
	 */
	private boolean validateUnitCodeForTypePackage(RateSchedule rateSchedule) throws CheckoutRuntimeException, CheckoutAPIException {
		String unitCode = rateSchedule.getUnit().getUnitCode();
		if (StringUtils.isNotBlank(unitCode)) {
			if (unitCode.equalsIgnoreCase("kg") || unitCode.equalsIgnoreCase("cm") || unitCode.equalsIgnoreCase("g")) {
				return true;
			} else {
				CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.UNIT_CODE_IN_SHOP_SHIPPING_RESPONSE_IS_INVALID, null, null));
			}
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.UNIT_CODE_IN_SHOP_SHIPPING_RESPONSE_IS_INVALID, null, null));
		}
		return false;
	}

	/**
	 * @param rateSchedule
	 * @return
	 * @throws CheckoutAPIException
	 *             ,CheckoutRuntimeException
	 * 
	 *             validate the rate type in the shop shipping response.
	 */
	private boolean validateRateType(RateSchedule rateSchedule) throws CheckoutAPIException, CheckoutRuntimeException {
		if (StringUtils.isNotBlank(rateSchedule.getRateType())) {
			if (rateSchedule.getRateType().equalsIgnoreCase("package") || rateSchedule.getRateType().equalsIgnoreCase("flat")) {
				return true;
			} else {
				CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.RATE_TYPE_IS_INVALID_IN_SHOP_SHIPPING_RESPONSE, null, null));
			}
		} else {
			CheckoutUtils.throwCheckoutRuntimeException(messageSource.getMessage(CheckoutCreateConstants.RATE_TYPE_IS_INVALID_IN_SHOP_SHIPPING_RESPONSE, null, null));
		}
		return false;
	}
}
