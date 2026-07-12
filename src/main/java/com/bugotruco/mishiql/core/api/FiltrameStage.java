package com.bugotruco.mishiql.core.api;

import com.bugotruco.mishiql.core.exception.MishiQueryException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface FiltrameStage extends AcomodameStage {
    BuscaleElStage yElCampo(String elCampo);
    BuscaleElStage oElCampo(String elCampo);
    List<JsonNode> jALALO() throws MishiQueryException;
}