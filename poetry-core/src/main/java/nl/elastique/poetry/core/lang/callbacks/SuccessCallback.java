package nl.elastique.poetry.core.lang.callbacks;

import nl.elastique.poetry.core.lang.Callback;

/**
 * A callback where you can only define onSuccess.
 */
abstract public class SuccessCallback<Type> implements Callback<Type>
{
    @Override
    final public void onFailure(Throwable caught)
    {
    }
}
