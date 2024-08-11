package ru.otus.basic.yampolskiy.servlets.exceptions;

public class ServiceException extends RuntimeException{
    public ServiceException(String message) {
        super(message);
    }
}
