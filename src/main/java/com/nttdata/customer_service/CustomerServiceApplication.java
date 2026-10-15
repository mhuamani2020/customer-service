package com.nttdata.customer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal del microservicio de clientes (customer-service).
 * <p>
 * Arranca la aplicación Spring Boot basada en WebFlux y expone la API
 * reactiva de gestión de clientes sobre MongoDB. La instancia se registra
 * en Eureka (servidor de descubrimiento) para que el API Gateway enrute
 * hacia ella las peticiones de {@code /customers/**}. Conforme al patrón
 * database-per-service, este servicio únicamente accede a la colección
 * {@code customers} y no a los datos de otros microservicios.
 *
 * @author NTT Data
 */
@SpringBootApplication
public class CustomerServiceApplication {

	/**
	 * Punto de entrada de la aplicación Spring Boot.
	 *
	 * @param args argumentos de línea de comandos proporcionados al arrancar
	 */
	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}

}
