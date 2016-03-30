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
import com.minismap.data.Entry;
import com.minismap.data.FogOfWar;
import com.minismap.data.Map;
import com.minismap.data.MapArray;

import java.util.ArrayList;

/**
 * Created by nbp184 on 2016/03/29.
 */
public class EditEntryDialog extends DialogFragment {

    public static final String MAP_INDEX = "map index";
    public static final String FOG_INDEX = "fog index";
    public static final String ENTRY_INDEX = "entry index";

    public interface OnClickListener {
        public void onEntryEdit(int index, String abbreviation, String lockMessage);
    }

    private EditEntryDialog.OnClickListener listener = null;
    private int index;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof EditEntryDialog.OnClickListener) {
            listener = (EditEntryDialog.OnClickListener)activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        DialogLayout view = (DialogLayout)inflater.inflate(R.layout.dialog_fog_entry, null);
        view.setTitle(R.string.edit_fog_entry);

        Bundle args = getArguments();
        int mapIndex = args.getInt(MAP_INDEX);
        Map map = MapArray.getInstance().getMap(mapIndex);
        int fogIndex = args.getInt(FOG_INDEX);
        FogOfWar fog = map.getFog(fogIndex);
        index = args.getInt(ENTRY_INDEX);
        Entry entry = fog.getEntry(index);

        EditText et = (EditText)view.findViewById(R.id.edit_abbreviation);
        et.setText(entry.abbreviation);

        et = (EditText)view.findViewById(R.id.edit_lock_message);
        et.setText(entry.lockMessage);

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

    private void doPositiveClick() {
        if (listener != null) {
            EditText et = (EditText)getDialog().findViewById(R.id.edit_abbreviation);
            String abbreviation = et.getText().toString();
            et = (EditText)getDialog().findViewById(R.id.edit_lock_message);
            String lockMessage = et.getText().toString();
            listener.onEntryEdit(index, abbreviation, lockMessage);
        }
    }

}
