package com.minismap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.minismap.data.Enemy;
import com.minismap.data.Map;
import com.minismap.data.MapArray;

import java.util.ArrayList;

/**
 * Created by nbp184 on 2016/03/24.
 */
public class EditEnemyDialog extends DialogFragment {

    public static final String MAP_INDEX = "map index";
    public static final String ENEMY_INDEX = "enemy index";

    public interface OnClickListener {
        public void onEnemyEdit(int index, String name, String abbreviation);
    }

    private EditEnemyDialog.OnClickListener listener = null;
    private int index;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof EditEnemyDialog.OnClickListener) {
            listener = (EditEnemyDialog.OnClickListener)activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        DialogLayout view = (DialogLayout)inflater.inflate(R.layout.dialog_enemy, null);
        view.setTitle(R.string.edit_enemy);

        Bundle args = getArguments();
        int mapIndex = args.getInt(MAP_INDEX);
        Map map = MapArray.getInstance().getMap(mapIndex);
        index = args.getInt(ENEMY_INDEX);
        Enemy enemy = map.enemies.get(index);

        ArrayList<String> names = map.getUniqueEnemyNames();
        Spinner spinner = (Spinner)view.findViewById(R.id.spinner_names);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        RadioButton rb = (RadioButton)view.findViewById(R.id.radio_dropdown);
        rb.setChecked(true);
        view.findViewById(R.id.edit_name).setEnabled(false);
        spinner.setSelection(names.indexOf(enemy.name));

        EditText et = (EditText)view.findViewById(R.id.edit_abbreviation);
        et.setText(enemy.abbreviation);

        builder.setView(view)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doPositiveClick();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        return dialog;
    }

    public void radioChange(View view) {
        getDialog().findViewById(R.id.spinner_names).setEnabled(view.getId() == R.id.radio_dropdown);
        getDialog().findViewById(R.id.edit_name).setEnabled(view.getId() == R.id.radio_edit);
    }

    private void doPositiveClick() {
        if (listener != null) {
            String name;
            RadioButton rb = (RadioButton)getDialog().findViewById(R.id.radio_dropdown);
            if(rb.isChecked()) {
                Spinner spinner = (Spinner)getDialog().findViewById(R.id.spinner_names);
                name = (String)spinner.getSelectedItem();
            } else {
                EditText et = (EditText)getDialog().findViewById(R.id.edit_name);
                name = et.getText().toString();
            }
            EditText et = (EditText)getDialog().findViewById(R.id.edit_abbreviation);
            String abbreviation = et.getText().toString();
            listener.onEnemyEdit(index, name, abbreviation);
        }
    }

}
