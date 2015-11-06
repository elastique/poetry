package nl.elastique.poetry.utils;

import java.security.InvalidParameterException;
import java.util.Date;
import java.util.List;

public class QueryUtils
{
    /**
     * Convert an Object to a String so that it can be used as a query parameter.
     * This method supports objects instantiated or derived from:
     * Integer, Long, Float, Double, Boolean, Short, Byte, CharSequence and Date
     *
     * @param object the object to convert
     * @return the String representing the input object
     * @throws InvalidParameterException when the input object is not supported
     */
    public static String parseAttribute(Object object) throws InvalidParameterException
    {
        if (Integer.class.isAssignableFrom(object.getClass()))
        {
            return Integer.toString((Integer)object);
        }
        else if (Long.class.isAssignableFrom(object.getClass()))
        {
            return Long.toString((Long)object);
        }
        else if (Float.class.isAssignableFrom(object.getClass()))
        {
            return Float.toString((Float)object);
        }
        else if (Double.class.isAssignableFrom(object.getClass()))
        {
            return Double.toString((Double)object);
        }
        else if (Boolean.class.isAssignableFrom(object.getClass()))
        {
            return Boolean.toString((Boolean)object);
        }
        else if (Short.class.isAssignableFrom(object.getClass()))
        {
            return Short.toString((Short)object);
        }
        else if (Byte.class.isAssignableFrom(object.getClass()))
        {
            return Byte.toString((Byte)object);
        }
        else if (CharSequence.class.isAssignableFrom(object.getClass()) || Date.class.isAssignableFrom(object.getClass()))
        {
            return object.toString();
        }
        else
        {
            throw new InvalidParameterException("parameter type not supported: " + object.getClass().getName());
        }
    }

    /**
     * Creates an "IN (?, ...)" query and outputs target ids.
     *
     * @param targetIds a list of IDs for the query
     * @param outputQueryArgs the array that will hold the output values for the query
     * @return the query part
     */
    public static String createInClause(List<Object> targetIds, String[] outputQueryArgs)
    {
        if (outputQueryArgs.length != targetIds.size())
        {
            throw new RuntimeException("targetIds and targetIdArgs must be the same size");
        }

        StringBuilder in_clause_builder = new StringBuilder(5 + (targetIds.size()));

        in_clause_builder.append("IN (");

        for (int i = 0; i < targetIds.size(); ++i)
        {
            outputQueryArgs[i] = parseAttribute(targetIds.get(i));

            in_clause_builder.append('?');

            if (i != (targetIds.size() - 1))
            {
                in_clause_builder.append(',');
            }
        }

        in_clause_builder.append(')');

        return in_clause_builder.toString();
    }
}
