package com.bugotruco.mishiql.core.exception;

public class InvalidFieldException extends MishiQueryException {
    public InvalidFieldException(String field) {
        super("El campo '" + field + "' no existe en el esquema del JSON.");
    }
}