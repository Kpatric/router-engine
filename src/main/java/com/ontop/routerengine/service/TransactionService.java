package com.ontop.routerengine.service;

import com.ontop.routerengine.config.HttpClient;
import com.ontop.routerengine.model.Balance;
import com.ontop.routerengine.model.BalanceResponse;
import com.ontop.routerengine.model.PaymentRequest;
import com.ontop.routerengine.model.Transaction;
import com.ontop.routerengine.repository.TransactionRepository;
import com.ontop.routerengine.utils.RestClientUtils;
import com.ontop.routerengine.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private Logger logger = LoggerFactory.getLogger(TransactionService.class);

    public Object processPayment(PaymentRequest paymentRequest, Long user_id) {
        Transaction tranData = persistTransaction(paymentRequest, user_id);
        BalanceResponse balanceResponse = checkWalletBalance(user_id);
        logger.info("Received balance response for {}", balanceResponse);

        if (!hasSufficientFunds(balanceResponse, paymentRequest.getAmount())) {
            updateTransactionStatus(tranData, "Insufficient funds in the wallet");
            return tranData;
        }

        double chargeAmount = calculateCharge(paymentRequest.getAmount());
        double netTransferAmount = paymentRequest.getAmount() - chargeAmount;
        ResponseEntity<Object> transferRequest = postTransferRequest(paymentRequest, chargeAmount);
        logger.info("Received transfer response for {}", transferRequest);

        if (!isTransferSuccessful(transferRequest)) {
            updateTransactionStatus(tranData, "Bank posting failed");
            return tranData;
        }


        updateWalletBalance(user_id, -netTransferAmount);
        updateTransactionStatus(tranData, "Transfer successful");
        return tranData;
    }

    private Transaction persistTransaction(PaymentRequest paymentRequest, Long user_id) {
        Transaction tranData = Transaction.builder()
                .transactionDate(LocalDateTime.now())
                .serviceName("Transfer")
                .charge(0.0)
                .destinationAccount(paymentRequest.getDestination().getAccount().getAccountNumber())
                .sourceAccount(paymentRequest.getSource().getAccount().getAccountNumber())
                .amount(paymentRequest.getAmount())
                .build();
        transactionRepository.save(tranData);
        return tranData;
    }

    private BalanceResponse checkWalletBalance(Long user_id) {
        String checkBalanceUrl = baseUrl + walletBalanceEndpoint + "?user_id=" + user_id;
        return RestClientUtils.getRequest(httpClient, checkBalanceUrl);
    }

    private boolean hasSufficientFunds(BalanceResponse balanceResponse, double transferAmount) {
        return balanceResponse.getBalance() >= transferAmount;
    }

    private double calculateCharge(double transferAmount) {
        return Utils.calculateFee(transferAmount);
    }

    private ResponseEntity<Object> postTransferRequest(PaymentRequest paymentRequest, double chargeAmount) {
        double netTransferAmount = paymentRequest.getAmount() - chargeAmount;
        paymentRequest.setAmount(netTransferAmount);
        String postPaymentUrl = baseUrl + bankEndPoint;
        return RestClientUtils.postRequest(httpClient, postPaymentUrl, paymentRequest);
    }

    private boolean isTransferSuccessful(ResponseEntity<Object> transferRequest) {
        return transferRequest.getStatusCode() == HttpStatus.OK;
    }

    private void updateWalletBalance(Long user_id, double amount) {
        var balRequest = Balance.builder()
                .amount(amount)
                .user_id(user_id)
                .build();
        String postWalletUpdateUrl = baseUrl + walletEndpoint;
        RestClientUtils.postRequest(httpClient, postWalletUpdateUrl, balRequest);
    }

    private void updateTransactionStatus(Transaction tranData, String status) {
        tranData.setWalletStatus(status);
        tranData.setBankStatus(status);
        transactionRepository.save(tranData);
    }


}
