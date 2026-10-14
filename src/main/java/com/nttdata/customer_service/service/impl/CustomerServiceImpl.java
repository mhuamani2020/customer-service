package com.nttdata.customer_service.service.impl;

import com.nttdata.customer_service.enums.CustomerType;
import com.nttdata.customer_service.enums.Profile;
import com.nttdata.customer_service.mapper.CustomerMapper;
import com.nttdata.customer_service.repository.CustomerRepository;
import com.nttdata.customer_service.service.CustomerService;
import com.nttdata.customerservice.dto.CustomerRequest;
import com.nttdata.customerservice.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Flux<CustomerResponse> findAll() {
        return customerRepository.findAll()
                .map(CustomerMapper::toResponse);
    }

    @Override
    public Mono<CustomerResponse> findById(String id) {
        return customerRepository.findById(id)
                .map(CustomerMapper::toResponse);
    }

    @Override
    public Mono<CustomerResponse> create(CustomerRequest request) {
        return Mono.just(request)
                .map(CustomerMapper::toEntity)
                .flatMap(customerRepository::save)
                .map(CustomerMapper::toResponse);
    }

    @Override
    public Mono<CustomerResponse> update(String id, CustomerRequest request) {
        return customerRepository.findById(id)
                .flatMap(existingCustomer -> {
                    existingCustomer.setName(request.getName());
                    existingCustomer.setDocumentNumber(request.getDocumentNumber());
                    existingCustomer.setCustomerType(CustomerType.valueOf(request.getCustomerType().toUpperCase()));
                    existingCustomer.setProfile(request.getProfile() != null ? Profile.valueOf(request.getProfile().toUpperCase()) : null);

                    return customerRepository.save(existingCustomer);
                })
                .map(CustomerMapper::toResponse);
    }

    @Override
    public Mono<Void> delete(String id) {
        return customerRepository.deleteById(id);
    }
}
