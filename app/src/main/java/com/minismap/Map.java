package com.minismap;

import java.util.ArrayList;

/**
 * Created by nbp184 on 2016/03/15.
 */
public class Map {

    public String name;

    public Map(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
