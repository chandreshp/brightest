package com.imaginea.brightest.execution;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Magic annotation to mark the method as a command. Supports overriding an existing command. If no name is supplied the
 * method name is taken as the command name.
 * 
 * @author apurba
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandInfo {
    String name() default "";

    int order() default -1;
}
