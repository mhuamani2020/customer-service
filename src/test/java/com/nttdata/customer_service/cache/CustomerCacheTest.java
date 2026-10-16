package com.nttdata.customer_service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.customer_service.config.CacheProperties;
import com.nttdata.customerservice.dto.CustomerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de {@link CustomerCache} con el cliente Redis mockeado
 * (Parte III: los nuevos métodos públicos deben tener sus pruebas con mocks).
 * <p>
 * Verifican el comportamiento <i>cache-aside</i> del catálogo de clientes
 * (datos maestros) y que los fallos de Redis sean best-effort.
 *
 * @author NTT Data
 */
@ExtendWith(MockitoExtension.class)
class CustomerCacheTest {

    @Mock
    private ReactiveStringRedisTemplate redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    private CustomerCache customerCache;

    @BeforeEach
    void setUp() {
        CacheProperties properties = new CacheProperties();
        properties.setCustomerTtlSeconds(120L);
        customerCache = new CustomerCache(redisTemplate, new ObjectMapper(), properties);
    }

    @Test
    @DisplayName("get: devuelve el cliente deserializado cuando está en caché")
    void get_whenHit_shouldReturnDeserializedCustomer() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("customer:cust-100"))
                .thenReturn(Mono.just("{\"id\":\"cust-100\",\"name\":\"Juan Perez\"}"));

        StepVerifier.create(customerCache.get("cust-100"))
                .expectNextMatches(res -> "cust-100".equals(res.getId())
                        && "Juan Perez".equals(res.getName()))
                .verifyComplete();
    }

    @Test
    @DisplayName("get: devuelve vacío cuando el cliente no está en caché")
    void get_whenMiss_shouldReturnEmpty() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("customer:cust-999")).thenReturn(Mono.empty());

        StepVerifier.create(customerCache.get("cust-999"))
                .verifyComplete();
    }

    @Test
    @DisplayName("get: ante un error de Redis devuelve vacío (best-effort)")
    void get_whenRedisFails_shouldReturnEmpty() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("customer:cust-100"))
                .thenReturn(Mono.error(new RuntimeException("redis caido")));

        StepVerifier.create(customerCache.get("cust-100"))
                .verifyComplete();
    }

    @Test
    @DisplayName("put: guarda el cliente serializado con el TTL configurado")
    void put_shouldStoreSerializedCustomerWithTtl() {
        CustomerResponse customer = new CustomerResponse();
        customer.setId("cust-100");
        customer.setName("Juan Perez");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.set(eq("customer:cust-100"), anyString(), eq(Duration.ofSeconds(120))))
                .thenReturn(Mono.just(true));

        StepVerifier.create(customerCache.put(customer))
                .verifyComplete();

        verify(valueOperations).set(eq("customer:cust-100"), anyString(), eq(Duration.ofSeconds(120)));
    }

    @Test
    @DisplayName("put: un fallo de Redis no propaga error (best-effort)")
    void put_whenRedisFails_shouldCompleteWithoutError() {
        CustomerResponse customer = new CustomerResponse();
        customer.setId("cust-100");

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.set(eq("customer:cust-100"), anyString(), any(Duration.class)))
                .thenReturn(Mono.error(new RuntimeException("redis caido")));

        StepVerifier.create(customerCache.put(customer))
                .verifyComplete();
    }

    @Test
    @DisplayName("evict: elimina la clave del cliente en Redis")
    void evict_shouldDeleteKey() {
        when(redisTemplate.delete("customer:cust-100")).thenReturn(Mono.just(1L));

        StepVerifier.create(customerCache.evict("cust-100"))
                .verifyComplete();

        verify(redisTemplate).delete("customer:cust-100");
    }

    @Test
    @DisplayName("evict: un fallo de Redis no propaga error (best-effort)")
    void evict_whenRedisFails_shouldCompleteWithoutError() {
        when(redisTemplate.delete("customer:cust-100"))
                .thenReturn(Mono.error(new RuntimeException("redis caido")));

        StepVerifier.create(customerCache.evict("cust-100"))
                .verifyComplete();
    }
}
