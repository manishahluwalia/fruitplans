package fruit.health.client.mvp;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public abstract class BasePlaceTokenizer<T extends BasePlace> implements PlaceTokenizer<T>
{
    @Override
    public final T getPlace (String token)
    {
        return getBasePlace(token);
    }

    @Override
    public final String getToken (T place)
    {
        return place.getToken();
    }

    /**
     * Like {@link PlaceTokenizer#getPlace(String)}, but returns a
     * {@link BasePlace} instead of a {@link Place}
     * @param token The token holding this place's internal state
     */
    protected abstract T getBasePlace (String token);
}
