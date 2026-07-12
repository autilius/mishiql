package com.bugotruco.mishiql.core.api;

public interface BuscaleElStage {
    BuscaleElStage siElCampo(String elCampo); // Para encadenamientos limpios
    FiltrameStage esIgualA(Object elValor);
    FiltrameStage esMayorA(Object elValor);
    FiltrameStage esMenorA(Object elValor);
    FiltrameStage traeElTextito(Object elValor);
    FiltrameStage noEsIgualA(Object elValor);
}