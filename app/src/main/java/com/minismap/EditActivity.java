package com.minismap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class EditActivity extends AppCompatActivity implements ChangeNameDialog.OnClickListener {

    private Map current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        current = MapArray.getInstance().getMap(this.getIntent().getIntExtra(MapArray.INDEX, -1));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(current.name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch(item.getItemId()) {
            case R.id.action_change_name:
                ChangeNameDialog frag = new ChangeNameDialog();
                Bundle args = new Bundle();
                args.putString(ChangeNameDialog.NAME, current.name);
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), "dialog");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onNameChange(String name) {
        if(!name.isEmpty()) {
            current.name = name;
            getSupportActionBar().setTitle(current.name);
        }
    }
}
