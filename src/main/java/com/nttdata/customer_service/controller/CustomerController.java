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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Controlador REST reactivo que expone los endpoints de gestión de clientes.
 * <p>
 * Implementa el contrato OpenAPI {@code CustomersApi} (enfoque contract-first)
 * y delega cada operación en {@link CustomerService}. Recibe las peticiones
 * enrutadas por el API Gateway bajo la ruta {@code /customers/**}.
 *
 * @author NTT Data
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomersApi {

    private final CustomerService customerService;

    /**
     * Obtiene el listado completo de clientes registrados.
     *
     * @param exchange contexto de la petición HTTP entrante
     * @return respuesta HTTP 200 con el flujo de todos los clientes
     */
    @Override
    public Mono<ResponseEntity<Flux<CustomerResponse>>> findAll(ServerWebExchange exchange) {
        log.info("Contract-First: Fetching all customers");
        return Mono.just(ResponseEntity.ok(customerService.findAll()));
    }

    /**
     * Registra un nuevo cliente a partir de los datos de la petición.
     *
     * @param customerRequest datos del cliente a crear
     * @param exchange        contexto de la petición HTTP entrante
     * @return mono con la respuesta HTTP 201 y el cliente creado
     */
    @Override
    public Mono<ResponseEntity<CustomerResponse>> create(
            Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
        log.info("Contract-First: Creating new customer");
        return customerRequest
                .flatMap(customerService::create)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    /**
     * Busca un cliente por su identificador.
     *
     * @param id      identificador del cliente
     * @param exchange contexto de la petición HTTP entrante
     * @return mono con la respuesta HTTP 200 y el cliente encontrado, o HTTP 404 si no existe
     */
    @Override
    public Mono<ResponseEntity<CustomerResponse>> findById(String id, ServerWebExchange exchange) {
        log.info("Contract-First: Fetching customer by id {}", id);
        return customerService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza los datos de un cliente existente.
     *
     * @param id             identificador del cliente a actualizar
     * @param customerRequest nuevos datos del cliente
     * @param exchange       contexto de la petición HTTP entrante
     * @return mono con la respuesta HTTP 200 y el cliente actualizado, o HTTP 404 si no existe
     */
    @Override
    public Mono<ResponseEntity<CustomerResponse>> update(
            String id, Mono<CustomerRequest> customerRequest, ServerWebExchange exchange) {
        log.info("Contract-First: Updating customer by id {}", id);
        return customerRequest
                .flatMap(request -> customerService.update(id, request))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Elimina un cliente por su identificador.
     *
     * @param id      identificador del cliente a eliminar
     * @param exchange contexto de la petición HTTP entrante
     * @return mono con la respuesta HTTP 204 sin contenido
     */
    @Override
    public Mono<ResponseEntity<Void>> delete(String id, ServerWebExchange exchange) {
        log.info("Contract-First: Deleting customer by id {}", id);
        return customerService.delete(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
    }
}
