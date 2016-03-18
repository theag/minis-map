package com.minismap;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nbp184 on 2016/03/15.
 */
public class MapArray {

    public static final String INDEX = "map index";

    private static MapArray instance = null;

    public static MapArray getInstance() {
        if(instance == null) {
            instance = new MapArray();
        }
        return instance;
    }

    private ArrayList<Map> maps;

    private MapArray() {
        maps = new ArrayList<>();
    }

    public int addMap(String name) {
        maps.add(new Map(name));
        return maps.size() - 1;
    }

    public Map getMap(int index) {
        if(index >= maps.size() || index < 0) {
            return null;
        }
        return maps.get(index);
    }

    public ListAdapter getAdapter(Context context) {
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, maps);
    }
}
