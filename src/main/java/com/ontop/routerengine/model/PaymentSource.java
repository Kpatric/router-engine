package com.ontop.routerengine.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentSource {
    private String type;
    private PaymentSourceInformation sourceInformation;
    private PaymentAccount account;

}

