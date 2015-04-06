package nl.elastique.poetry.core.services;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.elastique.poetry.core.annotations.Nullable;
import nl.elastique.poetry.core.concurrent.Callback;

/**
 * Manages the {@link ServiceConnection} of a {@link LocalService} or {@link LocalIntentService}.
 * Note: Use {@link LocalServiceProvider} if you want access to a local service.
 */
public class LocalServiceConnector<S extends Service>
{
    static private final Logger sLogger = LoggerFactory.getLogger(LocalServiceConnector.class);

    private LocalServiceConnection mServiceConnection;

    private final Class<S> mServiceClass;

    private class LocalServiceConnection implements ServiceConnection
    {
        private final Context mContext;

        private final Callback<S> mCallback;

        private S mService;

        public LocalServiceConnection(Context context, @Nullable Callback<S> callback)
        {
            mContext = context;
            mCallback = callback;
        }

        public void onServiceConnected(ComponentName className, IBinder binder)
        {
            try
            {
                sLogger.debug("service connected: " + mServiceClass.getName());

                LocalBinder<S> local_binder = (LocalBinder<S>)binder;

                mService = local_binder.getService();

                mCallback.onSuccess(mService);
            }
            catch (Exception e)
            {
                sLogger.warn("exception during service connection of " + mServiceClass.getName());

                mCallback.onFailure(e);
            }
        }

        public void onServiceDisconnected(ComponentName className)
        {
            sLogger.debug("service disconnected: " + mServiceClass.getName());
        }

        public Context getContext()
        {
            return mContext;
        }
    }

    public LocalServiceConnector(final Class<S> serviceClass)
    {
        mServiceClass = serviceClass;
    }

    public void bindService(Context context, int options, @Nullable final Callback<S> callback)
    {
        if (mServiceConnection == null)
        {
            try
            {
                mServiceConnection = new LocalServiceConnection(context, callback);

                Intent intent = new Intent(context, mServiceClass);
                context.bindService(intent, mServiceConnection, options);
            }
            catch (Exception e)
            {
                mServiceConnection = null;

                callback.onFailure(e);
            }
        }
        else
        {
            callback.onFailure(new Exception("service already bound"));
        }
    }

    public void unbindService()
    {
        if (mServiceConnection != null)
        {
            mServiceConnection.getContext().unbindService(mServiceConnection);
            mServiceConnection = null;
        }
        else
        {
            sLogger.warn("service not bound");
        }
    }
}
