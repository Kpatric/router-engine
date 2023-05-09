package com.ontop.routerengine.controller;

import com.ontop.routerengine.model.BalanceResponse;
import com.ontop.routerengine.model.PaymentRequest;
import com.ontop.routerengine.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    private Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @PostMapping("/transfer")
    public Object walletTransfer(@RequestBody PaymentRequest paymentRequest, @RequestParam Long user_id) {
        logger.info("Received request for {}", paymentRequest);
        return transactionService.processPayment(paymentRequest,user_id);
    }
}
