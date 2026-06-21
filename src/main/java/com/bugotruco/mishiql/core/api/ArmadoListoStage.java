package com.bugotruco.mishiql.core.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.bugotruco.mishiql.core.exception.MishiQueryException;
import java.util.List;

/**
 * Reemplaza a ExecutionStage. El punto sin retorno.
 */
public interface ArmadoListoStage {
    /**
     * Compila el AST y ejecuta la magia.
     * @throws MishiQueryException ¡Miau! Si algo truena (tipos incompatibles o campos fantasma).
     */
    List<JsonNode> jALALO() throws MishiQueryException;
}