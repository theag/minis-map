package com.minismap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by nbp184 on 2016/03/29.
 */
public class MessageDialog extends DialogFragment {

    public static final String MESSAGE = "message";
    public static final String TITLE = "title";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(args.getString(MESSAGE))
                .setTitle(args.getString(TITLE))
                .setPositiveButton(R.string.ok, null)
                .setCancelable(true);
        return builder.create();
    }

}
