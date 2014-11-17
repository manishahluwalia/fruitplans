
package fruit.health.server.dao.impl;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import fruit.health.server.dao.iface.Transaction;
import fruit.health.server.dao.iface.TransactionFactory;

@Singleton
public class TransactionFactoryImpl implements TransactionFactory
{

    private Provider<EntityManager> emFactory;

    @Inject
    public TransactionFactoryImpl (Provider<EntityManager> emFactory)
    {
        this.emFactory = emFactory;
    }

    @Override
    public Transaction get ()
    {
        return new TransactionImpl(emFactory.get().getTransaction());
    }
}
