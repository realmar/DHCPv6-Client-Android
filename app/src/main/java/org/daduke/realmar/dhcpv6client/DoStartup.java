package org.daduke.realmar.dhcpv6client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.stericson.RootTools.RootTools;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Anastassios Martakos on 11.08.2015.
 */
public class DoStartup extends AsyncTask<String, String, String> {
    ProgressDialog progDialog;
    Context context;
    MainActivity main_activity;
    private String result_status;

    public DoStartup(Context context_arg, MainActivity main_activity_arg) {
        context = context_arg;
        main_activity = main_activity_arg;
        progDialog = new ProgressDialog(context_arg);
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

        MsgBoxes error_message = new MsgBoxes();
        AlertDialog fail_root_dialog_obj = (AlertDialog) error_message.one_button(context, context.getString(R.string.no_root_title), context.getString(R.string.no_root), true);
        AlertDialog fail_busybox_dialog_obj = (AlertDialog) error_message.one_button(context, context.getString(R.string.no_busybox_title), context.getString(R.string.no_busybox), true);

        progDialog.dismiss();

        if(result_status.equals("run")) {
            main_activity.post_startup();
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
