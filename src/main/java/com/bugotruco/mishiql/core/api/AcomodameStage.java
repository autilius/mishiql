package com.bugotruco.mishiql.core.api;

/**
 * Reemplaza a OrderByStage.
 */
public interface AcomodameStage extends ArmadoListoStage {
    /**
     * Ordena el desorden.
     */
    ArmadoListoStage acomodadoPor(String elCampo, ModoOrden elModo);
}
