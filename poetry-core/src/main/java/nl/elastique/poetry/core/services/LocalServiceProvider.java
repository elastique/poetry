package nl.elastique.poetry.core.services;

import android.app.Service;
import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.elastique.poetry.core.concurrent.Callback;

/**
 * A utility class that provides easy binding/access to local (in-process) services.
 */
public class LocalServiceProvider
{
    static private final Logger sLogger = LoggerFactory.getLogger(LocalServiceConnector.class);

    /**
     * The callback for services that become available
     * @param <S> the service type
     */
    public interface ServiceCallback<S extends Service>
    {
        /**
         * @param service the service that is available
         * @param serviceUnbinder the interface to release/unbind the service
         */
        void onService(S service, ServiceUnbinder serviceUnbinder);
    }

    /**
     * An interface to unbind a service.
     */
    public interface ServiceUnbinder
    {
        void unbind();
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
     *
     * @param <S> the Service type
     * @param context the Android context
     * @param serviceClass the service type to bind
     * @param callback the ServiceCallback that provides the service and interface to release it
     */
    public static <S extends Service> void bindService(Context context, Class<S> serviceClass, final ServiceCallback<S> callback)
    {
        bindService(context, serviceClass, callback, Context.BIND_AUTO_CREATE);
    }

    /**
     * Wait for a service to be bound and return it through the {@link ServiceCallback}.
     * The owner of {@link ServiceCallback} is responsible of calling the {@link ServiceUnbinder}.
     *
     * It is not guaranteed on which thread the {@link ServiceCallback} is accessed.
     *
     * @param <S> the Service type
     * @param context the Android context
     * @param serviceClass the service type to bind
     * @param options the options to pass on to Context.bindService()
     * @param callback the ServiceCallback that provides the service and interface to release it
     */
    public static <S extends Service> void bindService(Context context, final Class<S> serviceClass, final ServiceCallback<S> callback, int options)
    {
        final LocalServiceConnector<S> local_service_provider = new LocalServiceConnector<>(serviceClass);

        sLogger.debug("bindService {}", serviceClass.getName());

        local_service_provider.bindService(context, options, new Callback<S>()
        {
            @Override
            public void onSuccess(final S service)
            {
                sLogger.debug("bindService onSuccess {}", serviceClass.getName());

                ServiceUnbinder unbinder = new ServiceUnbinderImpl<>(local_service_provider);

                callback.onService(service, unbinder);
            }

            @Override
            public void onFailure(Throwable caught)
            {
                String message = (caught != null && caught.getMessage() != null) ? caught.getMessage() : "[unknown error]";

                sLogger.error("bindService onFailure {}", message);
            }
        });
    }
}
