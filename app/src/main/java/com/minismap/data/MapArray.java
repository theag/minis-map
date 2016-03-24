package com.minismap.data;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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

    public static void saveInstance(File dir, String filename) {
        try {
            PrintWriter outFile = new PrintWriter(new File(dir, filename));
            for(Map map : instance.maps) {
                outFile.println(map.save(dir));
            }
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }

    public static void loadInstance(File dir, File file) {
        instance = new MapArray();
        try {
            BufferedReader inFile = new BufferedReader(new FileReader(file));
            String line = inFile.readLine();
            while(line != null) {
                instance.maps.add(Map.load(dir, line));
                line = inFile.readLine();
            }
            inFile.close();
        } catch (IOException e) {
            instance.maps.clear();
            e.printStackTrace();
        }
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

    public int indexOf(Map map) {
        return maps.indexOf(map);
    }

    public void deleteMap(File dir, int index) {
        Map map = maps.remove(index);
        map.deleteImage(dir);
        map.unloadBackground();
    }

    public void unloadOtherBackgounds(Map current) {
        for(Map map : maps) {
            if(map != current) {
                map.unloadBackground();
            }
        }
    }
}
