package fruit.health.client.google;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import javax.inject.Singleton;

@Singleton
public class GoogleAnalytics implements ValueChangeHandler<String>
{
    private String _domain;
    private String _apiAppName;
    private String _apiAppKey;

    public GoogleAnalytics ()
    {
        History.addValueChangeHandler(this);
    }

    public void init (String domain, String apiAppName, String apiAppKey)
    {
        _domain = "/" + domain + "/";
        _apiAppName = apiAppName;
        _apiAppKey = apiAppKey;
    }

    @Override
    public void onValueChange (ValueChangeEvent<String> event)
    {
        track(event.getValue());
    }

    public void track (String historyToken)
    {
        if (null == historyToken)
        {
            historyToken = "_null_";
        }

        _track(_domain + historyToken);
    }

    private native void _track (String page)
    /*-{
        $wnd._gaq.push(['_trackPageview', page]);
    }-*/;
}
