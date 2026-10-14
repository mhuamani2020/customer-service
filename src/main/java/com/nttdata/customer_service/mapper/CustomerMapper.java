package com.nttdata.customer_service.mapper;


import com.nttdata.customer_service.enums.CustomerType;
import com.nttdata.customer_service.enums.Profile;
import com.nttdata.customer_service.model.Customer;
import com.nttdata.customerservice.dto.CustomerRequest;
import com.nttdata.customerservice.dto.CustomerResponse;

public class CustomerMapper {
    public static Customer toEntity(CustomerRequest request) {
        return Customer.builder()
                .documentNumber(request.getDocumentNumber())
                .name(request.getName())
                .customerType(CustomerType.valueOf(request.getCustomerType().toUpperCase()))
                .profile(request.getProfile() != null ? Profile.valueOf(request.getProfile().toUpperCase()) : null)
                .build();
    }

    public static CustomerResponse toResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setDocumentNumber(customer.getDocumentNumber());
        response.setName(customer.getName());
        response.setCustomerType(customer.getCustomerType() != null ? customer.getCustomerType().name() : null);
        response.setProfile(customer.getProfile() != null ? customer.getProfile().name() : null);
        return response;
    }
}
