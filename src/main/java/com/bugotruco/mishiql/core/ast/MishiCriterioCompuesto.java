package com.bugotruco.mishiql.core.ast;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

/**
 * El contenedor de la v2.0. Puede agrupar múltiples criterios (simples o compuestos).
 */
public record MishiCriterioCompuesto(List<MishiCriterio> criterios, String operadorLogico) implements MishiCriterio {

    public MishiCriterioCompuesto {
        if (criterios == null || criterios.isEmpty()) {
            throw new IllegalArgumentException("Un criterio compuesto necesita al menos un filtro de compas.");
        }
        if (!operadorLogico.equals("AND") && !operadorLogico.equals("OR")) {
            throw new IllegalArgumentException("Operador lógico inválido en el barrio: " + operadorLogico);
        }
    }

    @Override
    public boolean evaluar(JsonNode nodo) {
        if (operadorLogico.equals("AND")) {
            // Todos los criterios de la lista deben cumplirse (.allMatch)
            return criterios.stream().allMatch(criterio -> criterio.evaluar(nodo));
        } else {
            // Con que uno solo se cumpla es suficiente (.anyMatch)
            return criterios.stream().anyMatch(criterio -> criterio.evaluar(nodo));
        }
    }
}