package com.nttdata.customer_service.service;

import com.nttdata.customer_service.cache.CustomerCache;
import com.nttdata.customer_service.enums.CustomerType;
import com.nttdata.customer_service.enums.Profile;
import com.nttdata.customer_service.model.Customer;
import com.nttdata.customer_service.repository.CustomerRepository;
import com.nttdata.customer_service.service.impl.CustomerServiceImpl;
import com.nttdata.customerservice.dto.CustomerRequest;
import com.nttdata.customerservice.dto.CustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerCache customerCache;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer sampleCustomer;
    private CustomerRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleCustomer = Customer.builder()
                .id("cust-100")
                .name("Juan Perez")
                .documentNumber("12345678")
                .customerType(CustomerType.PERSONAL)
                .profile(Profile.VIP)
                .build();

        sampleRequest = new CustomerRequest();
        sampleRequest.setName("Juan Perez");
        sampleRequest.setDocumentNumber("12345678");
        sampleRequest.setCustomerType("PERSONAL");
        sampleRequest.setProfile("VIP");

        // La caché Redis (datos maestros) es best-effort: por defecto se comporta como
        // "miss" y como escrituras/borrados exitosos para no acoplar los tests base.
        lenient().when(customerCache.get(anyString())).thenReturn(Mono.empty());
        lenient().when(customerCache.put(any(CustomerResponse.class))).thenReturn(Mono.empty());
        lenient().when(customerCache.evict(anyString())).thenReturn(Mono.empty());
    }

    // --- FIND ALL & FIND BY ID ---

    @Test
    @DisplayName("findAll: Debe retornar la lista completa de clientes mapeada a response")
    void findAll_shouldReturnAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Flux.just(sampleCustomer));

        Flux<CustomerResponse> result = customerService.findAll();

        StepVerifier.create(result)
                .expectNextMatches(res -> "cust-100".equals(res.getId())
                        && "Juan Perez".equals(res.getName())
                        && "12345678".equals(res.getDocumentNumber()))
                .verifyComplete();

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("findById: Debe retornar el cliente si el ID existe")
    void findById_whenExists_shouldReturnCustomer() {
        when(customerRepository.findById("cust-100")).thenReturn(Mono.just(sampleCustomer));

        Mono<CustomerResponse> result = customerService.findById("cust-100");

        StepVerifier.create(result)
                .expectNextMatches(res -> "cust-100".equals(res.getId())
                        && "Juan Perez".equals(res.getName()))
                .verifyComplete();

        verify(customerRepository, times(1)).findById("cust-100");
    }

    @Test
    @DisplayName("findById: Debe retornar Mono.empty cuando el ID no existe")
    void findById_whenNotFound_shouldReturnEmpty() {
        when(customerRepository.findById("cust-999")).thenReturn(Mono.empty());

        Mono<CustomerResponse> result = customerService.findById("cust-999");

        StepVerifier.create(result)
                .verifyComplete();

        verify(customerRepository, times(1)).findById("cust-999");
    }

    // --- CREATE ---

    @Test
    @DisplayName("create: Debe transformar el request, persistir en base de datos y retornar response")
    void create_shouldSaveAndReturnCustomer() {
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(sampleCustomer));

        Mono<CustomerResponse> result = customerService.create(sampleRequest);

        StepVerifier.create(result)
                .expectNextMatches(res -> "cust-100".equals(res.getId())
                        && "Juan Perez".equals(res.getName())
                        && "PERSONAL".equalsIgnoreCase(res.getCustomerType()))
                .verifyComplete();

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    // --- UPDATE ---

    @Test
    @DisplayName("update: Debe actualizar campos y persistir cambios cuando el cliente existe")
    void update_whenCustomerExists_shouldUpdateAndSave() {
        CustomerRequest updateReq = new CustomerRequest();
        updateReq.setName("Juan Perez Modificado");
        updateReq.setDocumentNumber("87654321");
        updateReq.setCustomerType("BUSINESS");
        updateReq.setProfile("PYME");

        when(customerRepository.findById("cust-100")).thenReturn(Mono.just(sampleCustomer));
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        Mono<CustomerResponse> result = customerService.update("cust-100", updateReq);

        StepVerifier.create(result)
                .expectNextMatches(res -> "Juan Perez Modificado".equals(res.getName())
                        && "87654321".equals(res.getDocumentNumber())
                        && "BUSINESS".equalsIgnoreCase(res.getCustomerType())
                        && "PYME".equalsIgnoreCase(res.getProfile()))
                .verifyComplete();

        verify(customerRepository, times(1)).findById("cust-100");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("update: Debe retornar vacio si el ID no existe")
    void update_whenCustomerNotFound_shouldReturnEmpty() {
        when(customerRepository.findById("cust-999")).thenReturn(Mono.empty());

        Mono<CustomerResponse> result = customerService.update("cust-999", sampleRequest);

        StepVerifier.create(result)
                .verifyComplete();

        verify(customerRepository, times(1)).findById("cust-999");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // --- DELETE ---

    @Test
    @DisplayName("delete: Invoca el borrado por ID reactivo")
    void delete_shouldCallRepositoryDeleteById() {
        when(customerRepository.deleteById("cust-100")).thenReturn(Mono.empty());

        Mono<Void> result = customerService.delete("cust-100");

        StepVerifier.create(result)
                .verifyComplete();

        verify(customerRepository, times(1)).deleteById("cust-100");
    }

    // --- CACHÉ REDIS (DATOS MAESTROS, PARTE III) ---

    @Test
    @DisplayName("findById: Devuelve el cliente desde la caché Redis sin consultar MongoDB")
    void findById_whenCacheHit_shouldReturnCachedCustomerAndSkipRepository() {
        CustomerResponse cached = new CustomerResponse();
        cached.setId("cust-100");
        cached.setName("Juan Perez");
        when(customerCache.get("cust-100")).thenReturn(Mono.just(cached));

        Mono<CustomerResponse> result = customerService.findById("cust-100");

        StepVerifier.create(result)
                .expectNextMatches(res -> "cust-100".equals(res.getId())
                        && "Juan Perez".equals(res.getName()))
                .verifyComplete();

        verify(customerRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("findById: Ante un fallo de caché carga desde MongoDB y refresca la caché")
    void findById_whenCacheMiss_shouldLoadFromRepositoryAndPopulateCache() {
        when(customerCache.get("cust-100")).thenReturn(Mono.empty());
        when(customerRepository.findById("cust-100")).thenReturn(Mono.just(sampleCustomer));

        Mono<CustomerResponse> result = customerService.findById("cust-100");

        StepVerifier.create(result)
                .expectNextMatches(res -> "cust-100".equals(res.getId()))
                .verifyComplete();

        verify(customerRepository, times(1)).findById("cust-100");
        verify(customerCache, times(1)).put(any(CustomerResponse.class));
    }

    @Test
    @DisplayName("delete: Invalida la entrada del cliente en la caché Redis")
    void delete_shouldEvictCacheEntry() {
        when(customerRepository.deleteById("cust-100")).thenReturn(Mono.empty());

        Mono<Void> result = customerService.delete("cust-100");

        StepVerifier.create(result)
                .verifyComplete();

        verify(customerCache, times(1)).evict("cust-100");
    }
}