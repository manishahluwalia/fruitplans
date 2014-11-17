
package fruit.health.server.dao.impl;

import javax.persistence.EntityTransaction;

import fruit.health.server.dao.iface.Transaction;

public class TransactionImpl implements Transaction
{

    private EntityTransaction transaction;

    public TransactionImpl (EntityTransaction transaction)
    {
        this.transaction = transaction;
        transaction.begin();
    }

    @Override
    public void commit ()
    {
        transaction.commit();
        transaction = null;
    }

    @Override
    public void rollBack ()
    {
        transaction.rollback();
        transaction = null;
    }

    @Override
    public void rollbackIfActive ()
    {
        if (null != transaction && transaction.isActive())
        {
            rollBack();
        }
    }

    @Override
    public boolean isActive ()
    {
        return null != transaction && transaction.isActive();
    }
}
