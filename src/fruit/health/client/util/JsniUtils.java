package fruit.health.client.util;

import com.google.gwt.core.client.JavaScriptObject;

public class JsniUtils
{
    public static native void setMapValue(JavaScriptObject map, String key, JavaScriptObject val)
    /*-{
        map[key] = val;
    }-*/;

    public static native void setMapValue(JavaScriptObject map, String key, int val)
    /*-{
        map[key] = val;
    }-*/;

    public static native void setMapValue(JavaScriptObject map, String key, String val)
    /*-{
        map[key] = val;
    }-*/;

    public static native void setMapValue(JavaScriptObject map, String key, Object val)
    /*-{
        map[key] = val;
    }-*/;


    
    public static native void pushToArray(JavaScriptObject array, JavaScriptObject val)
    /*-{
        array.push(val);
    }-*/;
    
    public static native void pushToArray(JavaScriptObject array, int val)
    /*-{
        array.push(val);
    }-*/;
    
    public static native void pushToArray(JavaScriptObject array, String val)
    /*-{
        array.push(val);
    }-*/;
    
    public static native void pushToArray(JavaScriptObject array, Object val)
    /*-{
        array.push(val);
    }-*/;
    

    
    public static native void setArrayIndex(JavaScriptObject array, int idx, JavaScriptObject val)
    /*-{
        array[idx] = val;
    }-*/;

    public static native void setArrayIndex(JavaScriptObject array, int idx, int val)
    /*-{
        array[idx] = val;
    }-*/;

    public static native void setArrayIndex(JavaScriptObject array, int idx, String val)
    /*-{
        array[idx] = val;
    }-*/;

    public static native void setArrayIndex(JavaScriptObject array, int idx, Object val)
    /*-{
        array[idx] = val;
    }-*/;
}
