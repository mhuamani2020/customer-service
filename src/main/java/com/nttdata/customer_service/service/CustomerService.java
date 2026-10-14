package com.nttdata.customer_service.service;

import com.nttdata.customerservice.dto.CustomerRequest;
import com.nttdata.customerservice.dto.CustomerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {
    Flux<CustomerResponse> findAll();
    Mono<CustomerResponse> findById(String id);
    Mono<CustomerResponse> create(CustomerRequest request);
    Mono<CustomerResponse> update(String id, CustomerRequest request);
    Mono<Void> delete(String id);
}
