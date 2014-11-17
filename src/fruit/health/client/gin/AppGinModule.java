package fruit.health.client.gin;

import javax.inject.Singleton;

import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryHandler.Historian;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.inject.Provides;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import fruit.health.client.AppPlaceDispatcher;
import fruit.health.client.mvp.AppActivityMapper;
import fruit.health.client.mvp.PlaceHistoryMapperWrapper;
import fruit.health.client.rpc.InitService;
import fruit.health.client.rpc.InitServiceAsync;
import fruit.health.client.rpc.RepeatingCsrfSafeRpcBuilder;
import fruit.health.client.rpc.TableService;
import fruit.health.client.rpc.TableServiceAsync;
import fruit.health.client.rpc.UserService;
import fruit.health.client.rpc.UserServiceAsync;
import fruit.health.client.view.ViewMaster;

public class AppGinModule extends AbstractGinModule {

	/* (non-Javadoc)
	 * @see com.google.gwt.inject.client.AbstractGinModule#configure()
	 */
    @Override
    protected void configure ()
    {
        bind(EventBus.class).to(SimpleEventBus.class).in(Singleton.class);
        bind(ActivityMapper.class).to(AppActivityMapper.class);
        bind(PlaceHistoryMapper.class).to(PlaceHistoryMapperWrapper.class).in(Singleton.class);
        bind(ViewMaster.class).in(Singleton.class);
        bind(Historian.class).to(PlaceHistoryHandler.DefaultHistorian.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    PlaceController getPlaceController (AppGinjector injector)
    {
        return new AppPlaceDispatcher(injector);
    }

    @Provides
    @Singleton
    PlaceHistoryHandler getPlaceHistoryHandler (AppGinjector injector)
    {
        return new PlaceHistoryHandler(injector.getPlaceHistoryMapper());
    }

    @SuppressWarnings("unchecked")
    private <A> A getWrappedService(Object syncServiceClass)
    {
        return (A)RepeatingCsrfSafeRpcBuilder.createService((ServiceDefTarget)syncServiceClass);
    }

    @Provides
    @Singleton
    InitServiceAsync getInitService()
    {
        return getWrappedService(GWT.create(InitService.class));
    }

    @Provides
    @Singleton
    UserServiceAsync getUserService()
    {
        return getWrappedService(GWT.create(UserService.class));
    }

    @Provides
    @Singleton
    TableServiceAsync getUploadService()
    {
        return getWrappedService(GWT.create(TableService.class));
    }
}
