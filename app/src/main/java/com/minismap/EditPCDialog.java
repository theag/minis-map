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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.minismap.data.PC;

/**
 * Created by nbp184 on 2016/03/21.
 */
public class EditPCDialog extends DialogFragment {

    public interface OnClickListener {
        public void onPCChange(Bundle data);
    }

    public static final String INDEX = "index";
    public static final String NAME = "name";
    public static final String ABBREVIATION = "abbreviation";
    public static final String TYPE = "type";
    public static final String ERROR = "error";

    private EditPCDialog.OnClickListener listener = null;
    private int index;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof EditPCDialog.OnClickListener) {
            listener = (EditPCDialog.OnClickListener)activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_pc, null);
        Bundle args = getArguments();
        index = args.getInt(INDEX, -1);
        EditText et;
        if(args.containsKey(NAME)) {
            et = (EditText) view.findViewById(R.id.edit_name);
            et.setText(args.getString(NAME));
        }
        if(args.containsKey(ABBREVIATION)) {
            et = (EditText) view.findViewById(R.id.edit_abbreviation);
            et.setText(args.getString(ABBREVIATION));
        }
        if(args.containsKey(TYPE) && args.getInt(TYPE) == PC.npc) {
            RadioButton rb = (RadioButton)view.findViewById(R.id.radio_npc);
            rb.setChecked(true);
        }
        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (EditPCDialog.this.listener != null) {
                            Dialog currDialog = EditPCDialog.this.getDialog();
                            Bundle data = new Bundle();
                            data.putInt(INDEX, EditPCDialog.this.index);
                            RadioGroup rg = (RadioGroup) currDialog.findViewById(R.id.radio_group_type);
                            if (rg.getCheckedRadioButtonId() == R.id.radio_pc) {
                                data.putInt(TYPE, PC.pc);
                            } else if (rg.getCheckedRadioButtonId() == R.id.radio_npc) {
                                data.putInt(TYPE, PC.npc);
                            } else {
                                data.putString(ERROR, "Must select either PC or NPC.");
                            }
                            EditText et = (EditText) currDialog.findViewById(R.id.edit_abbreviation);
                            if (et.getText().toString().isEmpty()) {
                                data.putString(ERROR, "Must enter an abbreviation.");
                            } else {
                                data.putString(ABBREVIATION, et.getText().toString());
                            }
                            et = (EditText) currDialog.findViewById(R.id.edit_name);
                            if (et.getText().toString().isEmpty()) {
                                data.putString(ERROR, "Must enter a name.");
                            } else {
                                data.putString(NAME, et.getText().toString());
                            }
                            EditPCDialog.this.listener.onPCChange(data);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        return dialog;
    }

}
