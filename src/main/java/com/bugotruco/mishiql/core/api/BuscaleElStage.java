package com.bugotruco.mishiql.core.api;

/**
* Reemplaza a FilterOperatorStage. Cumple OCP pero con barrio.
 */
public interface BuscaleElStage {
    AcomodameStage esIgualA(Object elValor);
    AcomodameStage esMayorA(Object elValor);
    AcomodameStage esMenorA(Object elValor);
    AcomodameStage traeElTextito(Object elValor); // Nuestro 'contains'
    AcomodameStage noEsIgualA(Object elValor);
}