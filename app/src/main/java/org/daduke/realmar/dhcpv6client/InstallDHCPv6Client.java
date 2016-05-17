package org.daduke.realmar.dhcpv6client;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Anastassios Martakos on 7/31/15.
 */
public class InstallDHCPv6Client extends AsyncTask<String, String, String> {
    ProgressDialog progDialog;
    Activity activity;
    private boolean result_status;
    private boolean full_installation;
    private boolean download_files;

    public InstallDHCPv6Client(Activity activity_arg, boolean full_installation, boolean download_files) {
        activity = activity_arg;
        this.full_installation = full_installation;
        progDialog = new ProgressDialog(activity_arg);
        this.download_files = download_files;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDialog.setMessage("Installing ...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(false);
        progDialog.show();
    }
    @Override
    protected String doInBackground(String... aurl) {
        result_status = install_dhcpv6();
        return null;
    }
    @Override
    protected void onPostExecute(String unused) {
        super.onPostExecute(unused);
        MsgBoxes msg_box = new MsgBoxes();
        if(result_status) {
            String full_check_result = DHCPv6Integrity.FullCheck();

            if(full_check_result.equals("ok")) {
                SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                SharedPreferences.Editor editor = shared_preferences.edit();

                editor.putBoolean("is_installed", true);
                editor.putBoolean("is_installed_update", true);
                editor.commit();

                MainActivity.option_menu_main.findItem(R.id.action_unorinstall).setTitle(R.string.action_uninstall);
                MainActivity.option_menu_main.findItem(R.id.action_invoke).setEnabled(true);
                MainActivity.option_menu_main.findItem(R.id.action_unorinstall).setIcon(R.mipmap.ic_uninstall);

                new GenerateClientConfig(activity).execute();

                AlertDialog success;

                if(full_installation) {
                    success = (AlertDialog) msg_box.one_button(activity, "Success", activity.getString(R.string.success_install), false);
                }else{
                    success = (AlertDialog) msg_box.one_button(activity, "Success", activity.getString(R.string.success_update), false);
                }
                success.show();
            }else{
                Log.v("EXCEPTION", full_check_result);
                AlertDialog failed;
                if(full_check_result.equals("md5")) {
                    if (download_files) {
                        failed = (AlertDialog) msg_box.one_button(activity, "Failed", activity.getString(R.string.check_md5), false);
                        failed.show();
                    }else{
                        Misc.question_download_files(activity, activity.getString(R.string.check_md5), full_installation);
                    }
                }else if(full_check_result.equals("exist")) {
                    if (download_files) {
                        failed = (AlertDialog) msg_box.one_button(activity, "Failed", activity.getString(R.string.check_copy), false);
                        failed.show();
                    }else {
                        Misc.question_download_files(activity, activity.getString(R.string.check_copy), full_installation);
                    }
                }else{
                    if (download_files) {
                        failed = (AlertDialog) msg_box.one_button(activity, "Failed", activity.getString(R.string.check_unexpected), false);
                        failed.show();
                    }else {
                        Misc.question_download_files(activity, activity.getString(R.string.check_unexpected), full_installation);
                    }
                }
            }
        }else{
            AlertDialog failed;
            if (download_files) {
                failed = (AlertDialog) msg_box.one_button(activity, "Failed", activity.getString(R.string.check_other), false);
                failed.show();
            }else {
                Misc.question_download_files(activity, activity.getString(R.string.check_other), full_installation);
            }
        }
        progDialog.dismiss();
    }

    public boolean install_dhcpv6() {
        try {
            if(full_installation) {
                DHCPv6Integrity.install_all(activity, download_files);
                return true;
            }else{
                DHCPv6Integrity.install_update(activity, download_files);
                return true;
            }
        }catch (Exception e) {
            Log.v("EXCEPTION", e.toString());
            return false;
        }
    }
}
