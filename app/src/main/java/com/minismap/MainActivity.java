package com.minismap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private static final int MAP_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lv = (ListView)findViewById(R.id.listView);
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
                intent = new Intent(this, EditActivity.class);
                intent.putExtra(MapArray.INDEX, MapArray.getInstance().addMap("Untitled"));
                startActivityForResult(intent, MAP_REQUEST);
            case  R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
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
        
    }

}
