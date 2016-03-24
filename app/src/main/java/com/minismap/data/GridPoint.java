package com.minismap.data;

/**
 * Created by nbp184 on 2016/03/24.
 */
public class GridPoint {

    public int x;
    public int y;

    public GridPoint() {
        x = 0;
        y = 0;
    }

    public GridPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof GridPoint) {
            GridPoint gp = (GridPoint)o;
            return x == gp.x && y == gp.y;
        } else {
            return false;
        }
    }

    public boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    public static GridPoint load(String line) {
        int index = line.indexOf(",");
        return new GridPoint(Integer.parseInt(line.substring(0, index)), Integer.parseInt(line.substring(index+1)));
    }
}
