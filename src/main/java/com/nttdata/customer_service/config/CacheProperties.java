package com.nttdata.customer_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades externalizadas de la caché Redis (Parte I: "No deben tener
 * configuraciones en el código").
 * <p>
 * Se leen de {@code app.cache.*} tanto del {@code application.yml} local como del
 * repositorio de configuración centralizada ({@code bank-config-repo}).
 *
 * @author NTT Data
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.cache")
public class CacheProperties {

    /**
     * Tiempo de vida, en segundos, de las entradas del catálogo de clientes
     * almacenadas en Redis. Valor por defecto: 300 segundos (5 minutos).
     */
    private long customerTtlSeconds = 300L;
}
