package com.bugotruco.mishiql.core.ast;

import com.fasterxml.jackson.databind.JsonNode;
import com.bugotruco.mishiql.core.engine.eval.MishiEvaluatorRegistry;

/**
 * Representa un filtro atómico de la v1.0 (ej: tokens > 200).
 * Mantiene la inmutabilidad de los Records.
 */
public record MishiCriterioSimple(String campo, String operador, Object valor) implements MishiCriterio {

    // Tu lógica defensiva original se queda intacta en el constructor compacto
    public MishiCriterioSimple {
        if (campo == null || campo.isBlank()) {
            throw new IllegalArgumentException("El campo del criterio no puede ser fantasma.");
        }
        if (operador == null || operador.isBlank()) {
            throw new IllegalArgumentException("El operador del criterio no puede estar vacío.");
        }
    }

    @Override
    public boolean evaluar(JsonNode nodo) {
        JsonNode campoNodo = nodo.get(this.campo);
        if (campoNodo == null || campoNodo.isNull()) {
            return false;
        }
        // Ejecuta la estrategia del MishiEvaluatorRegistry que armamos
        return MishiEvaluatorRegistry.obtener(this.operador)
                .evaluar(campoNodo, this.valor, this.campo);
    }
}