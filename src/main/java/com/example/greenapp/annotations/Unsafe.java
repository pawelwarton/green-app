package com.example.greenapp.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Immutability is not very green. To save the planet, just like we need to
 * recycle, we need to use mutability. Here I take it to the next level, and
 * SHARE those mutable object, which results in shared mutability a.k.a
 * THE ROOT OF ALL EVIL!!!
 * <br>
 * Marks error-prone pieces of code.
 * <br>
 * Addition details should be provided in a nearby comment.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface Unsafe {
}
