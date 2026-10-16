package com.nttdata.customer_service.enums;

/**
 * Enumeración con los perfiles de cliente definidos en la Fase II del banco.
 * <p>
 * Regla de negocio: un cliente VIP solo puede ser natural (personal) y exige un
 * saldo mínimo promedio diario de S/ 30,000 en sus cuentas de ahorro; un
 * cliente PYME puede ser natural o jurídico y requiere tres meses de
 * antigüedad operativa (operatividad desde la fecha de creación).
 *
 * @author NTT Data
 */
public enum Profile {
    /**
     * Perfil de cliente de alto valor: solo persona natural (personal) con saldo
     * mínimo promedio diario de S/ 30,000 en cuentas de ahorro.
     */
    VIP,
    /**
     * Perfil de pequeña y mediana empresa: persona natural o jurídico con al menos
     * tres meses de antigüedad operativa.
     */
    PYME,
    /** Perfil estándar, sin requisitos especiales de acceso. */
    STANDARD
}
