package com.ontop.routerengine.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private PaymentSource source;
    private PaymentDestination destination;
    private double amount;
}
