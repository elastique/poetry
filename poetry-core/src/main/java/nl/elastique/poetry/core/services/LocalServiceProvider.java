package nl.elastique.poetry.core.services;

import android.app.Service;
import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.elastique.poetry.core.lang.Callback;

public class LocalServiceProvider
{
    static private final Logger sLogger = LoggerFactory.getLogger(LocalServiceConnector.class);

    public interface ServiceCallback<S extends Service>
    {
        public void onService(S service, ServiceUnbinder serviceUnbinder);
    }

    public static interface ServiceUnbinder
    {
        public void unbind();
    }

    private static class ServiceUnbinderImpl<S extends Service> implements ServiceUnbinder
    {
        private final LocalServiceConnector<S> mLocalServiceProvider;

        public ServiceUnbinderImpl(LocalServiceConnector<S> localServiceProvider)
        {
            mLocalServiceProvider = localServiceProvider;
        }

        @Override
        public void unbind()
        {
            mLocalServiceProvider.unbindService();
        }
    }

    /**
     * Wait for a service to be bound and return it through the {@link ServiceCallback}.
     * The owner of {@link ServiceCallback} is responsible of calling the {@link ServiceUnbinder}.
     *
     * It is not guaranteed on which thread the {@link ServiceCallback} is accessed.
     */
    public static <S extends Service> void bindService(Context context, Class<S> classObject, final ServiceCallback<S> callback)
    {
        bindService(context, classObject, Context.BIND_AUTO_CREATE, callback);
    }

    /**
     * Wait for a service to be bound and return it through the {@link ServiceCallback}.
     * The owner of {@link ServiceCallback} is responsible of calling the {@link ServiceUnbinder}.
     *
     * It is not guaranteed on which thread the {@link ServiceCallback} is accessed.
     */
    public static <S extends Service> void bindService(Context context, final Class<S> classObject, int options, final ServiceCallback<S> callback)
    {
        final LocalServiceConnector<S> local_service_provider = new LocalServiceConnector<>(classObject);

        sLogger.debug(String.format("bindingService %s", classObject.getName()));

        local_service_provider.bindService(context, options, new Callback<S>()
        {
            @Override
            public void onSuccess(final S service)
            {
                sLogger.debug("bindingService onSuccess {}", classObject.getName());

                ServiceUnbinder unbinder = new ServiceUnbinderImpl<>(local_service_provider);

                callback.onService(service, unbinder);
            }

            @Override
            public void onFailure(Throwable caught)
            {
                String message = (caught != null && caught.getMessage() != null) ? caught.getMessage() : "[unknown error]";

                sLogger.error("bindingService onFailure {}", message);
            }
        });
    }
}
