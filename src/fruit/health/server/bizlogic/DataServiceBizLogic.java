package fruit.health.server.bizlogic;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fruit.health.server.dao.iface.ConsolidatedDao;
import fruit.health.server.dao.iface.TransactionFactory;

@Singleton
public class DataServiceBizLogic
{
    private static final Logger logger = LoggerFactory.getLogger(DataServiceBizLogic.class);
    
    private final ConsolidatedDao dao;
    private final TransactionFactory txFactory;
    
    @Inject
    public DataServiceBizLogic(ConsolidatedDao dao, TransactionFactory txFactory) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        this.dao = dao;
        this.txFactory = txFactory;
    }
    
}
