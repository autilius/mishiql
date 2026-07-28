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
            case SUM -> valores.stream().mapToDouble(Double::doubleValue).sum();
            case AVG -> valores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            case MIN -> valores.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
            case MAX -> valores.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
            default -> 0.0;
        };
    }
}