package fruit.health.client.util;

import fruit.health.client.view.ViewMaster;

public class InputValidation
{
    public static Integer readIntVal(String val, String field, Integer min, Integer max, ViewMaster viewMaster)
    {
        if (null==val || val.isEmpty()) {
            return null;
        }
        
        int v;
        try
        {
            v = Integer.parseInt(val);
        }
        catch (Exception e)
        {
            viewMaster.alertDialog("Bad Input", "Bad value entered for " + field, null, null);
            return null;
        }
        
        if (null!=min && v<min)
        {
            viewMaster.alertDialog("Bad Input", "Bad value entered for " + field+ ". Must be >= " + min, null, null);
            return null;
        }
        
        if (null!=max && v>max)
        {
            viewMaster.alertDialog("Bad Input", "Bad value entered for " + field+ ". Must be <= " + max, null, null);
            return null;
        }

        return v;
    }


    public static String convertToStr(Integer val)
    {
        if (null==val) {
            return "";
        }
        return val.toString();
    }

    public static String makeEmptyStrIfNull(String str)
    {
        if (null==str) {
            return "";
        }
        return str;
    }
}
