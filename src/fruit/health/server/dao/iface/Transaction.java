
package fruit.health.server.dao.iface;

/**
 * Abstracts, begins and encapsulates a DB transaction. A transaction is started
 * as soon as this item is acquired (i.e. you don't need to begin(), or start()
 * it). Use {@link TransactionFactory#get()} to get and begin a transaction.
 * <p/>
 * After the transaction is acquired, it is active. It can be
 * {@link #commit()}ed or {@link #rollBack()}ed, but only once, and exactly
 * one of these operations can be called.
 */
public interface Transaction
{
    /**
     * Commits the transaction
     */
    public void commit ();

    /**
     * Aborts the transaction.
     */
    public void rollBack ();

    /**
     * If the transaction has not yet been committed or rolled back, perform a
     * {@link Transaction#rollBack()}, otherwise do nothing.
     */
    public void rollbackIfActive ();

    /**
     * Returns true iff the transaction is active
     */
    public boolean isActive ();
}
