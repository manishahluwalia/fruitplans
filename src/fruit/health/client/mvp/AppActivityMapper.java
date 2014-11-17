package fruit.health.client.mvp;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import fruit.health.client.gin.AppGinjector;
import fruit.health.client.view.ViewMaster;

@Singleton
public class AppActivityMapper implements ActivityMapper {
	
	private final AppGinjector injector;
	private final ViewMaster viewMaster;
	
	/**
	 * AppActivityMapper associates each Place with its corresponding
	 * {@link Activity}
	 */
	@Inject
	public AppActivityMapper(AppGinjector injector) {
		super();
		this.injector = injector;
		this.viewMaster = injector.getViewMaster();
	}

	@Override
	public Activity getActivity(Place _place) {
	    BasePlace place = (BasePlace)_place;
	    viewMaster.setViewName(place.getPlaceName());
		return (place).getActivity(injector);
	}
}
