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


    
    public static native <T> void pushToArray(JavaScriptObject array, T val)
    /*-{
        array.push(val);
    }-*/;
    
    
    public static native <T> void setArrayIndex(JavaScriptObject array, int idx, T val)
    /*-{
        array[idx] = val;
    }-*/;
    
    public static <T> JavaScriptObject toJsArray(T[] javaArray) {
        JavaScriptObject arr = JavaScriptObject.createArray();
        for (T t : javaArray) {
            pushToArray(arr, t);
        }
        return arr;
    }
}
