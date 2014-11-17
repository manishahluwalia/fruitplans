package fruit.health.shared.util;

public class BrowserIdCookie
{
    private static final int OBJECT_VERSION = 1;

    private String browserId;

    public BrowserIdCookie ()
    {
    }

    public BrowserIdCookie (String serialized) throws DeserializationHelper.IncompleteData, DeserializationHelper.UnsupportedVersion
    {
        DeserializationHelper helper = new DeserializationHelper(serialized);

        if (OBJECT_VERSION < helper.getObjectVersion())
        {
            throw new DeserializationHelper.UnsupportedVersion(Integer.toString(helper.getObjectVersion()));
        }

        browserId = helper.readString();
    }

    public String serialize ()
    {
        SerializationHelper helper = new SerializationHelper(OBJECT_VERSION);

        assert !SharedUtils.isEmpty(browserId);

        helper.writeString(browserId);

        return helper.getSerialized();
    }

    public String getBrowserId ()
    {
        return browserId;
    }

    public final void setBrowserId (String browserId)
    {
        this.browserId = browserId;
    }
}
