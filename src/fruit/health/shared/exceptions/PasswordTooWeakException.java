
package fruit.health.shared.exceptions;

import logging.shared.RPCException;

@SuppressWarnings("serial")
public class PasswordTooWeakException extends RPCException
{
    public PasswordTooWeakException ()
    {
        super();
    }

    public PasswordTooWeakException (String msg)
    {
        super(msg);
    }

}
