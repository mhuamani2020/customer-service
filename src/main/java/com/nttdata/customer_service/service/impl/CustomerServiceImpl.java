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

/**
 * Implementación del contrato {@link CustomerService} basada en MongoDB reactivo.
 * <p>
 * Delega el acceso a datos en {@link CustomerRepository} y traduce las
 * entidades de dominio a DTOs de respuesta mediante {@link CustomerMapper}.
 * Solo opera sobre la colección {@code customers} (database-per-service).
 *
 * @author NTT Data
 */
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Obtiene todos los clientes y los convierte a DTOs de respuesta.
     *
     * @return flujo con todos los clientes registrados
     */
    @Override
    public Flux<CustomerResponse> findAll() {
        return customerRepository.findAll()
                .map(CustomerMapper::toResponse);
    }

    /**
     * Busca un cliente por su identificador y lo convierte a DTO de respuesta.
     *
     * @param id identificador del cliente
     * @return mono con el cliente encontrado; vacío si no existe
     */
    @Override
    public Mono<CustomerResponse> findById(String id) {
        return customerRepository.findById(id)
                .map(CustomerMapper::toResponse);
    }

    /**
     * Crea un cliente, lo persiste en MongoDB y lo devuelve como DTO.
     *
     * @param request datos del cliente a crear
     * @return mono con el cliente recién creado
     */
    @Override
    public Mono<CustomerResponse> create(CustomerRequest request) {
        return Mono.just(request)
                .map(CustomerMapper::toEntity)
                .flatMap(customerRepository::save)
                .map(CustomerMapper::toResponse);
    }

    /**
     * Actualiza los datos de un cliente existente y lo devuelve como DTO.
     *
     * @param id      identificador del cliente a actualizar
     * @param request nuevos datos del cliente
     * @return mono con el cliente actualizado; vacío si no existe
     */
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

    /**
     * Elimina definitivamente al cliente identificado.
     *
     * @param id identificador del cliente a eliminar
     * @return mono vacío que indica la finalización de la operación
     */
    @Override
    public Mono<Void> delete(String id) {
        return customerRepository.deleteById(id);
    }
}
