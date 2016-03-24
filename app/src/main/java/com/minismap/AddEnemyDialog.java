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

import com.minismap.data.MapArray;

import java.util.ArrayList;

/**
 * Created by nbp184 on 2016/03/22.
 */
public class AddEnemyDialog extends DialogFragment {

    public static final String MAP_INDEX = "map index";
    public static final String X = "x";
    public static final String Y = "y";

    public interface OnClickListener {
        public void onEnemyAdd(String name, String abbreviation, int x, int y);
    }

    private AddEnemyDialog.OnClickListener listener = null;
    private int x;
    private int y;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof AddEnemyDialog.OnClickListener) {
            listener = (AddEnemyDialog.OnClickListener)activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_enemy, null);
        Bundle args = getArguments();
        int mapIndex = args.getInt(MAP_INDEX);
        x = args.getInt(X);
        y = args.getInt(Y);
        ArrayList<String> names = MapArray.getInstance().getMap(mapIndex).getUniqueEnemyNames();
        Spinner spinner = (Spinner)view.findViewById(R.id.spinner_names);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, names);
        if(names.isEmpty()) {
            RadioButton rb = (RadioButton)view.findViewById(R.id.radio_edit);
            rb.setChecked(true);
            spinner.setEnabled(false);
        } else {
            RadioButton rb = (RadioButton)view.findViewById(R.id.radio_dropdown);
            rb.setChecked(true);
            view.findViewById(R.id.edit_name).setEnabled(false);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        builder.setView(view)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
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
            listener.onEnemyAdd(name, abbreviation, x, y);
        }
    }

}
