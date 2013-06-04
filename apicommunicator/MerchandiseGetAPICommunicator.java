package com.rakuten.gep.checkout.api.apicommunicator;

import org.springframework.stereotype.Component;
import com.rakuten.gep.checkout.api.dto.request.MerchandiseRequestDTO;
import com.rakuten.gep.checkout.api.exception.CheckoutAPIException;
import com.rakuten.gep.checkout.api.exception.CheckoutRuntimeException;
import com.rakuten.gep.externalapi.item.bulkget.BulkGetService;

@Component
public interface MerchandiseGetAPICommunicator {

	BulkGetService getMerchandiseDetails(String clientId, MerchandiseRequestDTO merchandiseRequestDTO) throws CheckoutRuntimeException,CheckoutAPIException;
}
