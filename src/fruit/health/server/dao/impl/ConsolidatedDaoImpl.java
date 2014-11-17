
package fruit.health.server.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import fruit.health.server.dao.iface.ConsolidatedDao;
import fruit.health.server.logging.LoggingUtils;
import fruit.health.shared.entities.Table;
import fruit.health.shared.entities.User;

public class ConsolidatedDaoImpl implements ConsolidatedDao
{
    private static final Logger logger = LoggerFactory.getLogger(ConsolidatedDaoImpl.class);

    private final Provider<EntityManager> factory;

    @Inject
    ConsolidatedDaoImpl (Provider<EntityManager> factory)
    {
        this.factory = factory;
    }

    
    private <T> T logAndReturn (T retVal)
    {
        return LoggingUtils.logAndReturn(1, logger, retVal);
    }

    private void logEntry ()
    {
        LoggingUtils.logEntry(1, logger);
    }

    private void logExit ()
    {
        LoggingUtils.logExit(1, logger);
    }

    
    
    @Override
    public User getUserById (long userId)
    {
        logEntry();

        EntityManager em = factory.get();

        return logAndReturn(em.find(User.class, userId));
    }

    @Override
    public User getUserByVerifier (String verifier)
    {
        logEntry();

        EntityManager em = factory.get();

        try
        {
            return logAndReturn((User)em.createNamedQuery("findUserByVerifier")
                    .setParameter("verificationToken", verifier)
                    .getSingleResult());
        }
        catch (NoResultException e)
        {
            return logAndReturn(null);
        }
    }

    @Override
    public void createNewUser (User user)
    {
        logEntry();

        EntityManager em = factory.get();

        // checkUserTableConstraints(em, user);

        em.persist(user);

        logExit();
    }

    @Override
    public User getUserByEmail (String email)
    {
        logEntry();

        EntityManager em = factory.get();

        try
        {
            return logAndReturn((User)em.createNamedQuery("findUserByEmail")
                    .setParameter("email", email)
                    .getSingleResult());
        }
        catch (NoResultException e)
        {
            return logAndReturn(null);
        }
    }

    @Override
    public void update (User user)
    {
        logEntry();

        EntityManager em = factory.get();

        em.merge(user);

        logExit();
    }

    @Override
    public void delete (User user)
    {
        logEntry();

        EntityManager em = factory.get();

        em.remove(user);

        logExit();
    }

    

    @Override
    public void createNewTable (Table table)
    {
        logEntry();

        EntityManager em = factory.get();

        em.persist(table);

        logExit();
    }
    
    @Override
    public List<Table> getTables(int startIndex, int limit)
    {
        logEntry();

        EntityManager em = factory.get();

        @SuppressWarnings("unchecked")
        List<Table> tables = em.createNamedQuery("findAllTables")
                       .setFirstResult(startIndex)
                       .setMaxResults(limit)
                       .getResultList();
        
        return logAndReturn(tables);
    }
    
    @Override
    public void update(Table table)
    {
        logEntry();

        EntityManager em = factory.get();

        em.merge(table);

        logExit();
    }

    @Override
    public Table getTableById(long tableId)
    {
        logEntry();

        EntityManager em = factory.get();

        return logAndReturn(em.find(Table.class, tableId));
    }


    @Override
    public List<Table> getTablesLoadingToDb()
    {
        logEntry();
        
        EntityManager em = factory.get();

        @SuppressWarnings("unchecked")
        List<Table> tables = em.createNamedQuery("findAllLoadingToDbTables")
                       .getResultList();
        
        return logAndReturn(tables);
    }
}
