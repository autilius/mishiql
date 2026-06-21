package com.bugotruco.mishiql.core.api;

/**
 * Reemplaza a SelectStage. El usuario define qué se va a traer.
 */
public interface TraemeStage {
    /**
     * Especifica los campos. Si pasas un "*" te traes todo el esquema.
     */
    FiltrameStage traeme(String... losCampos);
}