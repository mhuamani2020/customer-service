package com.nttdata.customer_service.model;

import com.nttdata.customer_service.enums.CustomerType;
import com.nttdata.customer_service.enums.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;

    private String documentNumber;

    private String name;

    // Indica si el cliente es "PERSONAL" o "BUSINESS"
    private CustomerType customerType;

    // Perfil del cliente: "VIP", "PYME", "STANDARD"
    private Profile profile;
}
