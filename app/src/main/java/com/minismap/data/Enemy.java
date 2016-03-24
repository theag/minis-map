package com.minismap.data;


import java.util.StringTokenizer;

/**
 * Created by nbp184 on 2016/03/21.
 */
public class Enemy {

    private static final String sep = ""+((char)31);

    public static Enemy load(String line) {
        StringTokenizer tokens = new StringTokenizer(line, sep);
        return new Enemy(tokens.nextToken(), tokens.nextToken(), Integer.parseInt(tokens.nextToken()), Integer.parseInt(tokens.nextToken()));
    }

    public String name;
    public String abbreviation;
    public GridPoint location;

    public Enemy(String name, String abbreviation, int x, int y) {
        this.name = name;
        this.abbreviation = abbreviation;
        location = new GridPoint(x, y);
    }

    public String save() {
        return name +sep +abbreviation +sep +location.x + sep +location.y;
    }
}
