package ru.otus.basic.yampolskiy.domain.exceptions;

import ru.otus.basic.yampolskiy.servlets.exceptions.ServiceException;

public class UserAlreadyExistException extends ServiceException {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}
