package fruit.health.shared.util;

import java.util.LinkedList;


public class SharedUtils
{
    public static boolean isEmpty(String str)
    {
        if (null==str || str.isEmpty())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isValidEmail(String email)
    {
        // TODO Auto-generated method stub
        return true;
    }

    public static <T> LinkedList<T> listAppend(LinkedList<T> list, T elem)
    {
        if (null==list)
        {
            list = new LinkedList<>();
        }
        
        list.add(elem);
        
        return list;
    }
}
