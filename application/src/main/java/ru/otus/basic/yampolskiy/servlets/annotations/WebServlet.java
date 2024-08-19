package ru.otus.basic.yampolskiy.servlets.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface WebServlet {
    String value();
}
