
package logging.shared;

@SuppressWarnings("serial")
public abstract class RPCException extends Exception
{
    private byte[] serverSideInformation = null;

    public RPCException ()
    {
        storeServerSideInformation();
    }

    public RPCException (String msg)
    {
        super(msg);
        storeServerSideInformation();
    }

    public RPCException (Throwable cause)
    {
        super(cause);
        storeServerSideInformation();
    }

    public RPCException (String msg, Throwable cause)
    {
        super(msg, cause);
        storeServerSideInformation();
    }

    private void storeServerSideInformation ()
    {
        serverSideInformation = RPCExceptionEncodingUtil.encryptServerSideInformation(this);
    }

    public byte[] getEncryptedServerSideInformation ()
    {
        return serverSideInformation;
    }
}
