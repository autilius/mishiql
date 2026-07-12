package com.bugotruco.mishiql.core.api;

import com.bugotruco.mishiql.core.ast.MishiCriterioSimple;
import com.bugotruco.mishiql.core.ast.MishiCriterioCompuesto;
import com.fasterxml.jackson.databind.JsonNode;
import com.bugotruco.mishiql.core.ast.MishiCriterio;
import com.bugotruco.mishiql.core.ast.MishiQuery;
import com.bugotruco.mishiql.core.exception.MishiQueryException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MishiQueryBuilder implements TraemeStage, FiltrameStage, BuscaleElStage, AcomodameStage {

    private final List<JsonNode> datos;

    private List<String> camposSeleccionados = new ArrayList<>();
    private String campoFiltroActual;
    private String campoOrdenFinal = null;
    private boolean esAscendente = true;

    private final List<MishiCriterio> listaDeCriterios = new ArrayList<>();
    private String operadorLogicoActual = "AND";

    public MishiQueryBuilder(List<JsonNode> elBaul) {
        this.datos = elBaul;
    }

    // --- 1. PASO: ¿Qué campos quiere? ---
    @Override
    public BuscaleElStage traeme(String... losCampos) {
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

    // --- 3. PASO: Los operadores de BuscaleElStage ---
    @Override
    public FiltrameStage esIgualA(Object elValor) {
        this.listaDeCriterios.add(new MishiCriterioSimple(this.campoFiltroActual, "IGUAL", elValor));
        return this;
    }

    @Override
    public FiltrameStage esMayorA(Object elValor) {
        this.listaDeCriterios.add(new MishiCriterioSimple(this.campoFiltroActual, "MAYOR", elValor));
        return this;
    }

    @Override
    public FiltrameStage esMenorA(Object elValor) {
        this.listaDeCriterios.add(new MishiCriterioSimple(this.campoFiltroActual, "MENOR", elValor));
        return this;
    }

    @Override
    public FiltrameStage traeElTextito(Object elValor) {
        this.listaDeCriterios.add(new MishiCriterioSimple(this.campoFiltroActual, "CONTAINS", elValor));
        return this;
    }

    @Override
    public FiltrameStage noEsIgualA(Object elValor) {
        this.listaDeCriterios.add(new MishiCriterioSimple(this.campoFiltroActual, "DIFERENTE", elValor));
        return this;
    }

    // --- 4. PASO: Encadenamiento (FiltrameStage) ---
    @Override
    public BuscaleElStage yElCampo(String elCampo) {
        if (elCampo == null || elCampo.isBlank()) {
            throw new MishiQueryException("Me pasaste un campo vacío para el AND.");
        }
        this.operadorLogicoActual = "AND";
        this.campoFiltroActual = elCampo;
        return this;
    }

    @Override
    public BuscaleElStage oElCampo(String elCampo) {
        if (elCampo == null || elCampo.isBlank()) {
            throw new MishiQueryException("Me pasaste un campo vacío para el OR.");
        }
        this.operadorLogicoActual = "OR";
        this.campoFiltroActual = elCampo;
        return this;
    }

    // --- 5. PASO: El Ordenamiento (AcomodameStage) ---
    @Override
    public ArmadoListoStage acomodadoPor(String elCampo, ModoOrden elModo) {
        if (elCampo == null || elCampo.isBlank()) {
            throw new MishiQueryException("No puedo ordenar por un campo fantasma.");
        }
        this.campoOrdenFinal = elCampo;
        this.esAscendente = (elModo == ModoOrden.AL_DERECHO);
        return this;
    }

    // --- 6. PASO FINAL: El gran zarpazo (ArmadoListoStage) ---
    @Override
    public List<JsonNode> jALALO() throws MishiQueryException {
        MishiCriterio criterioFinal = null;

        if (!this.listaDeCriterios.isEmpty()) {
            if (this.listaDeCriterios.size() == 1) {
                criterioFinal = this.listaDeCriterios.get(0);
            } else {
                criterioFinal = new MishiCriterioCompuesto(this.listaDeCriterios, this.operadorLogicoActual);
            }
        }

        MishiQuery papelitoDeLaOrden = new MishiQuery(
                this.camposSeleccionados,
                criterioFinal,
                this.campoOrdenFinal,
                this.esAscendente
        );

        return com.bugotruco.mishiql.core.engine.MishiEngine.ejecutar(this.datos, papelitoDeLaOrden);
    }
}