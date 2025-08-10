package az.cybernet.invoice.util;

import java.util.List;

public class GeneralUtil {
    public static boolean isNullOrEmpty(List<?> list){
        return list == null || list.isEmpty();
    }

}
