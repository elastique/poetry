package nl.elastique.poetry.core.services;

import android.app.Service;
import android.os.IBinder;

/**
 * A {@link Service} that is not accessed by other Android processes.
 *
 * @param <T> the deriving class
 */
public abstract class LocalService<T extends Service> extends Service
{
    @Override
    final public IBinder onBind(android.content.Intent intent)
    {
        return new LocalBinder<>((T)this);
    }
}
