package com.bugotruco.mishiql.core.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MishiCsvAdapter {

    public static List<JsonNode> convertirCsvAJson(Path rutaCsv) throws IOException {
        List<String> lineas = Files.readAllLines(rutaCsv);
        if (lineas.isEmpty()) return Collections.emptyList();

        // 1. Encabezados
        String[] headers = lineas.get(0).split(",");
        List<JsonNode> listaNodos = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        // 2. Mapeo de filas a ObjectNode
        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) continue;

            String[] valores = linea.split(",");
            ObjectNode node = mapper.createObjectNode();

            for (int j = 0; j < headers.length; j++) {
                String clave = headers[j].trim();
                String valor = valores[j].trim();

                if (esEntero(valor)) {
                    node.put(clave, Integer.parseInt(valor));
                } else if (esDecimal(valor)) {
                    node.put(clave, Double.parseDouble(valor)); // <--- ¡AQUÍ ESTÁ LA MAGIA PARA EL PESO!
                } else if (valor.equalsIgnoreCase("true") || valor.equalsIgnoreCase("false")) {
                    node.put(clave, Boolean.parseBoolean(valor));
                } else {
                    node.put(clave, valor);
                }
            }
            listaNodos.add(node);
        }
        return listaNodos;
    }

    private static boolean esEntero(String str) {
        return str != null && str.matches("-?\\d+");
    }

    private static boolean esDecimal(String str) {
        return str != null && str.matches("-?\\d+\\.\\d+");
    }
}