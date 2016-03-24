package com.minismap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.minismap.data.Map;
import com.minismap.data.MapArray;
import com.minismap.data.PC;
import com.minismap.data.PCArray;

import java.io.File;

public class MainActivity extends AppCompatActivity implements EditPCDialog.OnClickListener {

    private static final int mapTab = 0;
    private static final int pcsTab = 1;
    private static final String CURRENT_TAB = "current tab";
    private static final int MAP_REQUEST = 1;

    private int currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = new File(getFilesDir(), "pcs.txt");
        if(file.exists()) {
            PCArray.loadInstance(file);
        } else {
            PCArray.getInstance();
        }
        file = new File(getFilesDir(), "maps.txt");
        if(file.exists()) {
            MapArray.loadInstance(getFilesDir(), file);
        } else {
            MapArray.getInstance();
        }

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        currentTab = sharedPref.getInt(CURRENT_TAB, mapTab);
        if(currentTab == pcsTab) {
            currentTab =  mapTab;
            tabClick(findViewById(R.id.tab_pcs));
        } else {
            currentTab = pcsTab;
            tabClick(findViewById(R.id.tab_maps));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(CURRENT_TAB, currentTab);
        editor.commit();
        PCArray.saveInstance(new File(getFilesDir(), "pcs.txt"));
        MapArray.saveInstance(getFilesDir(), "maps.txt");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.action_new:
                if(currentTab == mapTab) {
                    intent = new Intent(this, EditActivity.class);
                    intent.putExtra(MapArray.INDEX, MapArray.getInstance().addMap("Untitled"));
                    startActivityForResult(intent, MAP_REQUEST);
                } else if(currentTab == pcsTab) {
                    EditPCDialog frag = new EditPCDialog();
                    Bundle args = new Bundle();
                    args.putInt(EditPCDialog.INDEX, -1);
                    frag.setArguments(args);
                    frag.show(getSupportFragmentManager(), "dialog");
                }
                break;
            case  R.id.action_settings:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MAP_REQUEST) {
            if(resultCode == RESULT_OK) {
                ListView lv = (ListView)findViewById(R.id.listView);
                ArrayAdapter<Map> adapter = (ArrayAdapter) lv.getAdapter();
                adapter.notifyDataSetChanged();
            }
        }
    }



    public void tabClick(View view) {
        int accent, white;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            accent = getResources().getColor(R.color.colorAccent, null);
            white = getResources().getColor(android.R.color.white, null);
        } else {
            accent = getResources().getColor(R.color.colorAccent);
            white = getResources().getColor(android.R.color.white);
        }
        ListView lv = (ListView)findViewById(R.id.listView);
        TextView tv = (TextView)view;
        tv.setTextColor(white);
        tv.setBackgroundResource(R.color.colorAccent);
        if(view.getId() == R.id.tab_pcs && currentTab == mapTab) {
            tv = (TextView)findViewById(R.id.tab_maps);
            tv.setTextColor(accent);
            tv.setBackgroundResource(R.drawable.accent_rectangle);
            lv.setAdapter(PCArray.getInstance().getAdapter(this));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView lv = (ListView) parent;
                    ArrayAdapter<PC> adapter = (ArrayAdapter) lv.getAdapter();
                    PC pc = adapter.getItem(position);
                    EditPCDialog frag = new EditPCDialog();
                    Bundle args = new Bundle();
                    args.putInt(EditPCDialog.INDEX, position);
                    args.putInt(EditPCDialog.TYPE, pc.type);
                    args.putString(EditPCDialog.NAME, pc.name);
                    args.putString(EditPCDialog.ABBREVIATION, pc.abbreviation);
                    frag.setArguments(args);
                    frag.show(getSupportFragmentManager(), "dialog");
                }
            });
            currentTab = pcsTab;
        } else if(view.getId() == R.id.tab_maps && currentTab == pcsTab) {
            tv = (TextView)findViewById(R.id.tab_pcs);
            tv.setTextColor(accent);
            tv.setBackgroundResource(R.drawable.accent_rectangle);
            lv.setAdapter(MapArray.getInstance().getAdapter(this));
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView lv = (ListView) parent;
                    ArrayAdapter<Map> adapter = (ArrayAdapter) lv.getAdapter();
                    Intent intent = new Intent(parent.getContext(), EditActivity.class);
                    intent.putExtra(MapArray.INDEX, position);
                    startActivityForResult(intent, MAP_REQUEST);
                }
            });
            currentTab = mapTab;
        }
    }

    Bundle savedData;

    @Override
    public void onPCChange(Bundle data) {
        if(data.containsKey(EditPCDialog.ERROR)) {
            savedData = data;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(data.getString(EditPCDialog.ERROR))
                    .setTitle("Error")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditPCDialog frag = new EditPCDialog();
                            frag.setArguments(savedData);
                            frag.show(getSupportFragmentManager(), "dialog");
                        }
                    });
            builder.create().show();
        } else {
            int index = data.getInt(EditPCDialog.INDEX);
            if(index < 0) {
                PCArray.getInstance().addPC(data.getInt(EditPCDialog.TYPE), data.getString(EditPCDialog.NAME), data.getString(EditPCDialog.ABBREVIATION));
            } else {
                PC pc = PCArray.getInstance().getPC(index);
                pc.type = data.getInt(EditPCDialog.TYPE);
                pc.name = data.getString(EditPCDialog.NAME);
                pc.abbreviation = data.getString(EditPCDialog.ABBREVIATION);
            }
            ListView lv = (ListView)findViewById(R.id.listView);
            ArrayAdapter<Map> adapter = (ArrayAdapter) lv.getAdapter();
            adapter.notifyDataSetChanged();
        }
    }
}
