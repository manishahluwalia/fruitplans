package fruit.health.server.dao.impl;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PrePut;
import com.google.appengine.api.datastore.PutContext;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Timestamper
{
    private final static Logger logger = LoggerFactory.getLogger(Timestamper.class);

    @PrePut
    void addTimeStamp(PutContext context)
    {
        Entity e = context.getCurrentElement();
        logger.info("entity kind: " + e.getKind() + "  ns: " + e.getNamespace() + "  key: " + e.getKey());
        e.setProperty("_ts", new Date());
    }
}
