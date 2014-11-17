
package fruit.health.server.cloner.api;

@SuppressWarnings("serial")
public class CloningError extends RuntimeException
{

    public CloningError (String msg)
    {
        super(msg);
    }

    public CloningError (String msg, Throwable cause)
    {
        super(msg, cause);
    }
}
