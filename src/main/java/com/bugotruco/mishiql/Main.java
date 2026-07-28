package com.bugotruco.mishiql;

import com.bugotruco.mishimentor.MishiNode;
import com.bugotruco.mishimentor.MishiVault;
import com.bugotruco.mishiql.core.api.MishiQL;
import com.bugotruco.mishiql.core.api.ModoOrden;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.bugotruco.mishiql.core.exception.MishiQueryException;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("🐾 [MishiQL] Inicializando laboratorio de pruebas v2.0...");

        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> baulDePrueba = new ArrayList<>();

        try {
            // =================================================================
            // ESCENARIO 1: Datos de juguete en memoria
            // =================================================================
            JsonNode nodo1 = mapper.readTree("{\"id\":\"NODO_001\", \"tipo\":\"prompt\", \"tokens\":150}");
            JsonNode nodo2 = mapper.readTree("{\"id\":\"NODO_002\", \"tipo\":\"embedding\", \"tokens\":500}");

            baulDePrueba.add(nodo1);
            baulDePrueba.add(nodo2);

            System.out.println("\n✅ [1/3] Baúl cargado con " + baulDePrueba.size() + " recuerdos de prueba.");

            List<JsonNode> resultadoMemoria = MishiQL.desde(baulDePrueba)
                    .traeme("id", "tokens")
                    .siElCampo("tokens").esMayorA(200)
                    .acomodadoPor("tokens", ModoOrden.AL_REVES)
                    .jALALO();

            System.out.println("🔥 [Resultado Real del Motor]: " + resultadoMemoria);

            // =================================================================
            // ESCENARIO 2: Integración con MishiVault (MishiMentor)
            // =================================================================
            System.out.println("\n📦 [2/3] Conectando con MishiVault de MishiMentor...");
            MishiVault vault = new MishiVault();
            List<MishiNode> recuerdosReales = vault.obtenerTodosLosRecuerdos();

            List<JsonNode> resultadosVault = MishiQL.desdeLosRecuerdos(recuerdosReales, mapper)
                    .traeme("id", "timestamp")
                    .siElCampo("id").noEsIgualA("")
                    .jALALO();

            System.out.println("🐈 ¡MishiQL Leyendo el Baúl Real!: " + resultadosVault);

            // =================================================================
            // ESCENARIO 3: ¡NUEVO! Autonomía leyendo una carpeta del sistema
            // =================================================================
            System.out.println("\n📂 [3/3] Probando lectura autónoma desde carpeta local...");

            // Apunta esto a una carpeta real en tu Linux donde metas un par de .json de prueba
            String rutaGatos = "/home/autilius/.mishi_vault/json";

            List<JsonNode> resultadoCarpeta = MishiQL.desdeCarpeta(rutaGatos)
                    .traeme("nombre", "raza", "edad")
                    .siElCampo("raza").esIgualA("Siberiano")
                    .acomodadoPor("edad", ModoOrden.AL_DERECHO)
                    .jALALO();

            System.out.println("🚀 ¡MishiQL Autónomo desde Carpeta!: " + resultadoCarpeta);

            long totalSiberianos = MishiQL.desdeCarpeta(rutaGatos)
                    .traeme("nombre", "raza", "edad")
                    .siElCampo("raza").esIgualA("Siberiano")
                    .cuentalos();

            System.out.println("🐱 Total de Siberianos encontrados: " + totalSiberianos);

        } catch (MishiQueryException e) {
            System.err.println("❌ Error en MishiQL: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error inesperado armando el laboratorio: " + e.getMessage());
        }
    }
}