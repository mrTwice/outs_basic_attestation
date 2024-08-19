package ru.otus.basic.yampolskiy.domain.exceptions;

import ru.otus.basic.yampolskiy.servlets.exceptions.ServiceException;

public class UserNotFoundException extends ServiceException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
