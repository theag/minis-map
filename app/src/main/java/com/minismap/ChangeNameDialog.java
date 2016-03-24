package com.minismap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * Created by nbp184 on 2016/03/16.
 */
public class ChangeNameDialog extends DialogFragment {

    public interface OnClickListener {
        public void onNameChange(String tag, String name, Bundle data);
    }

    public static final String NAME = "name";
    public static final String TITLE = "title";

    private ChangeNameDialog.OnClickListener listener = null;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof ChangeNameDialog.OnClickListener) {
            listener = (ChangeNameDialog.OnClickListener)activity;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        DialogLayout view = (DialogLayout)inflater.inflate(R.layout.dialog_change_name, null);
        if(getArguments().containsKey(TITLE)) {
            view.setTitle(getArguments().getString(TITLE));
        }
        EditText et = (EditText)view.findViewById(R.id.edit_name);
        et.setText(getArguments().getString(NAME));
        builder.setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doPositiveClick();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    private void doPositiveClick() {
        EditText et = (EditText)getDialog().findViewById(R.id.edit_name);
        listener.onNameChange(getTag(), et.getText().toString(), getArguments());
        listener.onNameChange(getTag(), et.getText().toString(), getArguments());
    }

}
