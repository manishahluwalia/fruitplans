
package fruit.health.shared.exceptions;

import logging.shared.RPCException;

@SuppressWarnings("serial")
public class InvalidLoginCredsException extends RPCException
{
    public InvalidLoginCredsException ()
    {
        super();
    }

    public InvalidLoginCredsException (String msg)
    {
        super(msg);
    }

}
