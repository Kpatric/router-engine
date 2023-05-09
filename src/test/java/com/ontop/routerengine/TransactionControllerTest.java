package com.ontop.routerengine;

import com.ontop.routerengine.controller.TransactionController;
import com.ontop.routerengine.model.*;
import com.ontop.routerengine.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;


    @Test
    void testSuccessfulTransfer() {
        // Set up the test case
        PaymentRequest paymentRequest = PaymentRequest.builder()
                .source(PaymentSource.builder()
                        .type("COMPANY")
                        .sourceInformation(PaymentSourceInformation.builder()
                                .name("ONTOP INC")
                                .build())
                        .account(PaymentAccount.builder()
                                .accountNumber("0245253419")
                                .currency("USD")
                                .routingNumber("028444018")
                                .build())
                        .build())
                .destination(PaymentDestination.builder()
                        .name("TONY STARK")
                        .account(PaymentAccount.builder()
                                .accountNumber("1885226711")
                                .currency("USD")
                                .routingNumber("211927207")
                                .build())
                        .build())
                .amount(1000)
                .build();

        Long userId = 1000L;
        Object responseObj = new Object();

        when(transactionService.processPayment(eq(paymentRequest),eq(userId))).thenReturn(responseObj);

        // Call the method under test
        Object result = transactionController.walletTransfer(paymentRequest, userId);
        // Verify the results
        assertEquals(responseObj, result);
    }
}
