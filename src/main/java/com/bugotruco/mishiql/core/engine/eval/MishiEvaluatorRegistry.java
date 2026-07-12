package com.bugotruco.mishiql.core.engine.eval;

import com.fasterxml.jackson.databind.JsonNode;
import com.bugotruco.mishiql.core.exception.MishiQueryException;
import java.util.HashMap;
import java.util.Map;

public class MishiEvaluatorRegistry {

    // Interfaz funcional interna para definir cada comportamiento de comparación
    public interface NodeEvaluator {
        boolean evaluar(JsonNode campoNodo, Object valorEsperado, String campoNombre);
    }

    private static final Map<String, NodeEvaluator> EVALUADORES = new HashMap<>();

    static {
        EVALUADORES.put("IGUAL", (nodo, valor, campo) ->
                nodo.asText().equals(String.valueOf(valor)));

        EVALUADORES.put("DIFERENTE", (nodo, valor, campo) ->
                !nodo.asText().equals(String.valueOf(valor)));

        EVALUADORES.put("MAYOR", (nodo, valor, campo) -> {
            if (nodo.isNumber() && valor instanceof Number) {
                return nodo.asDouble() > ((Number) valor).doubleValue();
            }
            throw new MishiQueryException("MAYOR A requiere un campo numérico: " + campo);
        });

        EVALUADORES.put("MENOR", (nodo, valor, campo) -> {
            if (nodo.isNumber() && valor instanceof Number) {
                return nodo.asDouble() < ((Number) valor).doubleValue();
            }
            throw new MishiQueryException("MENOR A requiere un campo numérico: " + campo);
        });

        EVALUADORES.put("CONTAINS", (nodo, valor, campo) ->
                nodo.asText().contains(String.valueOf(valor)));
    }

    public static NodeEvaluator obtener(String operador) {
        NodeEvaluator evaluador = EVALUADORES.get(operador);
        if (evaluador == null) {
            throw new MishiQueryException("Operador desconocido en el barrio: " + operador);
        }
        return evaluador;
    }
}