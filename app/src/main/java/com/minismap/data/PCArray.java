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
 * Created by nbp184 on 2016/03/21.
 */
public class PCArray {

    public static final String INDEX = "index";
    private static PCArray instance = null;

    public static void loadInstance(File file) {
        instance = new PCArray();
        try {
            BufferedReader inFile = new BufferedReader(new FileReader(file));
            String line = inFile.readLine();
            while(line != null) {
                instance.pcs.add(PC.load(line));
                line = inFile.readLine();
            }
            inFile.close();
        } catch (IOException e) {
            instance.pcs.clear();
            e.printStackTrace();
        }
    }

    public static PCArray getInstance() {
        if(instance == null) {
            instance = new PCArray();
        }
        return instance;
    }

    public static void saveInstance(File file) {
        try {
            PrintWriter outFile = new PrintWriter(file);
            for(PC pc : instance.pcs) {
                outFile.println(pc.save());
            }
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<PC> pcs;

    private PCArray() {
        pcs = new ArrayList<>();
    }

    public int addPC(int type, String name, String abbreviation) {
        pcs.add(new PC(type, name, abbreviation));
        return pcs.size() - 1;
    }

    public PC getPC(int index) {
        if(index >= pcs.size() || index < 0) {
            return null;
        }
        return pcs.get(index);
    }

    public ListAdapter getAdapter(Context context) {
        return new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, pcs);
    }
}
