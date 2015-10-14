package nl.elastique.poetry.json;

public class JsonPathException extends Exception
{
    public JsonPathException(String message)
    {
        super(message);
    }

    public JsonPathException(String message, Throwable parent)
    {
        super(message, parent);
    }
}
