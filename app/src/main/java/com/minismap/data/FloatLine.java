package com.minismap.data;

/**
 * Created by nbp184 on 2016/03/24.
 */
public class FloatLine {

    public float x1;
    public float y1;
    public float x2;
    public float y2;

    public FloatLine() {
        x1 = 0;
        y1 = 0;
        x2 = 0;
        y2 = 0;
    }

    public FloatLine(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof FloatLine) {
            FloatLine fp = (FloatLine)o;
            return x1 == fp.x1 && y1 == fp.y1 && x2 == fp.x2 && y2 == fp.y2;
        } else {
            return false;
        }
    }

}
