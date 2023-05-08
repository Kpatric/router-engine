package com.ontop.routerengine.service;

import com.ontop.routerengine.config.HttpClient;
import com.ontop.routerengine.model.Balance;
import com.ontop.routerengine.model.BalanceResponse;
import com.ontop.routerengine.model.PaymentRequest;
import com.ontop.routerengine.model.Transaction;
import com.ontop.routerengine.repository.TransactionRepository;
import com.ontop.routerengine.utils.Utils;
import com.ontop.routerengine.utils.Utils.*;
import jdk.jshell.execution.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransactionService {
    @Value("${baseUrl}")
    public String baseUrl;

    @Value("${walletBalanceEndpoint}")
    public String walletBalanceEndpoint;

    @Value("${bankEndPoint}")
    public String bankEndPoint;

    @Value("${walletEndpoint}")
    public String walletEndpoint;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private TransactionRepository transactionRepository;


    public Object processPayment(PaymentRequest paymentRequest, Long user_id) {
        //persist transaction for reporting
        var tranData = Transaction.builder().transactionDate(LocalDateTime.now())
                .serviceName("Transfer")
                .charge(0.0)
                .destinationAccount(paymentRequest.getDestination().getAccount().getAccountNumber())
                .sourceAccount(paymentRequest.getSource().getAccount().getAccountNumber())
                .amount(paymentRequest.getAmount())
                .status("Processing")
                .build();
        transactionRepository.save(tranData);




        //check wallet balance first.
        String checkBalanceUrl = baseUrl + walletBalanceEndpoint + "?user_id=" + user_id;
        var balanceResponse = httpClient.getRestTemplate()
                .getForObject(checkBalanceUrl, BalanceResponse.class);
        //if there is available funds as compared to amount to transfer, initiate transfer else cancel
        if (balanceResponse.getBalance() < paymentRequest.getAmount()) {
            return "Insufficient funds in the wallet";
        }
        var transferRequest = makePayment(paymentRequest);
        //if response code is 200 mark as successful and update balance
        if (!(transferRequest.getStatusCode() == HttpStatus.OK)) {
            return transferRequest;
        }
        //update wallet balance
        var updateWalletBalance = updateWallet(paymentRequest.getAmount(), user_id);


        return balanceResponse;


    }


    private ResponseEntity<Object> makePayment(PaymentRequest paymentRequest) {
        var calculateCharges = Utils.calculateFee(paymentRequest.getAmount());
        paymentRequest.setAmount(paymentRequest.getAmount() - calculateCharges);
        String postPaymentUrl = baseUrl + bankEndPoint;
        return httpClient.getRestTemplate()
                .postForEntity(postPaymentUrl, paymentRequest, Object.class);

    }

    private ResponseEntity<Object> updateWallet(double amount, Long user_id) {
        String postWalletUpdateUrl = baseUrl + walletEndpoint;
        var balRequest = Balance.builder()
                .amount(-amount)
                .user_id(user_id)
                .build();
        return httpClient.getRestTemplate()
                .postForEntity(postWalletUpdateUrl, balRequest, Object.class);
    }
}
