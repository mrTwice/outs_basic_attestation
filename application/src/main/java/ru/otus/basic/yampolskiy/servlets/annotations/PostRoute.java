package ru.otus.basic.yampolskiy.servlets.annotations;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WebRoute(method = "POST")
public @interface PostRoute {
    String value() default "";
}
