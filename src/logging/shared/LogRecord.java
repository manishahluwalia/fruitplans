
package logging.shared;

import java.io.Serializable;

public class LogRecord implements Serializable
{
    private static final long serialVersionUID = 1L;

    private java.util.logging.LogRecord logRecord;
    private byte[] encryptedServerSideInformation;

    public java.util.logging.LogRecord getLogRecord ()
    {
        return logRecord;
    }

    public void setLogRecord (java.util.logging.LogRecord logRecord)
    {
        this.logRecord = logRecord;
    }

    public byte[] getEncryptedServerSideInformation ()
    {
        return encryptedServerSideInformation;
    }

    public void setEncryptedServerSideInformation (
            byte[] encryptedServerSideInformation)
    {
        this.encryptedServerSideInformation = encryptedServerSideInformation;
    }
}
