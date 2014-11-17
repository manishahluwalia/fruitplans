package fruit.health.client.util;

import java.util.logging.Logger;

public class JsLoader
{
    public static final Logger logger = Logger.getLogger(JsLoader.class.getName());
    
    private final String scriptSetName;
    private final String[] jsScripts;
    private final boolean async;
    
    private boolean loaded = false;
    private Runnable callback = null;
    
    public JsLoader(String scriptSetName, String[] jsScripts, boolean async)
    {
        this.scriptSetName = scriptSetName;
        this.jsScripts = jsScripts;
        this.async = async;
    }
    
    public void load()
    {
        loadScripts(jsScripts, async, 0);
    }

    public void callbackWhenLoaded(Runnable callback)
    {
        if (loaded)
        {
            logger.finest(scriptSetName + ": scripts already loaded. going straight to callback");
            callback.run();
        }
        else
        {
            logger.finest(scriptSetName + ": deferring callback to after script(s) are initialized");
            this.callback = callback;
        }
    }
    
    private void loadScripts (final String[] scripts, final boolean async, final int idx)
    {
        if (scripts.length>idx)
        {
            String scriptSrc = scripts[idx];
            logger.finer(scriptSetName + ": Loading " + scriptSrc);
            loadScript(scriptSrc, async, new Runnable() {
                @Override
                public void run ()
                {
                    loadScripts(scripts, async, idx+1);
                }
            });
        }
        else
        {
            logger.finer(scriptSetName + ": All scripts loaded");
            loaded = true;
            if (null!=callback)
            {
                logger.finest(scriptSetName + ": invoking deferred script loaded initialized callback");
                callback.run();
            }
        }
    }
    
    /**
     * Load the script at the given url and make the callback
     */
    public static native void loadScript (String scriptUrl, boolean async,
            Runnable callback)
    /*-{
        var body = $wnd.document.body;
        var script = $wnd.document.createElement('script');
        script.src = scriptUrl;
        if (async)
        {
            script.async = true;
        }

        var done = false;
        script.onload = script.onreadystatechange = function () {
            if (!done && (!this.readystate || "loaded"===this.readystate || "complete"===this.readystate))
            {
                done = true;
                script.onload = script.onreadystatechange = null;
                if (callback)
                {
                    $entry(callback.@java.lang.Runnable::run()());
                }
            }
        };

        body.appendChild(script);
    }-*/;
}
