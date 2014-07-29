package nl.elastique.poetry.data.json.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify that a collection has a many-to-many relation ship with an intermediary table to connect them.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ManyToManyField
{
    /**
     * The target type that contains 2 fields (one from each type) with a DatabaseField annotation.
     */
    Class<?> targetType();
}
