package com.nttdata.customer_service.controller;

import com.nttdata.customer_service.service.CustomerService;
import com.nttdata.customerservice.dto.CustomerRequest;
import com.nttdata.customerservice.dto.CustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CustomerService customerService;

    private CustomerResponse sampleResponse;
    private CustomerRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleResponse = new CustomerResponse();
        sampleResponse.setId("cust-001");
        sampleResponse.setName("Carlos Alcantara");
        sampleResponse.setDocumentNumber("45678912");
        sampleResponse.setCustomerType("PERSONAL");
        sampleResponse.setProfile("VIP");

        sampleRequest = new CustomerRequest();
        sampleRequest.setName("Carlos Alcantara");
        sampleRequest.setDocumentNumber("45678912");
        sampleRequest.setCustomerType("PERSONAL");
        sampleRequest.setProfile("VIP");
    }

    @Test
    @DisplayName("GET /customers: Retorna 200 OK con la lista de clientes")
    void findAll_shouldReturnOkWithCustomersList() {
        when(customerService.findAll()).thenReturn(Flux.just(sampleResponse));

        webTestClient.get()
                .uri("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CustomerResponse.class)
                .hasSize(1)
                .contains(sampleResponse);
    }

    @Test
    @DisplayName("GET /customers/{id}: Retorna 200 OK cuando el cliente existe")
    void findById_whenExists_shouldReturnOk() {
        when(customerService.findById("cust-001")).thenReturn(Mono.just(sampleResponse));

        webTestClient.get()
                .uri("/customers/{id}", "cust-001")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponse.class)
                .isEqualTo(sampleResponse);
    }

    @Test
    @DisplayName("GET /customers/{id}: Retorna 404 Not Found cuando el cliente no existe")
    void findById_whenNotFound_shouldReturnNotFound() {
        when(customerService.findById("cust-999")).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/customers/{id}", "cust-999")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /customers: Retorna 201 Created al registrar un cliente exitosamente")
    void create_shouldReturnCreated() {
        when(customerService.create(any(CustomerRequest.class))).thenReturn(Mono.just(sampleResponse));

        webTestClient.post()
                .uri("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponse.class)
                .isEqualTo(sampleResponse);
    }

    @Test
    @DisplayName("PUT /customers/{id}: Retorna 200 OK al actualizar datos del cliente existente")
    void update_whenExists_shouldReturnOk() {
        when(customerService.update(eq("cust-001"), any(CustomerRequest.class)))
                .thenReturn(Mono.just(sampleResponse));

        webTestClient.put()
                .uri("/customers/{id}", "cust-001")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponse.class)
                .isEqualTo(sampleResponse);
    }

    @Test
    @DisplayName("PUT /customers/{id}: Retorna 404 Not Found cuando el cliente a actualizar no existe")
    void update_whenNotFound_shouldReturnNotFound() {
        when(customerService.update(eq("cust-999"), any(CustomerRequest.class)))
                .thenReturn(Mono.empty());

        webTestClient.put()
                .uri("/customers/{id}", "cust-999")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sampleRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("DELETE /customers/{id}: Retorna 204 No Content al eliminar cliente")
    void delete_shouldReturnNoContent() {
        when(customerService.delete("cust-001")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/customers/{id}", "cust-001")
                .exchange()
                .expectStatus().isNoContent();
    }
}