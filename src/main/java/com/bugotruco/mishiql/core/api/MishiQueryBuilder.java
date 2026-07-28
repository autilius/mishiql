package com.bugotruco.mishiql.core.api;

import com.bugotruco.mishiql.core.ast.MishiCriterio;
import com.bugotruco.mishiql.core.ast.MishiCriterioSimple;
import com.bugotruco.mishiql.core.ast.MishiCriterioCompuesto;
import com.bugotruco.mishiql.core.ast.MishiQuery;
import com.bugotruco.mishiql.core.engine.MishiEngine;
import com.bugotruco.mishiql.core.engine.analytics.AggregationType;
import com.bugotruco.mishiql.core.engine.analytics.MishiAnalyticsEngine;
import com.bugotruco.mishiql.core.exception.MishiQueryException;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MishiQueryBuilder implements TraemeStage, FiltrameStage, BuscaleElStage, AcomodameStage, ArmadoListoStage {

    private final List<JsonNode> datos;

    private List<String> camposSeleccionados = new ArrayList<>();
    private String campoFiltroActual;
    private String campoOrdenFinal = null;
    private boolean esAscendente = true;

    // Guardamos el criterio raíz acumulado para respetar el orden real de AND / OR
    private MishiCriterio criterioAcumulado = null;
    private String ultimoOperadorLogico = "AND";

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

    // --- 3. PASO: Operadores que registran y encadenan el criterio ---
    @Override
    public FiltrameStage esIgualA(Object elValor) {
        agregarCriterio(new MishiCriterioSimple(this.campoFiltroActual, "IGUAL", elValor));
        return this;
    }

    @Override
    public FiltrameStage esMayorA(Object elValor) {
        agregarCriterio(new MishiCriterioSimple(this.campoFiltroActual, "MAYOR", elValor));
        return this;
    }

    @Override
    public FiltrameStage esMenorA(Object elValor) {
        agregarCriterio(new MishiCriterioSimple(this.campoFiltroActual, "MENOR", elValor));
        return this;
    }

    @Override
    public FiltrameStage traeElTextito(Object elValor) {
        agregarCriterio(new MishiCriterioSimple(this.campoFiltroActual, "CONTAINS", elValor));
        return this;
    }

    @Override
    public FiltrameStage noEsIgualA(Object elValor) {
        agregarCriterio(new MishiCriterioSimple(this.campoFiltroActual, "DIFERENTE", elValor));
        return this;
    }

    // Método auxiliar interno para armar el árbol del Composite Pattern correctamente
    private void agregarCriterio(MishiCriterio nuevoCriterio) {
        if (this.criterioAcumulado == null) {
            this.criterioAcumulado = nuevoCriterio;
        } else {
            // Se crea una rama compuesta respetando si fue 'yElCampo' (AND) u 'oElCampo' (OR)
            this.criterioAcumulado = new MishiCriterioCompuesto(
                    List.of(this.criterioAcumulado, nuevoCriterio),
                    this.ultimoOperadorLogico
            );
        }
    }

    // --- 4. PASO: Encadenamiento (FiltrameStage) ---
    @Override
    public BuscaleElStage yElCampo(String elCampo) {
        if (elCampo == null || elCampo.isBlank()) {
            throw new MishiQueryException("Me pasaste un campo vacío para el AND.");
        }
        this.ultimoOperadorLogico = "AND";
        this.campoFiltroActual = elCampo;
        return this;
    }

    @Override
    public BuscaleElStage oElCampo(String elCampo) {
        if (elCampo == null || elCampo.isBlank()) {
            throw new MishiQueryException("Me pasaste un campo vacío para el OR.");
        }
        this.ultimoOperadorLogico = "OR";
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
        MishiQuery papelitoDeLaOrden = new MishiQuery(
                this.camposSeleccionados,
                this.criterioAcumulado,
                this.campoOrdenFinal,
                this.esAscendente
        );

        return com.bugotruco.mishiql.core.engine.MishiEngine.ejecutar(this.datos, papelitoDeLaOrden);
    }

    // =========================================================================
    // IMPLEMENTACIÓN DE DATA ANALYTICS
    // =========================================================================

    @Override
    public long cuentalos() {
        MishiQuery query = construirQuery();
        List<JsonNode> filtrados = MishiEngine.ejecutar(this.datos, query);
        return (long) MishiAnalyticsEngine.calcular(filtrados, null, AggregationType.COUNT);
    }

    @Override
    public double sumaDe(String campo) {
        MishiQuery query = construirQuery();
        List<JsonNode> filtrados = MishiEngine.ejecutar(this.datos, query);
        return MishiAnalyticsEngine.calcular(filtrados, campo, AggregationType.SUM);
    }

    @Override
    public double promedioDe(String campo) {
        MishiQuery query = construirQuery();
        List<JsonNode> filtrados = MishiEngine.ejecutar(this.datos, query);
        return MishiAnalyticsEngine.calcular(filtrados, campo, AggregationType.AVG);
    }

    @Override
    public double maximoDe(String campo) {
        MishiQuery query = construirQuery();
        List<JsonNode> filtrados = MishiEngine.ejecutar(this.datos, query);
        return MishiAnalyticsEngine.calcular(filtrados, campo, AggregationType.MAX);
    }

    @Override
    public double minimoDe(String campo) {
        MishiQuery query = construirQuery();
        List<JsonNode> filtrados = MishiEngine.ejecutar(this.datos, query);
        return MishiAnalyticsEngine.calcular(filtrados, campo, AggregationType.MIN);
    }

    // --- Método auxiliar privado para armar el AST inmutable ---
    private MishiQuery construirQuery() {
        return new MishiQuery(
                this.camposSeleccionados,
                this.criterioAcumulado,
                this.campoOrdenFinal,
                this.esAscendente
        );
    }
}