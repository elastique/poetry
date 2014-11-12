package nl.elastique.poetry.core.lang;

/**
 * A generic callback that provides a value on success and a Throwable on error.
 * Which thread the callback happens on depends on the parent that is calling this interface.
 *
 * @param <Type> the type to provide on success
 */
public interface Callback<Type>
{
    /**
     * Called when an operation has succeeded
     *
     * @param object the returned data (this could be null, depending on the implementation)
    */
    public void onSuccess(Type object);

    /**
     * Called when an operation has failed
     *
     * @param caught the Throwable that has caused the failure (this is never null)
     */
    public void onFailure(Throwable caught);
}
