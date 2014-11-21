
package fruit.health.client.places;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.user.client.Window;

import fruit.health.client.activities.CompareActivity;
import fruit.health.client.entities.PlanData;
import fruit.health.client.gin.AppGinjector;
import fruit.health.client.mvp.BasePlace;
import fruit.health.client.mvp.BasePlaceTokenizer;
import fruit.health.shared.util.DeserializationHelper;
import fruit.health.shared.util.DeserializationHelper.IncompleteData;
import fruit.health.shared.util.DeserializationHelper.UnsupportedVersion;
import fruit.health.shared.util.SerializationHelper;

public class compare extends BasePlace
{
    private static final Logger logger = Logger.getLogger(compare.class.toString());
    
    private static final int OBJECT_VERSION = 0;
    
    private final List<PlanData> plans;

    public compare (List<PlanData> plans)
    {
        this.plans = plans;
    }

    public static class Tokenizer extends BasePlaceTokenizer<compare>
            implements PlaceTokenizer<compare>
    {
        @Override
        public compare getBasePlace (String token)
        {
            LinkedList<PlanData> plans = new LinkedList<PlanData>();
            DeserializationHelper deserializer;
            try
            {
                deserializer = new DeserializationHelper(token);
                if (OBJECT_VERSION!=deserializer.getObjectVersion()) {
                    throw new UnsupportedVersion("Cannot read plans list of version " + deserializer.getObjectVersion());
                }
                int numPlans = (int) deserializer.readLong();
                for ( ; numPlans-->0; ) {
                    PlanData p = new PlanData();
                    p.planName = deserializer.readString();
                    p.premium = (int) deserializer.readLong();
                    p.deductible = (int) deserializer.readLong();
                    p.copay = (int) deserializer.readLong();
                    p.oopMax = (int) deserializer.readLong();
                    plans.add(p);
                }
            }
            catch (IncompleteData | UnsupportedVersion | NumberFormatException e)
            {
                logger.log(Level.SEVERE, "Can't deserialize place with token: " + token, e);
                Window.alert("Error in URL. Link is bad or expired");
            }
            
            return new compare(plans);
        }
    }

    @Override
    public Activity getActivity (AppGinjector injector)
    {
        return new CompareActivity(this, injector);
    }

    @Override
    public String getPlaceName ()
    {
        return constants.getComparePlaceTitle();
    }

    public List<PlanData> getPlans () {
        return plans;
    }
    
    @Override
    public String getToken () {
        SerializationHelper serializer = new SerializationHelper(OBJECT_VERSION);
        serializer.writeLong(plans.size());
        for (PlanData p : plans) {
            serializer.writeString(p.planName);
            serializer.writeLong(p.premium);
            serializer.writeLong(p.deductible);
            serializer.writeLong(p.copay);
            serializer.writeLong(p.oopMax);
        }
        return serializer.getSerialized();
    }
}
