package com.bugotruco.mishiql.core.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.bugotruco.mishiql.core.ast.MishiCriterio;
import com.bugotruco.mishiql.core.ast.MishiQuery;
import com.bugotruco.mishiql.core.exception.MishiQueryException;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class MishiEngine {

    /**
     * El punto de entrada del motor. Procesa la lista de JSONs siguiendo el plano del AST.
     */
    public static List<JsonNode> ejecutar(List<JsonNode> datosOriginales, MishiQuery query) {
        if (datosOriginales == null || datosOriginales.isEmpty()) {
            return List.of();
        }

        List<JsonNode> resultado = datosOriginales;

        // 1. FASE DE FILTRADO (WHERE)
        if (query.tieneFiltros()) {
            resultado = filtrarDatos(resultado, query.criterio());
        }

        // 2. FASE DE ORDENAMIENTO (ORDER BY) - ¡NUEVO!
        if (query.tieneOrdenamiento()) {
            resultado = ordenarDatos(resultado, query.campoOrden(), query.esAscendente());
        }

        // 3. FASE DE PROYECCIÓN (SELECT) - ¡NUEVO!
        resultado = proyectarCampos(resultado, query.camposAProyectar());

        return resultado;
    }

    // --- Métodos de Filtrado (Los que ya tenías ayer) ---
    private static List<JsonNode> filtrarDatos(List<JsonNode> datos, MishiCriterio criterio) {
        return datos.stream()
                .filter(nodo -> evaluarFiltro(nodo, criterio))
                .collect(Collectors.toList());
    }

    private static boolean evaluarFiltro(JsonNode nodo, MishiCriterio criterio) {
        JsonNode campoNodo = nodo.get(criterio.campo());
        if (campoNodo == null || campoNodo.isNull()) {
            return false;
        }

        String operador = criterio.operador();
        Object valorEsperado = criterio.valor();

        switch (operador) {
            case "IGUAL":
                return campoNodo.asText().equals(String.valueOf(valorEsperado));
            case "DIFERENTE":
                return !campoNodo.asText().equals(String.valueOf(valorEsperado));
            case "MAYOR":
                if (campoNodo.isNumber() && valorEsperado instanceof Number) {
                    return campoNodo.asDouble() > ((Number) valorEsperado).doubleValue();
                }
                throw new MishiQueryException("MAYOR A requiere un campo numérico: " + criterio.campo());
            case "MENOR":
                if (campoNodo.isNumber() && valorEsperado instanceof Number) {
                    return campoNodo.asDouble() < ((Number) valorEsperado).doubleValue();
                }
                throw new MishiQueryException("MENOR A requiere un campo numérico: " + criterio.campo());
            case "CONTAINS":
                return campoNodo.asText().contains(String.valueOf(valorEsperado));
            default:
                throw new MishiQueryException("Operador desconocido: " + operador);
        }
    }

    // --- 2. FASE DE ORDENAMIENTO (Implementación) ---
    private static List<JsonNode> ordenarDatos(List<JsonNode> datos, String campo, boolean esAscendente) {
        Comparator<JsonNode> comparator = (nodo1, nodo2) -> {
            JsonNode val1 = nodo1.get(campo);
            JsonNode val2 = nodo2.get(campo);

            if (val1 == null || val1.isNull()) return 1;  // Mandamos nulos al final
            if (val2 == null || val2.isNull()) return -1;

            // Si son números, comparamos numéricamente para evitar que "10" sea menor que "2"
            if (val1.isNumber() && val2.isNumber()) {
                return Double.compare(val1.asDouble(), val2.asDouble());
            }
            // Si es texto o cualquier otra cosa, comparación alfabética estándar
            return val1.asText().compareTo(val2.asText());
        };

        // Si el usuario pidió AL_REVES, invertimos el comparador
        if (!esAscendente) {
            comparator = comparator.reversed();
        }

        return datos.stream().sorted(comparator).collect(Collectors.toList());
    }

    // --- 3. FASE DE PROYECCIÓN (Implementación) ---
    private static List<JsonNode> proyectarCampos(List<JsonNode> datos, List<String> camposAProyectar) {
        // Si pidieron "*" o no hay restricciones, pasamos los JSONs completitos
        if (camposAProyectar.contains("*")) {
            return datos;
        }

        return datos.stream()
                .map(nodo -> {
                    // Clonamos el nodo como un ObjectNode mutable para poder podar los campos sobrantes
                    ObjectNode nodoRecortado = nodo.deepCopy();

                    // Usamos un iterador para remover de forma segura los campos que NO pidió el usuario
                    Iterator<String> nombresDeCampos = nodoRecortado.fieldNames();
                    while (nombresDeCampos.hasNext()) {
                        String nombreCampo = nombresDeCampos.next();
                        if (!camposAProyectar.contains(nombreCampo)) {
                            nombresDeCampos.remove(); // Zarpazo: campo eliminado de la vista
                        }
                    }
                    return (JsonNode) nodoRecortado;
                })
                .collect(Collectors.toList());
    }
}