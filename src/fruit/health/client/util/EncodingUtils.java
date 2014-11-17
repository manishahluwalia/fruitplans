package fruit.health.client.util;

public class EncodingUtils
{
    public static String escapeJava (String string)
    {
        StringBuilder sb = new StringBuilder();
        for (char c : string.toCharArray())
        {
            if (c < 32 || c > 0x7f)
            {
                sb.append("\\u");
                sb.append(Integer.toHexString(0x10000 + c).substring(1));
            }
            else
            {
                switch (c)
                {
                case '\"':
                case '\\':
                    sb.append('\\');
                default:
                    sb.append(c);
                    break;
                }
            }
        }
        return sb.toString();
    }
}
