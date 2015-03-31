package nl.elastique.poetry.core.services;

import android.app.IntentService;
import android.os.IBinder;

/**
 * An {@link IntentService} that is not accessed by other Android processes.
 *
 * @param <T> the deriving class
 */
public abstract class LocalIntentService<T extends IntentService> extends IntentService
{
    public LocalIntentService()
    {
        super(LocalIntentService.class.getSimpleName());
    }

    public LocalIntentService(String name)
    {
        super(name);
    }

    @Override
    final public IBinder onBind(android.content.Intent intent)
    {
        return new LocalBinder<>((T)this);
    }
}
