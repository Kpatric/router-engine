package com.ontop.routerengine.utils;

import com.ontop.routerengine.config.HttpClient;
import com.ontop.routerengine.model.BalanceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class RestClientUtils {

    public static ResponseEntity<Object> postRequest(HttpClient httpClient, String url, Object request) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, request, Object.class);
            return responseEntity;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // handle 4xx and 5xx status codes
            ResponseEntity<Object> errorResponseEntity = ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
            return errorResponseEntity;
        } catch (RestClientException e) {
            // handle other types of exceptions
            // e.g., connection refused, timeout, etc.
            ResponseEntity<Object> errorResponseEntity = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
            return errorResponseEntity;
        }
    }

    public static BalanceResponse getRequest(HttpClient httpClient, String checkBalanceUrl) {
        try {
            return httpClient.getRestTemplate().getForObject(checkBalanceUrl, BalanceResponse.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            // handle 4xx and 5xx status codes
            ResponseEntity<Object> errorResponseEntity = ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
            return null;
        } catch (RestClientException e) {
            // handle other types of exceptions
            // e.g., connection refused, timeout, etc.
            ResponseEntity<Object> errorResponseEntity = ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
            return null;
        }
    }


}
