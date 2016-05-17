package org.daduke.realmar.dhcpv6client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

/**
 * Created by Anastassios Martakos on 8/3/15.
 */
public class UninstallDHCPv6Client  extends AsyncTask<String, String, String> {
    ProgressDialog progDialog;
    Activity activity;
    private boolean result_status;

    public UninstallDHCPv6Client(Activity activity_arg) {
        activity = activity_arg;
        progDialog = new ProgressDialog(activity_arg);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDialog.setMessage("Uninstalling ...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(false);
        progDialog.show();
    }
    @Override
    protected String doInBackground(String... aurl) {
        result_status = uninstall_dhcpv6();
        return null;
    }
    @Override
    protected void onPostExecute(String unused) {
        super.onPostExecute(unused);
        MsgBoxes msg_box = new MsgBoxes();
        if(result_status) {
            SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = shared_preferences.edit();

            editor.putBoolean("is_installed", false);
            editor.putBoolean("is_installed_update", false);
            editor.commit();

            MainActivity.option_menu_main.findItem(R.id.action_unorinstall).setTitle(R.string.action_install);
            MainActivity.option_menu_main.findItem(R.id.action_invoke).setEnabled(false);
            MainActivity.option_menu_main.findItem(R.id.action_unorinstall).setIcon(R.mipmap.ic_install);

            AlertDialog success = (AlertDialog) msg_box.one_button(activity, "Success", activity.getString(R.string.success_uninstall), false);
            success.show();
        }else{
            AlertDialog error = (AlertDialog) msg_box.one_button(activity, "Error", activity.getString(R.string.failure_uninstall) , false);
            error.show();
        }
        progDialog.dismiss();
    }

    public boolean uninstall_dhcpv6() {
        try {
            SUCalls.mount_rw();
            SUCalls.uninstall_dhcpv6_client();
            SUCalls.mount_ro();

            return true;
        }catch (Exception e) {
            return false;
        }
    }
}
