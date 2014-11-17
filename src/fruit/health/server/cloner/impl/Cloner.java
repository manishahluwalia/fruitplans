package fruit.health.server.cloner.impl;

import java.util.Map;
import java.util.Set;

import fruit.health.server.cloner.api.CloningError;
import fruit.health.server.cloner.api.Projection;

abstract class Cloner
{
    public abstract boolean cloneNeededForGwt (Object object,
            Class<? extends Projection> projection, Set<Object> alreadyChecked);

    public abstract Object deepClone (Object source,
            Class<? extends Projection> projection,
            Map<Object, Object> alreadyXlated);

    public void copyFromClient (Object serverDestinationObject,
            Object clientSourceObject, Class<? extends Projection> projection)
    {
        throw new CloningError("Method copyFromClient() not overridden in "
                + this.getClass().getName());
    }

}
