package com.eCommerceWebsite.eCommerceWeb.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {

    private Long addressId;
    private String pgName;
    private String pgResponseMessage;
    private String pgStatus;
    private String  paymentMethod;
    private  String pgPaymentId;

}
