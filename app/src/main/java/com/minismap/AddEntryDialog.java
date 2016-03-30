package com.minismap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by nbp184 on 2016/03/29.
 */
public class AddEntryDialog extends DialogFragment {

    public static final String START_X = "start x";
    public static final String START_Y = "start y";
    public static final String END_X = "end x";
    public static final String END_Y = "end y";

    public interface OnClickListener {
        public void onEntryAdd(String abbreviation, String lockMessage, int startx, int starty, int endx, int endy);
    }

    private AddEntryDialog.OnClickListener listener = null;
    private int startx;
    private int starty;
    private int endx;
    private int endy;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof AddEntryDialog.OnClickListener) {
            listener = (AddEntryDialog.OnClickListener)activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_fog_entry, null);
        Bundle args = getArguments();
        startx = args.getInt(START_X);
        starty = args.getInt(START_Y);
        endx = args.getInt(END_X);
        endy = args.getInt(END_Y);
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

    private void doPositiveClick() {
        if (listener != null) {
            EditText et = (EditText)getDialog().findViewById(R.id.edit_abbreviation);
            String abbreviation = et.getText().toString();
            et = (EditText)getDialog().findViewById(R.id.edit_lock_message);
            String lockMessage = et.getText().toString();
            listener.onEntryAdd(abbreviation, lockMessage, startx, starty, endx, endy);
        }
    }

}
