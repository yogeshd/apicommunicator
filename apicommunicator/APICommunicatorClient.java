package com.rakuten.gep.checkout.api.apicommunicator;

import java.util.Arrays;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import com.rakuten.gep.checkout.api.constants.CheckoutCreateConstants;
import com.rakuten.gep.checkout.api.constants.CheckoutCommonConstants;
import com.rakuten.gep.checkout.api.dto.request.ShippingFeeRequestDTOBody;
import com.rakuten.gep.checkout.api.dto.response.ShippingFeeCalcResponse;
import com.rakuten.gep.checkout.api.dto.response.ShopResponse;
import com.rakuten.gep.checkout.api.dto.response.StatusResponse;
import com.rakuten.gep.checkout.api.dto.response.TimeZoneResponse;
import com.rakuten.gep.checkout.api.exception.CheckoutAPIException;
import com.sun.jersey.api.client.ClientResponse;

/**
 * @author Shubhamk
 * 
 *         This class is a client to call shop-merchandise/bulk-get to get
 *         shippingWeight and shippingDimension of Item
 * 
 */
@Component
public class APICommunicatorClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(APICommunicatorClient.class);

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
	 * @param checkoutOrderRequestDTOBody
	 * @return
	 * @throws CheckoutAPIException
	 */
	public ShippingFeeCalcResponse callShippingFeeAPI(ShippingFeeRequestDTOBody shippingFeeRequestDTOBody) throws CheckoutAPIException {
		LOGGER.info("in getShippingFee");
		ShippingFeeCalcResponse shippingFeeCalcResponse = null;
		JSONObject json = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			if (shippingFeeRequestDTOBody != null) {
				shippingFeeCalcResponse = new ShippingFeeCalcResponse();
				String requestURL = apiDomainURL.getMessage("shipping-fee-calculate-url", null, null);
				LOGGER.info("requestURL is :: " + requestURL);
				ClientResponse responseFromAPI = RestAPICaller.post(shippingFeeRequestDTOBody.getClientId(),requestURL, mapper.writeValueAsString(shippingFeeRequestDTOBody));
				json = new JSONObject(responseFromAPI.getEntity(String.class));
				int status = json.getInt(CheckoutCommonConstants.STATUS);
				shippingFeeCalcResponse.setStatus(status);
				// To set the message in shippingFeeCalcResponse
				if(status!=CheckoutCommonConstants.STATUS_SUCCESS){
				List<String> messages = Arrays.asList(json.get("message").toString());
					shippingFeeCalcResponse.setMessage(messages);
				} else {
					if (json.has("shippingFee")) {
						shippingFeeCalcResponse.setShippingFee(json.getString("shippingFee"));
					}
				}
			}
		} catch (JSONException e) {
			throw new CheckoutAPIException("Error In method getShippingFee() while getting the results", e);
		} catch (Exception e) {
			throw new CheckoutAPIException("Error In method getShippingFee() while getting the results", e);
		}
		return shippingFeeCalcResponse;
	}

	/**
	 * The method calls shop/get api to get shop details and get status and
	 * message as a JSON response. It also converts JSON response to java class
	 * 
	 * @param checkoutOrderRequestDTOBody
	 * @return CheckoutBaseResponse
	 * @throws CheckoutAPIException
	 */
	public ShopResponse callShopGetAPI(String clientId, String shopId) throws CheckoutAPIException {
		ShopResponse shopGetResponse = null;
		JSONObject json = null;
		try {
			shopGetResponse = new ShopResponse();
			if (shopId != null) {
				String requestURL = apiDomainURL.getMessage("shop-get-url", null, null) + "?shopId=" + shopId;
				ClientResponse responseFromAPI = RestAPICaller.get(clientId, requestURL);
				json = new JSONObject(responseFromAPI.getEntity(String.class));
				int status = json.getInt(CheckoutCommonConstants.STATUS);
				shopGetResponse.setStatus(status);

				if (status == CheckoutCommonConstants.STATUS_SUCCESS) {

					JSONObject jsonShop = json.getJSONObject("shop");
					shopGetResponse.setCountryCode(jsonShop.get("countryCode").toString());

					JSONObject jsonTimeZone = jsonShop.getJSONObject("timeZone");
					TimeZoneResponse timeZone = new TimeZoneResponse();
					timeZone.setTimeZoneId(jsonTimeZone.get("timeZoneId").toString());
					timeZone.setTimeZoneSerializedValue(jsonTimeZone.get("timeZoneSerializedValue").toString());
					shopGetResponse.setTimeZone(timeZone);
					// shopGetResponse.setTimeZoneSerializationValue(jsonTimeZone.get("timeZoneSerializedValue").toString());
					shopGetResponse.setShopNumber(jsonShop.get("shopNumber").toString());

					JSONObject jsonShopStatus = jsonShop.getJSONObject(CheckoutCommonConstants.STATUS);

					StatusResponse statusResponseDTO = new StatusResponse();
					statusResponseDTO.setInactiveTime(jsonShopStatus.optString("inactiveTime"));
					statusResponseDTO.setLiveStartTime(jsonShopStatus.optString("liveStartTime"));
					statusResponseDTO.setName(jsonShopStatus.getString("name"));
					statusResponseDTO.setStatusId(jsonShopStatus.getString("statusId"));
					shopGetResponse.setStatusResponse(statusResponseDTO);

				}

				// To set the message in checkoutBaseResponse
				else {
					List<String> messages = Arrays.asList(json.get(CheckoutCommonConstants.MESSAGE).toString());
					shopGetResponse.setMessage(messages);
				}
			}
		} catch (JSONException e) {
			throw new CheckoutAPIException(CheckoutCreateConstants.ERROR_IN_CALLING_SHOP_API, e);
		} catch (Exception e) {
			throw new CheckoutAPIException(CheckoutCreateConstants.ERROR_IN_CALLING_SHOP_API, e);		}
		return shopGetResponse;
	}
}
