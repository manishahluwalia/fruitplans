package fruit.health.client.util;

/**
 * <p>
 * Sometimes in javascript it is necessary to start some asynchronous jobs and continue when all of them have finished.
 * This routine helps with that. It waits till all such execution contexts / jobs have finished and then invokes the success
 * or failure callbacks.
 * </p>
 * 
 * <p>
 * Usage is demonstrated by the following:
 * <pre>
 * final ConvergenceWaiter waiter = new ConvergenceWaiter(2) {
 *   public void onFailure () {
 *       // One or both of the two path below failed
 *   }
 *    
 *   public void onAllSuccess () {
 *       // Both paths below succeeded
 *   }
 * };
 * 
 * Timer t = new Timer() {
 *   public void run () {
 *     waiter.waiteeFinished(true);
 * };
 * 
 * AsyncCallback<Void> callback = new AsyncCallback() {
 *   public void onFailure (Throwable caught) {
 *     waiter.waiteeFinished(false);
 *   }
 *   
 *   public void onSuccess (Void v) {
 *     waiter.waiteeFinished(true);
 *   }
 * };
 * </pre>
 * </p>
 * 
 * <p>
 * As the sample code and comments indicate, a waiter is created and asked to wait for a certain number of execution
 * contexts to finish. As the execution contexts finish execution, they inform the waiter that they are done, with success
 * or failure as appropriate. Once all execution contexts are done, either the {@link #onAllSuccess()} or the {@link #onFailure()}
 * methods are called, as appropriate.
 * </p>
 */
public abstract class ConvergenceWaiter
{
    private int unfinishedWaitees;
    boolean failure = false;
    
    /**
     * Set up a convergence waiter that waits till the specified number of waited-for execution contexts (aka. waitees)
     * have completed.
     * @param waitees The number of waitees to wait for. Must be > 0
     */
    public ConvergenceWaiter (int waitees)
    {
        assert waitees > 0;
        unfinishedWaitees = waitees;
    }
    
    /**
     * Indicated that a waited-for execution context (aka. waitee) has finished. Should be called exactly as many times
     * as there are waitees specified in the constructor.
     * @param success true iff the waitee finished with success
     */
    public void waiteeFinished (boolean success)
    {
        assert unfinishedWaitees > 0;
        
        if (!success)
        {
            failure = true;
        }
        
        if (0==--unfinishedWaitees)
        {
            if (failure)
            {
                onFailure();
            }
            else
            {
                onAllSuccess();
            }
        }
    }
    
    /**
     * Called iff all the execution contexts waited for finished successfully
     */
    public abstract void onAllSuccess ();
    
    /**
     * Called iff at least one waited for execution context ended unsuccessfully
     */
    public abstract void onFailure ();
}
