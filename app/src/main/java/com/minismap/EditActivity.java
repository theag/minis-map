package com.minismap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.minismap.data.Map;
import com.minismap.data.MapArray;

import java.io.IOException;

public class EditActivity extends AppCompatActivity implements ChangeNameDialog.OnClickListener,
    AdapterView.OnItemSelectedListener, MapView.OnTapListener, AddEnemyDialog.OnClickListener,
    EditEnemyDialog.OnClickListener {

    private static final int[] toolbarIDs = {R.id.toolbar_layout, R.id.toolbar_enemies, R.id.toolbar_fogOfWar};
    private static final int IMAGE_REQUEST = 1;
    private static final String CHANGE_NAME_DIALOG = "change name dialog";
    private static final String ADD_FOG_DIALOG = "add fog dialog";
    private static final String ADD_FOG_RECT_DIALOG = "add fog (rectangle) dialog";
    private static final String CHANGE_FOG_DIALOG = "change fog dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spinner = (Spinner)findViewById(R.id.spinner_toolbar);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.map_toolbars_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        spinner = (Spinner)findViewById(R.id.spinner_fog_mode);
        adapter = ArrayAdapter.createFromResource(this, R.array.map_fogMode_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        MapView mapView = (MapView)findViewById(R.id.mapView);
        mapView.setOnTapListener(this);
        Map map = MapArray.getInstance().getMap(this.getIntent().getIntExtra(MapArray.INDEX, -1));
        mapView.setMap(map);
        getSupportActionBar().setTitle(map.name);
        EditText et = (EditText)findViewById(R.id.edit_x0);
        et.setText("" + map.x0);
        et = (EditText)findViewById(R.id.edit_y0);
        et.setText("" + map.y0);
        et = (EditText)findViewById(R.id.edit_boxSize);
        et.setText(""+map.boxSize);
        et = (EditText)findViewById(R.id.edit_width);
        et.setText(""+map.width);
        et = (EditText)findViewById(R.id.edit_height);
        et.setText(""+map.height);

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
        MapView mapView = (MapView)findViewById(R.id.mapView);
        switch(item.getItemId()) {
            case R.id.action_change_name:
                ChangeNameDialog frag = new ChangeNameDialog();
                Bundle args = new Bundle();
                args.putString(ChangeNameDialog.NAME, mapView.getMapName());
                frag.setArguments(args);
                frag.show(getSupportFragmentManager(), CHANGE_NAME_DIALOG);
                return true;
            case R.id.action_picture:
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, IMAGE_REQUEST);
                return true;
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.delete)
                        .setMessage("Are you sure you wish to delete this map?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteThisMap();
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .setCancelable(true);
                builder.create().show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteThisMap() {
        MapView mapView = (MapView)findViewById(R.id.mapView);
        MapArray.getInstance().deleteMap(getFilesDir(), mapView.getMapIndex());
        setResult(RESULT_OK);
        finish();
    }

    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onNameChange(String tag, String name, Bundle data) {
        MapView mapView = (MapView)findViewById(R.id.mapView);
        switch(tag) {
            case CHANGE_NAME_DIALOG:
                if(!name.isEmpty()) {
                    mapView.setMapName(name);
                    getSupportActionBar().setTitle(name);
                }
                break;
            case ADD_FOG_DIALOG:
                mapView.addFog(name, data.getInt("x"), data.getInt("y"));
                break;
            case ADD_FOG_RECT_DIALOG:
                mapView.addFog(name, data.getInt("x1"), data.getInt("y1"), data.getInt("x2"), data.getInt("y2"));
                break;
            case CHANGE_FOG_DIALOG:
                mapView.setFogName(data.getInt("index"), name);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MapView mapView = (MapView)findViewById(R.id.mapView);
        if(requestCode == IMAGE_REQUEST) {
            if(resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                System.out.println(imageUri);
                try {
                    MapArray.getInstance().unloadOtherBackgounds(null);
                    mapView.setMapBackground(MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri));
                } catch (IOException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Could not find file.")
                            .setTitle("Error")
                            .setPositiveButton("OK", null);
                    builder.create().show();
                }

            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        MapView mapView = (MapView)findViewById(R.id.mapView);
        switch(((View)view.getParent()).getId()) {
            case R.id.spinner_toolbar:
                for(int resID : toolbarIDs) {
                    findViewById(resID).setVisibility(View.GONE);
                }
                findViewById(toolbarIDs[position]).setVisibility(View.VISIBLE);
                mapView.setDrawingMode(position);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.spinner_fog_mode:
                mapView.setFogMode(position);
                break;
            default:
                System.out.println("bad item select: " +view.getClass().getSimpleName() +" " +view.getParent().getClass().getSimpleName());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void update(View view) {
        MapView mapView = (MapView)findViewById(R.id.mapView);
        switch(view.getId()) {
            case R.id.btn_update:
                switch(mapView.getDrawingMode()) {
                    case MapView.MODE_LAYOUT:
                        EditText et = (EditText)findViewById(R.id.edit_x0);
                        mapView.setMapX0(Integer.parseInt(et.getText().toString()));
                        et = (EditText)findViewById(R.id.edit_y0);
                        mapView.setMapY0(Integer.parseInt(et.getText().toString()));
                        et = (EditText)findViewById(R.id.edit_boxSize);
                        mapView.setMapBoxSize(Integer.parseInt(et.getText().toString()));
                        et = (EditText)findViewById(R.id.edit_width);
                        mapView.setMapWidth(Integer.parseInt(et.getText().toString()));
                        et = (EditText)findViewById(R.id.edit_height);
                        mapView.setMapHeight(Integer.parseInt(et.getText().toString()));
                        break;
                }
                break;
            case R.id.chk_show_grid:
            case R.id.chk_show_gridf:
                mapView.setShowGrid(((CheckBox)view).isChecked());
                break;
            case R.id.btn_deselect_fog:
                mapView.deselectFog();
                break;
            case R.id.btn_delete_enemy:
                mapView.deleteSelectedEnemy();
                break;
            case R.id.btn_delete_fog:
                mapView.deleteSelectedFog();
                break;
        }
    }

    @Override
    public void onAddEnemy(int x, int y) {
        MapView mapView = (MapView)findViewById(R.id.mapView);
        AddEnemyDialog frag = new AddEnemyDialog();
        Bundle args = new Bundle();
        args.putInt(AddEnemyDialog.MAP_INDEX, mapView.getMapIndex());
        args.putInt(AddEnemyDialog.X, x);
        args.putInt(AddEnemyDialog.Y, y);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onChangeEnemy(int enemyIndex) {
        MapView mapView = (MapView)findViewById(R.id.mapView);
        EditEnemyDialog frag = new EditEnemyDialog();
        Bundle args = new Bundle();
        args.putInt(EditEnemyDialog.MAP_INDEX, mapView.getMapIndex());
        args.putInt(EditEnemyDialog.ENEMY_INDEX, enemyIndex);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onMoveTapError(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle("Error")
                .setPositiveButton("OK", null)
                .setCancelable(true);
        builder.create().show();
    }

    @Override
    public void onAddFog(int x, int y) {
        ChangeNameDialog frag = new ChangeNameDialog();
        Bundle args = new Bundle();
        args.putString(ChangeNameDialog.TITLE, "Add Fog");
        args.putString(ChangeNameDialog.NAME, "");
        args.putInt("x", x);
        args.putInt("y", y);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), ADD_FOG_DIALOG);
    }

    @Override
    public void onAddFog(int startx, int starty, int endx, int endy) {
        ChangeNameDialog frag = new ChangeNameDialog();
        Bundle args = new Bundle();
        args.putString(ChangeNameDialog.TITLE, "Add Fog");
        args.putString(ChangeNameDialog.NAME, "");
        args.putInt("x1", startx);
        args.putInt("y1", starty);
        args.putInt("x2", endx);
        args.putInt("y2", endy);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), ADD_FOG_RECT_DIALOG);
    }

    @Override
    public void onChangeFog(int fogIndex) {
        ChangeNameDialog frag = new ChangeNameDialog();
        Bundle args = new Bundle();
        MapView mapView = (MapView)findViewById(R.id.mapView);
        args.putString(ChangeNameDialog.NAME, mapView.getFogName(fogIndex));
        args.putInt("index", fogIndex);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), CHANGE_FOG_DIALOG);
    }

    @Override
    public void onEnemyAdd(String name, String abbreviation, int x, int y) {
        MapView mapView = (MapView)findViewById(R.id.mapView);
        mapView.addEnemy(name, abbreviation, x, y);
    }

    @Override
    public void onEnemyEdit(int index, String name, String abbreviation) {
        MapView mapView = (MapView)findViewById(R.id.mapView);
        mapView.editEnemy(index, name, abbreviation);
    }
}
