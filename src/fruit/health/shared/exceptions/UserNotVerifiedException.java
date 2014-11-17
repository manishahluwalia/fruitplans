
package fruit.health.shared.exceptions;

import logging.shared.RPCException;

@SuppressWarnings("serial")
public class UserNotVerifiedException extends RPCException
{
    public UserNotVerifiedException ()
    {
        super();
    }

    public UserNotVerifiedException (String msg)
    {
        super(msg);
    }
}
