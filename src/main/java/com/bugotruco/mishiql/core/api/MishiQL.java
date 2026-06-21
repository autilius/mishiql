package com.bugotruco.mishiql.core.api;

import com.bugotruco.mishimentor.MishiNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bugotruco.mishiql.core.exception.MishiQueryException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



public final class MishiQL {

    // Un solo ObjectMapper estático para no saturar la memoria creando uno por consulta
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MishiQL() {}

    /**
     * Variante 1: Jalamos los datos desde una lista viva de JsonNodes que ya tenemos en memoria.
     */
    public static TraemeStage desde(List<JsonNode> elBaul) {
        return new MishiQueryBuilder(elBaul);
    }

    /**
     * Variante 2: El método de conveniencia. Le avientas un String JSON crudo
     * (un arreglo tipo "[{...}, {...}]") y el Mishi lo parsea solito.
     */
    public static TraemeStage desdeTextoJson(String textoJson) {
        try {
            // Leemos el string y lo convertimos en un árbol de Jackson
            JsonNode nodoRaiz = MAPPER.readTree(textoJson);
            List<JsonNode> listaDeNodos = new ArrayList<>();

            // Validamos defensivamente que nos hayan pasado un arreglo homogéneo
            if (nodoRaiz.isArray()) {
                for (JsonNode nodo : nodoRaiz) {
                    listaDeNodos.add(nodo);
                }
            } else {
                // Si nos pasan un objeto suelto u otra cosa, lo metemos a la lista para no tronar
                listaDeNodos.add(nodoRaiz);
            }

            // Ya que tenemos la lista armada, se la aventamos al constructor de siempre
            return new MishiQueryBuilder(listaDeNodos);

        } catch (Exception e) {
            // Si el String está mal formado o no es un JSON válido, soltamos el zarpazo
            throw new MishiQueryException("No pude masticar ese texto JSON, viene medio rancio o corrupto.", e);
        }
    }

    /**
     * 🚀 ¡EL PUENTE ESTRUCTURAL!
     * Recibe los recuerdos fuertemente tipados de MishiVault, los transforma
     * transparentemente a JsonNode usando tu ObjectMapper y arranca el Builder.
     */
    public static TraemeStage desdeLosRecuerdos(List<MishiNode> losRecuerdos, ObjectMapper mapper) {
        if (losRecuerdos == null || losRecuerdos.isEmpty()) {
            return new MishiQueryBuilder(List.of());
        }

        // Mapeo transparente: de Objeto de Dominio a Árbol de JSON
        List<JsonNode> baulTransformado = losRecuerdos.stream()
                .map(mishiNodo -> (JsonNode) mapper.valueToTree(mishiNodo)) // Convierte cada MishiNode en un JsonNode nativo
                .collect(Collectors.toList());

        return new MishiQueryBuilder(baulTransformado);
    }
}
