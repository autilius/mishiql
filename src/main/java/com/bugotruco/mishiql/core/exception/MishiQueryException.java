package com.bugotruco.mishiql.core.exception;

/**
 * Excepción raíz para todo lo que truene dentro de MishiQL.
 * Al heredar de RuntimeException, mantenemos la Fluent API limpia de try-catches estorbosos.
 */
public class MishiQueryException extends RuntimeException {

    // Constructor básico para mandar un rugido/mensaje personalizado
    public MishiQueryException(String message) {
        super("🐾 ¡Miau! Error en MishiQL: " + message);
    }

    // Constructor compuesto por si queremos arrastrar la causa original (ej. un error de Jackson)
    public MishiQueryException(String message, Throwable cause) {
        super("🐾 ¡Miau! Error en MishiQL: " + message, cause);
    }
}