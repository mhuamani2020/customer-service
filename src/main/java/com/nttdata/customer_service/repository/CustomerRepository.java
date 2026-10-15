package com.nttdata.customer_service.repository;

import com.nttdata.customer_service.model.Customer;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio reactivo de la entidad {@link Customer} sobre MongoDB.
 * <p>
 * Proporciona las operaciones CRUD reactivas (Flux/Mono) únicamente sobre la
 * colección {@code customers} de este microservicio, conforme al patrón
 * database-per-service.
 *
 * @author NTT Data
 */
@Repository
public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
}
