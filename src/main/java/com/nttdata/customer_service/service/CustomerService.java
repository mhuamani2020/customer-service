package com.nttdata.customer_service.service;

import com.nttdata.customerservice.dto.CustomerRequest;
import com.nttdata.customerservice.dto.CustomerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Contrato de negocio para la gestión de clientes del banco.
 * <p>
 * Define las operaciones CRUD reactivas sobre clientes que el controlador
 * expone mediante la ruta {@code /customers/**}, enrutada por el API Gateway.
 *
 * @author NTT Data
 */
public interface CustomerService {

    /**
     * Obtiene el listado completo de clientes registrados.
     *
     * @return flujo con todos los clientes en formato de respuesta
     */
    Flux<CustomerResponse> findAll();

    /**
     * Obtiene un cliente por su identificador.
     *
     * @param id identificador del cliente
     * @return mono con el cliente encontrado; vacío si no existe
     */
    Mono<CustomerResponse> findById(String id);

    /**
     * Registra un nuevo cliente.
     *
     * @param request datos del cliente a crear
     * @return mono con el cliente recién creado
     */
    Mono<CustomerResponse> create(CustomerRequest request);

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param id      identificador del cliente a actualizar
     * @param request nuevos datos del cliente
     * @return mono con el cliente actualizado; vacío si no existe
     */
    Mono<CustomerResponse> update(String id, CustomerRequest request);

    /**
     * Elimina un cliente por su identificador.
     *
     * @param id identificador del cliente a eliminar
     * @return mono vacío que indica la finalización de la operación
     */
    Mono<Void> delete(String id);
}
