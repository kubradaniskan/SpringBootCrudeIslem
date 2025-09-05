package com.kubradaniskan.SpringBootCrudeIslem.exception;

public class DatabaseConnectionException extends RuntimeException {
    public DatabaseConnectionException(String message) {
        super(message);
    }
}
