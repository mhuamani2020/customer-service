package com.nttdata.customer_service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nttdata.customer_service.config.CacheProperties;
import com.nttdata.customerservice.dto.CustomerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Caché Redis del catálogo de clientes (datos maestros) sobre la que se apoya
 * {@code CustomerServiceImpl} para acelerar el acceso (Parte III).
 * <p>
 * Los clientes se serializan a JSON en texto para no acoplar el contenido de la
 * caché a detalles de serialización binaria. Todas las operaciones son
 * <b>best-effort</b>: si Redis no está disponible, se registra una advertencia y
 * el servicio continúa resolviendo la lectura contra MongoDB (el origen).
 *
 * @author NTT Data
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCache {

    /** Prefijo de las claves de cliente en Redis. */
    private static final String KEY_PREFIX = "customer:";

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheProperties cacheProperties;

    /**
     * Recupera un cliente del catálogo cacheado.
     *
     * @param id identificador del cliente
     * @return mono con el cliente almacenado; vacío si no está en caché o si Redis falla
     */
    public Mono<CustomerResponse> get(String id) {
        return redisTemplate.opsForValue()
                .get(key(id))
                .flatMap(this::deserialize)
                .onErrorResume(ex -> {
                    log.warn("No se pudo leer el cliente {} de Redis: {}", id, ex.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Almacena (o refresca) un cliente en el catálogo cacheado con el TTL configurado.
     *
     * @param customer cliente a almacenar
     * @return mono que notifica la finalización del intento de escritura
     */
    public Mono<Void> put(CustomerResponse customer) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(customer))
                .flatMap(json -> redisTemplate.opsForValue()
                        .set(key(customer.getId()), json,
                                Duration.ofSeconds(cacheProperties.getCustomerTtlSeconds())))
                .then()
                .onErrorComplete(ex -> {
                    log.warn("No se pudo cachear el cliente {} en Redis: {}", customer.getId(), ex.getMessage());
                    return true;
                });
    }

    /**
     * Invalida la entrada de un cliente del catálogo cacheado.
     *
     * @param id identificador del cliente
     * @return mono que notifica la finalización del intento de borrado
     */
    public Mono<Void> evict(String id) {
        return redisTemplate.delete(key(id))
                .then()
                .onErrorComplete(ex -> {
                    log.warn("No se pudo invalidar el cliente {} de Redis: {}", id, ex.getMessage());
                    return true;
                });
    }

    /**
     * Construye la clave de Redis para un cliente.
     *
     * @param id identificador del cliente
     * @return clave con el prefijo del catálogo
     */
    private String key(String id) {
        return KEY_PREFIX + id;
    }

    /**
     * Deserializa el JSON almacenado en la caché al DTO de respuesta.
     *
     * @param json contenido JSON del cliente
     * @return mono con el cliente deserializado
     */
    private Mono<CustomerResponse> deserialize(String json) {
        return Mono.fromCallable(() -> objectMapper.readValue(json, CustomerResponse.class));
    }
}
