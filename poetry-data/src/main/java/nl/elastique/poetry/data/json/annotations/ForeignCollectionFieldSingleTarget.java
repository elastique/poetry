package nl.elastique.poetry.data.json.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the foreign collection is one of base types, e.g. an array of String objects.
 * This annotation allows you to specify the name of the field where that base type is stored.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignCollectionFieldSingleTarget
{
    String targetField(); // target database column name
}

