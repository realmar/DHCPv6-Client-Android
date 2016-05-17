package org.daduke.realmar.dhcpv6client;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Anastassios Martakos on 8/3/15.
 */
public class GetIPv6Address extends AsyncTask<String, String, String> {
    private boolean result_status;
    private String inter_face;
    public GetIPv6Address(String inter_face_arg) {
        inter_face = inter_face_arg;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
    @Override
    protected String doInBackground(String... aurl) {

        SUCalls.remove_file("/data/misc/dhcp/dhcp6c_duid");

        if(!SUCalls.check_process("dhcp6c")) {
            SUCalls.start_dhpv6c_process(inter_face);
            return null;
        }else {
            SUCalls.send_signal_to_client_process(inter_face);
            return null;
        }
    }
    @Override
    protected void onPostExecute(String unused) {
        super.onPostExecute(unused);
        Misc.send_ui_request_refresh_ips();
    }

    public static boolean PreGetIPv6Address(Context context, boolean do_all) {
        SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Misc.send_ui_request_refresh_ips();

        if(!shared_preferences.getBoolean(Constants.PREF_ENABLE, false) || !shared_preferences.getBoolean(Constants.IS_INSTALLED, false)) { return do_all; }

        SharedPreferences.Editor editor = shared_preferences.edit();

        if(!shared_preferences.getBoolean(Constants.IS_INSTALLED_UPDATE, false)) {
            if(DHCPv6Integrity.CheckUpdate().equals("ok")) {
                editor.putBoolean(Constants.IS_INSTALLED_UPDATE, true);
                editor.commit();
            }else{
                editor.putBoolean(Constants.IS_INSTALLED_UPDATE, false);
                editor.commit();

                show_update_notification(context);
            }
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();

        String ssid = info.getBSSID();

        if (activeNetwork != null) {
            if(!shared_preferences.contains(Constants.LAST_STATE)) {
                editor.putInt(Constants.LAST_STATE, -200);
                editor.commit();
            }

            if(!shared_preferences.contains(Constants.LAST_SSID)) {
                editor.putString(Constants.LAST_SSID, "randomlastssiddummy");
                editor.commit();
            }

            int last_state = shared_preferences.getInt(Constants.LAST_STATE, -200);
            String last_ssid = shared_preferences.getString(Constants.LAST_SSID, "randomlastssiddummy");

            switch(activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    if(last_state == ConnectivityManager.TYPE_WIFI && last_ssid.equals(ssid)) return do_all;
                    editor.putString(Constants.LAST_SSID, ssid);
                    editor.putInt(Constants.LAST_STATE, ConnectivityManager.TYPE_WIFI);
                    editor.commit();
                    if(!shared_preferences.getBoolean(Constants.PREF_WLAN, false) && !shared_preferences.getBoolean("pref_all", false)) return do_all;

                    do_all = prepare_dhcpv6(context, "wlan0", do_all);
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    if(last_state == ConnectivityManager.TYPE_MOBILE) return do_all;
                    editor.putInt(Constants.LAST_STATE, ConnectivityManager.TYPE_MOBILE);
                    editor.commit();
                    // if(!shared_preferences.getBoolean(Constants.PREF_MOBILE, false) && !shared_preferences.getBoolean("pref_all", false)) return do_all;

                    // do_all = prepare_dhcpv6(context, "rmnet0", do_all);
                    break;
                default:
                    editor.putInt(Constants.LAST_STATE, -200);
                    editor.putString(Constants.LAST_SSID, "randomlastssiddummy");
                    editor.commit();
                    if(shared_preferences.getBoolean(Constants.PREF_DEBUG, false)) {
                        Toast.makeText(context, "NOW DISCONNECTED", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

            if(do_all) {
                do_all = false;
            }else {
                for (String inter_face : get_custom_interfaces(context)) {
                    do_dhcpv6_on_custom_interface(context, inter_face);
                }
            }

        }else{
            editor.putInt(Constants.LAST_STATE, -200);
            editor.putString(Constants.LAST_SSID, "randomlastssiddummy");
            editor.commit();
            if(shared_preferences.getBoolean(Constants.PREF_DEBUG, false)) {
                Toast.makeText(context, "NOW DISCONNECTED", Toast.LENGTH_SHORT).show();
            }
        }

        return do_all;
    }

    private static Set<String> get_custom_interfaces(Context context) {
        Set<String> custom_interfaces = new HashSet<String>(PreferenceManager.getDefaultSharedPreferences(context).getStringSet(Constants.ADDITIONAL_INTERFACES, new HashSet<String>()));
        return custom_interfaces;
    }

    private static void do_dhcpv6_on_custom_interface(Context context, String inter_face) {
        SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(shared_preferences.getBoolean(Constants.PREF_DEBUG, false)) {
            Toast.makeText(context, "DOING OPERATIONS ON: " + inter_face, Toast.LENGTH_SHORT).show();
        }
        new GetIPv6Address(inter_face).execute();
    }

    private static boolean prepare_dhcpv6(Context context, String inter_face, boolean do_all) {
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(Constants.PREF_ALL, false)) {
            for(String in : Misc.get_all_interfaces()) {
                do_dhcpv6_on_custom_interface(context, in);
            }
            do_all = true;
        }else{
            do_dhcpv6_on_custom_interface(context, inter_face);
        }

        return do_all;
    }

    private static void show_update_notification(Context context) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.app_icon)
                        .setContentTitle(context.getString(R.string.update_notification_title))
                        .setContentText(context.getString(R.string.update_notification));

        Intent resultIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int mId = 0;

        mNotificationManager.notify(mId, mBuilder.build());
    }
}
