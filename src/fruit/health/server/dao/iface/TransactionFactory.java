
package fruit.health.server.dao.iface;

import com.google.inject.ImplementedBy;
import com.google.inject.Provider;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.UnitOfWork;

import fruit.health.server.dao.impl.TransactionFactoryImpl;

/**
 * Used to acquire a {@link Transaction}. Use {@link #get()} to get a
 * transaction. A transaction can only be acquired in a context where a
 * {@link UnitOfWork} is already setup, e.g. by using a {@link PersistFilter}.
 */
@ImplementedBy(TransactionFactoryImpl.class)
public interface TransactionFactory extends Provider<Transaction>
{
}
