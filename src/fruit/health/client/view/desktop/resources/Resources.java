
package fruit.health.client.view.desktop.resources;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;


public interface Resources extends ClientBundle
{
    static final Resources INSTANCE = GWT.create(Resources.class);

    @Source({"styles/Components.css"})
    StyleCss style();
}
