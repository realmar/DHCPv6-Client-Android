package org.daduke.realmar.dhcpv6client;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Anastassios Martakos on 7/31/15.
 */

public class UIMenu {

    public UIMenu(Context context_arg) {
    }

    public static void open_status(boolean is_running, Context context) {
        String running = is_running ? "running" : "not running";

        MsgBoxes status = new MsgBoxes();
        AlertDialog status_dialog = (AlertDialog) status.one_button((MainActivity) context, "Status", "Client Service: " + running, false);
        status_dialog.show();
    }

    public static void unorinstall(Context context) {
        SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (shared_preferences.getBoolean("is_installed", false) && shared_preferences.getBoolean("is_installed_update", false)) {
            Misc.question_uninstallation(context);
        }else if(shared_preferences.getBoolean("is_installed", false) && !shared_preferences.getBoolean("is_installed_update", false)) {
            Misc.question_installation_update(context);
        }else{
            Misc.question_installation(context);
        }
    }
}
