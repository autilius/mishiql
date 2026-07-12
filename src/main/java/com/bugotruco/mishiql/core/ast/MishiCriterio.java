package com.bugotruco.mishiql.core.ast;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * La nueva espina dorsal del filtrado en la v2.0.
 * Al ser una interfaz, nos permite usar el Composite Pattern para AND/OR.
 */
public interface MishiCriterio {
    // Dejamos listo el método que usará el motor para evaluar el nodo
    boolean evaluar(JsonNode nodo);
}