package fruit.health.client.mvp;


import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceHistoryMapper;

import fruit.health.shared.util.SharedUtils;

public class PlaceHistoryMapperWrapper implements PlaceHistoryMapper
{
    private static final char BANG = '!';
    private final PlaceHistoryMapper mapper = GWT.create(AppPlaceHistoryMapper.class);

    @Override
    public Place getPlace (String token)
    {
        if (!SharedUtils.isEmpty(token))
        {
            if (BANG == token.charAt(0))
            {
                token = token.substring(1);
            }

            if (!token.contains(":"))
            {
                token = token + ":";
            }
        }

        return mapper.getPlace(token);
    }

    @Override
    public String getToken (Place place)
    {
        String token = BANG + mapper.getToken(place);
        return token;
    }
}
