package com.ideas2it.training.patient.vital.aop.annotation;

import com.ideas2it.training.patient.vital.aop.enums.AuditAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    AuditAction action(); // e.g., Create, Update, Delete
    String description() default "";
}

