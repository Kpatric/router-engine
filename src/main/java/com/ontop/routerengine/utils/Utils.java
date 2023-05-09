package com.ontop.routerengine.utils;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
@UtilityClass
public class Utils {
    public double calculateFee(double amount) {
        return BigDecimal.valueOf(amount)
                .multiply(new BigDecimal("0.1"))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
