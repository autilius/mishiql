package com.bugotruco.mishiql.core.exception;

public class TypeMismatchException extends MishiQueryException {
    public TypeMismatchException(String field, String expectedType, String actualType) {
        super("Conflicto de tipos en el campo '" + field + "'. Esperaba [" + expectedType + "] pero llegó [" + actualType + "].");
    }
}