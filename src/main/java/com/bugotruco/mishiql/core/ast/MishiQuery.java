package com.bugotruco.mishiql.core.ast;

import java.util.List;

/**
 * La estructura del AST que representa la consulta completa y consolidada.
 */
public record MishiQuery(
        List<String> camposAProyectar, // Lo que va en el .traeme()
        MishiCriterio criterio,         // El filtro de .siElCampo() (puede ser null si no hay WHERE)
        String campoOrden,             // El campo de .acomodadoPor() (puede ser null)
        boolean esAscendente           // true para AL_DERECHO, false para AL_REVES
) {

    // Lógica defensiva para asegurar que el AST no nazca corrupto
    public MishiQuery {
        // Si el usuario no especificó campos, por defecto nos traemos todo ("*")
        if (camposAProyectar == null || camposAProyectar.isEmpty()) {
            camposAProyectar = List.of("*");
        }
    }

    /**
     * Método conveniente para saber si la consulta lleva filtros o va directa.
     */
    public boolean tieneFiltros() {
        return criterio != null;
    }

    /**
     * Método conveniente para saber si hay que ordenar el resultado.
     */
    public boolean tieneOrdenamiento() {
        return campoOrden != null && !campoOrden.isBlank();
    }
}