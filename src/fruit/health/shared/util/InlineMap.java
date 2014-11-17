package fruit.health.shared.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/*
 * This class should be IsA(Map<String, Object>) as opposed to hasA(Map<String,
 * Object>) for greater convenience. However, that makes this class
 * Serializable, and anyone creating an inline map will either have to use a
 * 
 * @SuppressWarning("serializable"), or declare a serialVersionUID, or live with
 * a warning. All of which are painful options.
 */
public class InlineMap
{
    private Map<String, Object> map = new HashMap<String, Object>();

    public void _ (String key, Object value)
    {
        map.put(key, value);
    }

    /**
     * @see HashMap#entrySet()
     */
    public Set<Entry<String, Object>> entrySet ()
    {
        return map.entrySet();
    }
}
