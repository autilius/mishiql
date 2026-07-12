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

        // 1. FASE DE FILTRADO (WHERE) - ¡Ahora invoca el contrato flexible!
        if (query.tieneFiltros()) {
            resultado = filtrarDatos(resultado, query.criterio());
        }

        // 2. FASE DE ORDENAMIENTO (ORDER BY) - (Impecable, se queda igual)
        if (query.tieneOrdenamiento()) {
            resultado = ordenarDatos(resultado, query.campoOrden(), query.esAscendente());
        }

        // 3. FASE DE PROYECCIÓN (SELECT) - (Impecable, se queda igual)
        resultado = proyectarCampos(resultado, query.camposAProyectar());

        return resultado;
    }

    // --- 1. FASE DE FILTRADO (La nueva versión limpia) ---
    private static List<JsonNode> filtrarDatos(List<JsonNode> datos, MishiCriterio criterio) {
        return datos.stream()
                // Borramos el viejo 'evaluarFiltro' y dejamos que el contrato haga su magia recursiva
                .filter(criterio::evaluar)
                .collect(Collectors.toList());
    }

    // --- 2. FASE DE ORDENAMIENTO (Tu lógica intacta) ---
    private static List<JsonNode> ordenarDatos(List<JsonNode> datos, String campo, boolean esAscendente) {
        Comparator<JsonNode> comparator = (nodo1, nodo2) -> {
            JsonNode val1 = nodo1.get(campo);
            JsonNode val2 = nodo2.get(campo);

            if (val1 == null || val1.isNull()) return 1;  // Mandamos nulos al final
            if (val2 == null || val2.isNull()) return -1;

            if (val1.isNumber() && val2.isNumber()) {
                return Double.compare(val1.asDouble(), val2.asDouble());
            }
            return val1.asText().compareTo(val2.asText());
        };

        if (!esAscendente) {
            comparator = comparator.reversed();
        }

        return datos.stream().sorted(comparator).collect(Collectors.toList());
    }

    // --- 3. FASE DE PROYECCIÓN (Tu zarpazo intacto) ---
    private static List<JsonNode> proyectarCampos(List<JsonNode> datos, List<String> camposAProyectar) {
        if (camposAProyectar.contains("*")) {
            return datos;
        }

        return datos.stream()
                .map(nodo -> {
                    ObjectNode nodoRecortado = nodo.deepCopy();
                    Iterator<String> nombresDeCampos = nodoRecortado.fieldNames();
                    while (nombresDeCampos.hasNext()) {
                        String nombreCampo = nombresDeCampos.next();
                        if (!camposAProyectar.contains(nombreCampo)) {
                            nombresDeCampos.remove();
                        }
                    }
                    return (JsonNode) nodoRecortado;
                })
                .collect(Collectors.toList());
    }
}