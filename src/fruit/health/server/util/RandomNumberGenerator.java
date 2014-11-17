package fruit.health.server.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomNumberGenerator {

    private static final SecureRandom rng;
    static
    {
        try
        {
            rng = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static byte[] getBytes(int numBytes) {
        byte[] bytes = new byte[numBytes];
        rng.nextBytes(bytes);
        return bytes;
    }

}
