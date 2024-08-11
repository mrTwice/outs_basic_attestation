package ru.otus.basic.yampolskiy.servlets.exceptions;

public class TokenNotValidException extends AuthorizationException{
    public TokenNotValidException(String message) {
        super(message);
    }
}
