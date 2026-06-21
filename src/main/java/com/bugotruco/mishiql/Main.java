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
        System.out.println("🐾 [MishiQL] Inicializando laboratorio de pruebas...");

        // 1. Instanciamos Jackson para crear datos de juguete
        ObjectMapper mapper = new ObjectMapper();
        List<JsonNode> baulDePrueba = new ArrayList<>();

        try {
            // Creamos un par de nodos JSON simulando registros de IA o auditoría
            JsonNode nodo1 = mapper.readTree("{\"id\":\"NODO_001\", \"tipo\":\"prompt\", \"tokens\":150}");
            JsonNode nodo2 = mapper.readTree("{\"id\":\"NODO_002\", \"tipo\":\"embedding\", \"tokens\":500}");

            baulDePrueba.add(nodo1);
            baulDePrueba.add(nodo2);

            System.out.println("✅ Baúl cargado con " + baulDePrueba.size() + " recuerdos de prueba.");

            // 2. Aquí es donde va a ocurrir la magia.
            // Por ahora, como MishiQL.desde() retorna null, esto lanzará un NullPointerException si lo corres,
            // pero ya tenemos el terreno listo para empezar a conectar las interfaces (Stages).

            /*
            List<JsonNode> resultado = MishiQL.desde(baulDePrueba)
                .traeme("id", "tipo")
                .siElCampo("tokens").esMayorA(200)
                .jALALO();
            */

            List<JsonNode> resultado = MishiQL.desde(baulDePrueba)
                    .traeme("id", "tokens") // Proyección: Solo queremos ver 'id' y 'tokens', 'tipo' debe desaparecer
                    .siElCampo("tokens").esMayorA(200) // Filtrado: Solo NODO_002 cumple tener 500 tokens
                    .acomodadoPor("tokens", ModoOrden.AL_REVES) // Ordenamiento
                    .jALALO();

            System.out.println("🔥 [Resultado Real del Motor]: " + resultado);


            // 1. Instancias tu mapeador y tu bóveda
            //ObjectMapper mapper = new ObjectMapper();
// (Asumiendo que necesitas instanciar o usar el singleton de tu MishiVault)
            MishiVault vault = new MishiVault();

// 2. Extraes los recuerdos reales del disco duro
            List<MishiNode> recuerdosReales = vault.obtenerTodosLosRecuerdos();

// 3. ¡Invocas el motor con la Fluent API en Spanglish!
            List<JsonNode> resultados = MishiQL.desdeLosRecuerdos(recuerdosReales, mapper)
                    .traeme("id", "timestamp") // Pon los campos reales que tenga tu MishiNode
                    .siElCampo("id").noEsIgualA("")
                    .jALALO();

            System.out.println("🐈 ¡MishiQL v1.0 Leyendo el Baúl Real!: " + resultados);

        } catch (MishiQueryException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Error inesperado armando el laboratorio: " + e.getMessage());
        }
    }
}