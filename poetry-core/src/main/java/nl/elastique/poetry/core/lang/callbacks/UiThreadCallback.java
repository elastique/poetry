package nl.elastique.poetry.core.lang.callbacks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import nl.elastique.poetry.core.lang.Callback;

public abstract class UiThreadCallback<Type> implements Callback<Type>
{
    private Handler mMainLooperHandler = new Handler(Looper.getMainLooper());

    protected void runOnUiThread(Runnable run)
    {
        mMainLooperHandler.post(run);
    }

    @Override
    public void onSuccess(final Type object)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onSuccessUiThread(object);
            }
        });
    }

    @Override
    public void onFailure(final Throwable caught)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                onFailureUiThread(caught);
            }
        });
    }

    protected abstract void onSuccessUiThread(Type type);

    protected abstract void onFailureUiThread(Throwable caught);
}
