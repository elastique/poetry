package nl.elastique.poetry.json.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the JSON property (and optionally a child object) to map from
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapFrom
{
	/**
	 * The name of the property.
	 */
	String value();
}
