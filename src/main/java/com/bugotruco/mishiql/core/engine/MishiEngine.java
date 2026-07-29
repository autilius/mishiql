/*
 * Copyright 2026 Salvador (Autilius) Granados Godínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

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

    // --- 2. FASE DE ORDENAMIENTO ---
    private static List<JsonNode> ordenarDatos(List<JsonNode> datos, String campo, boolean esAscendente) {
        // 1. Definimos la comparación interna solo para nodos que SÍ tienen valor
        Comparator<JsonNode> comparadorValores = (val1, val2) -> {
            // Tratar nodos JSON explicitamente nulos (JsonNullNode) como si fueran nulls de Java
            if (val1.isNull()) return val2.isNull() ? 0 : 1;
            if (val2.isNull()) return -1;

            if (val1.isNumber() && val2.isNumber()) {
                return Double.compare(val1.asDouble(), val2.asDouble());
            }
            return val1.asText().compareTo(val2.asText());
        };

        // 2. Envolvemos la extracción del campo asegurando que los nulos queden al final
        Comparator<JsonNode> comparator = Comparator.comparing(
                nodo -> nodo.get(campo),
                Comparator.nullsLast(comparadorValores)
        );

        // 3. Si pide orden descendente, invertimos
        if (!esAscendente) {
            // Nota: Si usas nullsLast con reversed(), la API de Java respeta la posición de los nulos
            comparator = esAscendente ? comparator : comparator.reversed();
        }

        return datos.stream().sorted(comparator).collect(Collectors.toList());
    }

    // --- 3. FASE DE PROYECCIÓN (Optimizado y 100% compatible con Jackson) ---
    private static List<JsonNode> proyectarCampos(List<JsonNode> datos, List<String> camposAProyectar) {
        if (camposAProyectar.contains("*")) {
            return datos;
        }

        return datos.stream()
                .map(nodo -> {
                    // Si el nodo no es un objeto JSON (ej. si fuera una cadena o número suelto), lo dejamos pasar
                    if (!(nodo instanceof ObjectNode objNodo)) {
                        return nodo;
                    }

                    // Usamos la fábrica nativa del nodo para crear el nuevo ObjectNode limpio
                    ObjectNode nuevoNodo = objNodo.arrayNode().objectNode();
                    // O también: ObjectNode nuevoNodo = objNodo.getFactory().objectNode();

                    for (String campo : camposAProyectar) {
                        if (objNodo.has(campo)) {
                            nuevoNodo.set(campo, objNodo.get(campo));
                        }
                    }
                    return (JsonNode) nuevoNodo;
                })
                .collect(Collectors.toList());
    }
}