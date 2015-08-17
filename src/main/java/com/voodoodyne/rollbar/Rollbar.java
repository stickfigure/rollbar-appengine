package com.voodoodyne.rollbar;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Qualifier to mark rollbar-specific injections
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Rollbar {
}
