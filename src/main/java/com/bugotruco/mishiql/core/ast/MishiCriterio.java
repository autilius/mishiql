package com.bugotruco.mishiql.core.ast;

/**
 * Guarda los tres datos esenciales de una condición de filtrado.
 * Al ser un Record, es inmutable, Thread-Safe y libre de efectos secundarios.
 */
public record MishiCriterio(String campo, String operador, Object valor) {

    // El constructor compacto de Java 17 nos sirve para meter lógica defensiva básica
    public MishiCriterio {
        if (campo == null || campo.isBlank()) {
            throw new IllegalArgumentException("El campo del criterio no puede ser fantasma.");
        }
        if (operador == null || operador.isBlank()) {
            throw new IllegalArgumentException("El operador del criterio no puede estar vacío.");
        }
        // El valor sí podría ser null (ej. buscar si un campo esIgualA(null))
    }
}