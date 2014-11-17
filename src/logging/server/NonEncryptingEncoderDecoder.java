
package logging.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import logging.shared.RPCExceptionEncodingUtil.EncrypterDecrypter;

public class NonEncryptingEncoderDecoder implements EncrypterDecrypter
{

    @Override
    public byte[] getEncryptedServerSideInformation (Throwable thrown)
    {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        thrown.printStackTrace(new PrintStream(bs, false)); // Use default
                                                            // encoding on the
                                                            // server
        try
        {
            bs.flush();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bs.toByteArray();
    }

    @Override
    public String getDecryptedServerSideInformation (
            byte[] encryptedServerSideInformation)
    {
        return new String(encryptedServerSideInformation); // Use default
                                                           // encoding on the
                                                           // server
    }

}
