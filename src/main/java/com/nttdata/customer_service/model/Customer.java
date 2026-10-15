package com.nttdata.customer_service.model;

import com.nttdata.customer_service.enums.CustomerType;
import com.nttdata.customer_service.enums.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entidad de dominio que representa a un cliente del banco.
 * <p>
 * Se persiste en la colección {@code customers} de MongoDB (patrón
 * database-per-service: este microservicio no accede a colecciones de otros
 * servicios). Cada cliente se clasifica por su tipo ({@link CustomerType}:
 * personal o business) y por su perfil ({@link Profile}: VIP, PYME o
 * standard), de acuerdo con las reglas de negocio de la Fase II.
 *
 * @author NTT Data
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "customers")
public class Customer {
    /** Identificador único del cliente generado por MongoDB. */
    @Id
    private String id;

    /** Número de documento de identidad (DNI, CE o RUC) del cliente. */
    private String documentNumber;

    /** Nombre o razón social del cliente. */
    private String name;

    // Indica si el cliente es "PERSONAL" o "BUSINESS"
    private CustomerType customerType;

    // Perfil del cliente: "VIP", "PYME", "STANDARD"
    private Profile profile;
}
