package com.minismap.data;

import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/03/21.
 */
public class PC {

    public static final int pc = 0;
    public static final int npc = 1;

    private static final String sep = "" +((char)31);

    public static PC load(String line) {
        StringTokenizer tokens = new StringTokenizer(line, sep);
        return new PC(Integer.parseInt(tokens.nextToken()), tokens.nextToken(), tokens.nextToken());
    }

    public String name;
    public String abbreviation;
    public int type;

    public PC(int type, String name, String abbreviation) {
        this.type = type;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public String toString() {
        String str = name +" (" +abbreviation +")";
        if(type == pc) {
            return "(P) " +str;
        } else if(type == npc) {
            return "(N) " +str;
        } else {
            return "(?) " +str;
        }
    }

    public String save() {
        return type +sep +name +sep +abbreviation;
    }
}
