package com.github.arielcarrera.quarkus.spring.data.jpa.deployment.test.config.custom;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

/**
 * Custom repository config qualifier (example)
 * 
 * @author Ariel Carrera <carreraariel@gmail.com>
 *
 */
@Target({ TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface CustomConfig {

    public static final class Literal extends AnnotationLiteral<CustomConfig> implements CustomConfig {

        public static final Literal INSTANCE = new Literal();

        private static final long serialVersionUID = 1L;

    }
}