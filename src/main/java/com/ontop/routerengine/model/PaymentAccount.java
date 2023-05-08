package com.ontop.routerengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentAccount {
    private String accountNumber;
    private String currency;
    private String routingNumber;
}