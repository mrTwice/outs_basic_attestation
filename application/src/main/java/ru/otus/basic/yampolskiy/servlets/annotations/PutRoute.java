package ru.otus.basic.yampolskiy.servlets.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@WebRoute(method = "PUT")
public @interface PutRoute {
    String value() default "";
}
