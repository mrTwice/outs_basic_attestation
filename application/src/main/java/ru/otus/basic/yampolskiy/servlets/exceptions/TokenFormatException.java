package ru.otus.basic.yampolskiy.servlets.exceptions;

public class TokenFormatException extends AuthorizationException {
    public TokenFormatException(String message) {
        super(message);
    }
}
