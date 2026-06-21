package com.bugotruco.mishiql.core.api; // Asegúrate de usar el package que tienes en tu proyecto

import com.fasterxml.jackson.databind.JsonNode;
import com.bugotruco.mishiql.core.ast.MishiCriterio;
import com.bugotruco.mishiql.core.ast.MishiQuery;
import com.bugotruco.mishiql.core.exception.MishiQueryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MishiQueryBuilder implements TraemeStage, FiltrameStage, BuscaleElStage, AcomodameStage {

    // El baúl original en memoria
    private final List<JsonNode> datos;

    // --- Caja de herramientas para ir acumulando las elecciones del usuario ---
    private List<String> camposSeleccionados = new ArrayList<>();
    private String campoFiltroActual;
    private MishiCriterio criterioFinal = null; // Puede quedarse null si no usan WHERE
    private String campoOrdenFinal = null;       // Puede quedarse null si no ordenan
    private boolean esAscendente = true;         // Por defecto AL_DERECHO

    public MishiQueryBuilder(List<JsonNode> elBaul) {
        this.datos = elBaul;
    }

    // --- 1. PASO: ¿Qué campos quiere? ---
    @Override
    public FiltrameStage traeme(String... losCampos) {
        if (losCampos != null && losCampos.length > 0) {
            this.camposSeleccionados = Arrays.asList(losCampos);
        }
        return this;
    }

    // --- 2. PASO: ¿Por cuál campo filtramos? ---
    @Override
    public BuscaleElStage siElCampo(String elCampo) {
        if (elCampo == null || elCampo.isBlank()) {
            throw new MishiQueryException("Me pasaste un campo vacío para filtrar.");
        }
        this.campoFiltroActual = elCampo;
        return this;
    }

    // --- 3. PASO: Los operadores (Ahora ya no retornan null, retornan el paso de ordenamiento) ---
    @Override
    public AcomodameStage esIgualA(Object elValor) {
        this.criterioFinal = new MishiCriterio(this.campoFiltroActual, "IGUAL", elValor);
        return this;
    }

    @Override
    public AcomodameStage esMayorA(Object elValor) {
        this.criterioFinal = new MishiCriterio(this.campoFiltroActual, "MAYOR", elValor);
        return this;
    }

    @Override
    public AcomodameStage esMenorA(Object elValor) {
        this.criterioFinal = new MishiCriterio(this.campoFiltroActual, "MENOR", elValor);
        return this;
    }

    @Override
    public AcomodameStage traeElTextito(Object elValor) {
        this.criterioFinal = new MishiCriterio(this.campoFiltroActual, "CONTAINS", elValor);
        return this;
    }

    @Override
    public AcomodameStage noEsIgualA(Object elValor) {
        this.criterioFinal = new MishiCriterio(this.campoFiltroActual, "DIFERENTE", elValor);
        return this;
    }

    // --- 4. PASO: El Ordenamiento (Opcional) ---
    @Override
    public ArmadoListoStage acomodadoPor(String elCampo, ModoOrden elModo) {
        if (elCampo == null || elCampo.isBlank()) {
            throw new MishiQueryException("No puedo ordenar por un campo fantasma.");
        }
        this.campoOrdenFinal = elCampo;
        this.esAscendente = (elModo == ModoOrden.AL_DERECHO);
        return this;
    }

    // --- 5. PASO FINAL: El ensamble de la orden (AST) y ejecución ---
    @Override
    public List<JsonNode> jALALO() throws MishiQueryException {

        MishiQuery papelitoDeLaOrden = new MishiQuery(
                this.camposSeleccionados,
                this.criterioFinal,
                this.campoOrdenFinal,
                this.esAscendente
        );

        // 🚀 ¡AQUÍ CONECTAMOS EL MOTOR!
        return com.bugotruco.mishiql.core.engine.MishiEngine.ejecutar(this.datos, papelitoDeLaOrden);
    }
}