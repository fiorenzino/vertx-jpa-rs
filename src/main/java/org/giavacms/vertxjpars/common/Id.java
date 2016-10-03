package org.giavacms.vertxjpars.common;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by fiorenzo on 02/10/16.
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface Id
{
}
