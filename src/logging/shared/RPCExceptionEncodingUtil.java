
package logging.shared;

public class RPCExceptionEncodingUtil
{
    public static interface EncrypterDecrypter
    {
        public byte[] getEncryptedServerSideInformation (Throwable thrown);

        public String getDecryptedServerSideInformation (
                byte[] encryptedServerSideInformation);
    }

    private static EncrypterDecrypter encrypterDecrypter = null;

    public static void setEncrypterDecrypter (
            EncrypterDecrypter encrypterDecrypter)
    {
        RPCExceptionEncodingUtil.encrypterDecrypter = encrypterDecrypter;
    }

    public static byte[] encryptServerSideInformation (Throwable thrown)
    {
        if (null == encrypterDecrypter)
        {
            return null;
        }
        return encrypterDecrypter.getEncryptedServerSideInformation(thrown);
    }

    public static String decryptEncryptedServerSideInformation (
            byte[] encryptedServerSideInformation)
    {
        if (null == encrypterDecrypter)
        {
            return null;
        }
        return encrypterDecrypter.getDecryptedServerSideInformation(encryptedServerSideInformation);
    }
}
