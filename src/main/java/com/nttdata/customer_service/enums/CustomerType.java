package com.nttdata.customer_service.enums;

/**
 * Enumeración con los tipos de cliente que admite el banco.
 * <p>
 * Determina la naturaleza del cliente dentro de la clasificación de la Fase II:
 * personal (persona natural) o business (empresa o negocio, que puede ser
 * persona jurídica).
 *
 * @author NTT Data
 */
public enum CustomerType {
    /** Cliente de tipo personal (persona natural). */
    PERSONAL,
    /** Cliente de tipo business (empresa o negocio). */
    BUSINESS
}
