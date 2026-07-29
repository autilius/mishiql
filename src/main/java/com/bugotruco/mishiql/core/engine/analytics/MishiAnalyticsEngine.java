/*
 * Copyright 2026 Salvador (Autilius) Granados Godínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.bugotruco.mishiql.core.engine.analytics;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import java.util.Objects;

public class MishiAnalyticsEngine {

    public static double calcular(List<JsonNode> registros, String campo, AggregationType tipo) {
        if (tipo == AggregationType.COUNT) {
            return registros.size();
        }

        if (registros == null || registros.isEmpty()) {
            return 0.0;
        }

        // Extraer valores numéricos válidos filtrando nulos o campos faltantes
        List<Double> valores = registros.stream()
                .map(nodo -> nodo.get(campo))
                .filter(Objects::nonNull)
                .filter(JsonNode::isNumber)
                .map(JsonNode::asDouble)
                .toList();

        if (valores.isEmpty()) {
            return 0.0;
        }

        return switch (tipo) {
            case SUM -> redondear(valores.stream().mapToDouble(Double::doubleValue).sum());
            case AVG -> redondear(valores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0));
            case MIN -> redondear(valores.stream().mapToDouble(Double::doubleValue).min().orElse(0.0));
            case MAX -> redondear(valores.stream().mapToDouble(Double::doubleValue).max().orElse(0.0));
            default -> 0.0;
        };
    }

    public static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}