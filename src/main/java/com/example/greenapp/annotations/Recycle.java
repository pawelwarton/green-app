package com.example.greenapp.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker signaling that the given method can be called to recycle its object.
 * <p>
 * Recycling offers environmental benefits by minimizing the creation of garbage. :p
 * </p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Recycle {
}

