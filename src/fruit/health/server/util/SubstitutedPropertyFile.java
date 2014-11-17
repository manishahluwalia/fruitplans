
package fruit.health.server.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Some of the property files we have need to have their values filled in by
 * maven, based on substitutions performed at build time by using settings from
 * setttings.xml file. When running under dev mode under Eclipse, these
 * substitutions are not performed. This class provides a wrapper around
 * property files to load the property file and read values from it. If we are
 * in eclipse and the substitution has not happened, then a given default value
 * is returned instead.
 * <p/>
 * For this to work, the property file must be dropped in the top level
 * "resources" directory. Any property must have the following scheme:
 * 
 * <pre>
 * property.name=${property.name}
 * </pre>
 * 
 * The maven build process should substitute the required value.
 */
@SuppressWarnings("serial")
public class SubstitutedPropertyFile extends Properties
{
    /*
     * Unfortunately, the dev mode for eclipse and the maven-gae-plugin started
     * Jetty run with different classpaths. We resolve this by loading the file
     * with its path relative to working directory. Fortunately, they both (can
     * be configured to) run with the same working directory.
     */
    private static final String PROPERTY_FILE_BASE = "WEB-INF/classes/";

    /**
     * @param propertyFileName The file name of the property file. This file should
     *   be placed in the directory given by {@link #PROPERTY_FILE_BASE} after
     *   compilation.
     * @return The loaded properties.
     */
    public SubstitutedPropertyFile (String propertyFileName)
    {
        super();
        String path = PROPERTY_FILE_BASE + propertyFileName;
        try
        {
            super.load(new FileInputStream(path));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Can't read property file " + path, e);
        }
    }

    /**
     * @param propertyName The name of the property to read
     * @param defaultValue If the property file / value has not been initialized by
     *   maven, return this value instead
     * @return The value of the propert in the file, null if there isn't any,
     *   the default value if the property file wasn't initialized
     */
    public String getProperty (String propertyName, String defaultValue)
    {
        String value = super.getProperty(propertyName);

        if (null != value)
        {
            if (value.equals("${" + propertyName + "}"))
            {
                value = defaultValue;
            }
        }

        return value;
    }
}
