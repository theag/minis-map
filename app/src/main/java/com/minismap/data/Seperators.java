package com.minismap.data;

/**
 * Created by nbp184 on 2016/03/29.
 */
public class Seperators {

    //PCs
    public static final String PC = "" +((char)31);

    //Maps
    public static final String MAP = ""+((char)28);
    public static final String INSIDE_MAP = ""+((char)29);
    public static final String NEW_ARRAY_INDICATOR = ""+((char)1);
    public static final String NULL = ""+((char)0);
    public static final String INSIDE_FOG = "" + ((char)30);

    public static String nullConvert(String str) {
        if(str == null) {
            return NULL;
        } else if(str.compareTo(NULL) == 0) {
            return null;
        } else {
            return str;
        }
    }

    public static String emptyConvert(String str) {
        if(str == null || str.isEmpty()) {
            return NULL;
        } else if(str.compareTo(NULL) == 0) {
            return "";
        } else {
            return str;
        }
    }

}
