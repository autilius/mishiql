/*
 * Copyright 2026 Salvador (Autilius) Granados Godínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package com.bugotruco.mishiql.core.api;

import com.bugotruco.mishimentor.MishiNode;
import com.bugotruco.mishiql.core.adapter.MishiCsvAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bugotruco.mishiql.core.exception.MishiQueryException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
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

    /**
     * 🔥 ¡LA EMANCIPACIÓN TOTAL! (Variante 4)
     * Permite usar MishiQL de forma 100% independiente. Va al sistema de archivos,
     * lee los archivos planos .json de la carpeta del usuario y arranca el motor.
     */
    public static TraemeStage desdeCarpeta(String rutaCarpeta) {
        if (rutaCarpeta == null || rutaCarpeta.isBlank()) {
            throw new MishiQueryException("La ruta de la carpeta no puede estar vacía.");
        }

        File carpeta = new File(rutaCarpeta);

        if (!carpeta.exists() || !carpeta.isDirectory()) {
            throw new MishiQueryException("La ruta no existe o no es una carpeta válida: " + rutaCarpeta);
        }

        List<JsonNode> datosCargados = new ArrayList<>();

        // Filtramos para leer solo los archivos que terminen en .json en el Linux
        File[] archivosJson = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        if (archivosJson != null) {
            for (File archivo : archivosJson) {
                try {
                    JsonNode nodo = MAPPER.readTree(archivo);

                    // Si el archivo contiene un arreglo de JSONs, extraemos sus elementos
                    if (nodo.isArray()) {
                        nodo.forEach(datosCargados::add);
                    } else {
                        datosCargados.add(nodo);
                    }
                } catch (Exception e) {
                    throw new MishiQueryException("Error fatal masticando el archivo JSON: " + archivo.getName(), e);
                }
            }
        }

        if (datosCargados.isEmpty()) {
            throw new MishiQueryException("La carpeta está vacía o no tiene archivos .json válidos.");
        }

        return new MishiQueryBuilder(datosCargados);
    }

    public static MishiQueryBuilder desdeCsv(String ruta) {
        try {
            List<JsonNode> datos = MishiCsvAdapter.convertirCsvAJson(Paths.get(ruta));
            return new MishiQueryBuilder(datos);
        } catch (IOException e) {
            // Envolvemos la excepción chequeada de I/O en la excepción oficial de MishiQL
            throw new MishiQueryException("No se pudo leer el archivo CSV en la ruta: " + ruta, e);
        }
    }
}