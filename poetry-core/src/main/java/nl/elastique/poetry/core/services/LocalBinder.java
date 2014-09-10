package nl.elastique.poetry.core.services;

import android.app.Service;
import android.os.Binder;

/**
 * A Binder class to provide the instance of a {@link Service}.
 * This is used for creating local services such as {@link LocalService} or
 * {@link LocalIntentService} and acts like a proxy to the local service instance.
 */
public class LocalBinder<T extends Service> extends Binder
{
    private final T mService;

    public LocalBinder(T service)
    {
        mService = service;
    }

    public T getService()
    {
        return mService;
    }
}