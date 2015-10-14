package nl.elastique.poetry.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to document that the specified target can be optionally null.
 */
@Retention(RetentionPolicy.CLASS)
@Target( { ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER } )
public @interface Nullable
{
}
