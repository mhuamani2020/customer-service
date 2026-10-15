package com.nttdata.customer_service.mapper;


import com.nttdata.customer_service.enums.CustomerType;
import com.nttdata.customer_service.enums.Profile;
import com.nttdata.customer_service.model.Customer;
import com.nttdata.customerservice.dto.CustomerRequest;
import com.nttdata.customerservice.dto.CustomerResponse;

/**
 * Utilidades de mapeo entre la entidad de dominio {@link Customer} y los DTOs
 * del contrato API ({@code CustomerRequest} y {@code CustomerResponse}).
 *
 * @author NTT Data
 */
public class CustomerMapper {

    /**
     * Convierte un DTO de solicitud en la entidad de dominio {@link Customer}.
     *
     * @param request DTO con los datos del cliente recibidos en la petición
     * @return entidad Customer lista para persistir; el tipo de cliente y el
     *         perfil se convierten a sus enumeraciones (el perfil puede ser nulo)
     */
    public static Customer toEntity(CustomerRequest request) {
        return Customer.builder()
                .documentNumber(request.getDocumentNumber())
                .name(request.getName())
                .customerType(CustomerType.valueOf(request.getCustomerType().toUpperCase()))
                .profile(request.getProfile() != null ? Profile.valueOf(request.getProfile().toUpperCase()) : null)
                .build();
    }

    /**
     * Convierte la entidad {@link Customer} en su DTO de respuesta.
     *
     * @param customer entidad de dominio a transformar
     * @return DTO de respuesta con los datos expuestos del cliente
     */
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
