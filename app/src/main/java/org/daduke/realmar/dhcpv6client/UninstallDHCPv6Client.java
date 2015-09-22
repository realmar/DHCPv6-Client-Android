package org.daduke.realmar.dhcpv6client;

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
    Context context;
    private boolean result_status;

    public UninstallDHCPv6Client(Context context_arg) {
        context = context_arg;
        progDialog = new ProgressDialog(context_arg);
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
            SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = shared_preferences.edit();

            editor.putBoolean("is_installed", false);
            editor.putBoolean("is_installed_update", false);
            editor.commit();

            MainActivity.option_menu_main.findItem(R.id.action_unorinstall).setTitle(R.string.action_install);
            MainActivity.option_menu_main.findItem(R.id.action_invoke).setEnabled(false);
            MainActivity.option_menu_main.findItem(R.id.action_unorinstall).setIcon(R.mipmap.ic_install);

            AlertDialog success = (AlertDialog) msg_box.one_button(context, "Success", context.getString(R.string.success_uninstall), false);
            success.show();
        }else{
            AlertDialog error = (AlertDialog) msg_box.one_button(context, "Error", context.getString(R.string.failure_uninstall) , false);
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
