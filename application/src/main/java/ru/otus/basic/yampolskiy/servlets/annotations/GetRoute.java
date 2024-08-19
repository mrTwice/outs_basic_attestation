package ru.otus.basic.yampolskiy.servlets.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WebRoute(method = "GET")
public @interface GetRoute {
    String value() default "";
}