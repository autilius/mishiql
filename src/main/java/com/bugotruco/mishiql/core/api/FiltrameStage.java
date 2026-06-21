package com.bugotruco.mishiql.core.api;

/**
 * Reemplaza a WhereStage. El filtro opcional.
 */
public interface FiltrameStage extends ArmadoListoStage {
    /**
     * Aplica el filtro sobre una propiedad del JSON.
     */
    BuscaleElStage siElCampo(String elCampo);
}