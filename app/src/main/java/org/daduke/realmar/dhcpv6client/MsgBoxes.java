package org.daduke.realmar.dhcpv6client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Anastassios Martakos on 7/29/15.
 */
public class MsgBoxes {
    public Dialog one_button(final Activity activity, String title, String text, final boolean exit_app) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(exit_app) activity.finish();
                    }
        });
        return alertDialog;
    }
}
