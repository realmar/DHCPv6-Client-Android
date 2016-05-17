package org.daduke.realmar.dhcpv6client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.KeyEvent;

import com.stericson.RootTools.RootTools;

import java.util.ArrayList;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Anastassios Martakos on 11.08.2015.
 */
public class DoStartup extends AsyncTask<String, String, String> {
    ProgressDialog progDialog;
    Activity activity;
    MainActivity main_activity;
    private String result_status;

    public DoStartup(Activity activity_arg, MainActivity main_activity_arg) {
        activity = activity_arg;
        main_activity = main_activity_arg;
        progDialog = new ProgressDialog(activity_arg);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDialog.setMessage("Requesting Root permissions ...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(false);
        progDialog.show();
    }

    @Override
    protected String doInBackground(String... aurl) {
        result_status = check_permissions();
        return null;
    }

    @Override
    protected void onPostExecute(String unused) {
        super.onPostExecute(unused);

        Dialog.OnKeyListener diaglog_key_listener = new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int key_code, KeyEvent event) {
                if(key_code == KeyEvent.KEYCODE_BACK) {
                    activity.finish();
                }

                return true;
            }
        };

        MsgBoxes error_message = new MsgBoxes();
        AlertDialog fail_root_dialog_obj = (AlertDialog) error_message.one_button(activity, activity.getString(R.string.no_root_title), activity.getString(R.string.no_root), true);
        AlertDialog fail_busybox_dialog_obj = (AlertDialog) error_message.one_button(activity, activity.getString(R.string.no_busybox_title), activity.getString(R.string.no_busybox), true);

        fail_root_dialog_obj.setOnKeyListener(diaglog_key_listener);
        fail_busybox_dialog_obj.setOnKeyListener(diaglog_key_listener);

        progDialog.dismiss();

        if(result_status.equals("run")) {
            ArrayList<String> missing_commands = DHCPv6Integrity.check_commands();

            if(missing_commands.size() > 0) {
                String missing_commands_string = new String();

                for(String commands : missing_commands) {
                    missing_commands_string = missing_commands_string + commands + " ";
                }

                AlertDialog fail_commands_dialog_obj = (AlertDialog) error_message.one_button(activity, activity.getString(R.string.missing_commands_title), activity.getString(R.string.missing_commands) + missing_commands_string, true);

                fail_commands_dialog_obj.setOnKeyListener(diaglog_key_listener);

                fail_commands_dialog_obj.show();
            }else{
                main_activity.post_startup();
            }
        }else if(result_status.equals("busybox")) {
            fail_busybox_dialog_obj.show();
        }else if(result_status.equals("root")) {
            fail_root_dialog_obj.show();
        }

    }

    private String check_permissions() {
        if (Shell.SU.available()) {
            if (RootTools.isBusyboxAvailable()) {
                return "run";
            }else{
                return "busybox";
            }
        }else{
            return "root";
        }
    }
}
