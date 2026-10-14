package com.nttdata.customer_service.controller;


import com.nttdata.customer_service.service.CustomerService;
import com.nttdata.customerservice.api.CustomersApi;
;
import com.nttdata.customerservice.dto.CustomerRequest;
import com.nttdata.customerservice.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;

    @Override
    public Mono<ResponseEntity<Flux<CustomerResponse>>> findAll(ServerWebExchange exchange) {
        log.info("Contract-First: Fetching all customers");
        return Mono.just(ResponseEntity.ok(customerService.findAll()));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> create(Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
        log.info("Contract-First: Creating new customer");
        return customerRequest
                .flatMap(customerService::create)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> findById(String id, ServerWebExchange exchange) {
        log.info("Contract-First: Fetching customer by id {}", id);
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<CustomerResponse>> update(String id, Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
        log.info("Contract-First: Updating customer by id {}", id);
        return customerRequest
                .flatMap(request -> customerService.update(id, request))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Override
    public Mono<ResponseEntity<Void>> delete(String id, ServerWebExchange exchange) {
        log.info("Contract-First: Deleting customer by id {}", id);
        return customerService.delete(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
    }
}
