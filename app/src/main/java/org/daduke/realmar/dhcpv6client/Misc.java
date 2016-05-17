package org.daduke.realmar.dhcpv6client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by Anastassios Martakos on 7/31/15.
 */
public class Misc extends ActionBarActivity {


    public static ArrayList[] get_ips() {
        ArrayList<String> ipv4s = new ArrayList<String>();
        ArrayList<String> ipv6s = new ArrayList<String>();

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        ipv4s.add(inetAddress.getHostAddress());
                    }else if (!inetAddress.isLoopbackAddress()) {
                        ipv6s.add(inetAddress.getHostAddress());
                    }
                }
            }
        }
        catch(Exception e) {
        }

        return new ArrayList[] {ipv4s, ipv6s};
    }

    public static int get_random_positive_int() {
        Random random = new Random();
        return Math.abs(random.nextInt());
    }

    public static int get_random_small() {
        Random random = new Random();
        return Math.abs(random.nextInt((9 - 1) + 1) + 1);
    }

    public static ArrayList<String> get_all_interfaces() {
        ArrayList<String> interfaces = new ArrayList<String>();

        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                interfaces.add(netint.getName());
            }
        }
        catch(Exception e) {
        }

        return interfaces;
    }

    public static void send_ui_request_refresh_ips() {
        if(MainActivity.main_context != null) {
            Intent intent = new Intent("org.daduke.realmar.dhcpv6client");
            intent.putExtra("refresh_ips", "refresh_ips");
            LocalBroadcastManager.getInstance(MainActivity.main_context).sendBroadcast(intent);
        }
    }

    public static void question_installation_update(final Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(activity.getString(R.string.update_title));
        alertDialog.setMessage(activity.getString(R.string.update_text));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(SystemIntegrity.update_check()) {
                            new InstallDHCPv6Client(activity, false, false).execute();
                        }else{
                            MsgBoxes msg_box = new MsgBoxes();
                            AlertDialog no_disk_space = (AlertDialog) msg_box.one_button(activity, "Failure", activity.getString(R.string.not_enough_space), false);
                            no_disk_space.show();
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void question_installation(final Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Install");
        alertDialog.setMessage(activity.getString(R.string.install_text));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(SystemIntegrity.full_check()) {
                            new InstallDHCPv6Client(activity, true, false).execute();
                        }else{
                            MsgBoxes msg_box = new MsgBoxes();
                            AlertDialog no_disk_space = (AlertDialog) msg_box.one_button(activity, "Failure", activity.getString(R.string.not_enough_space), false);
                            no_disk_space.show();
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void question_uninstallation(final Activity activity) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Uninstall");
        alertDialog.setMessage(activity.getString(R.string.uninstall_text));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new UninstallDHCPv6Client(activity).execute();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void question_download_files(final Activity activity, String text, final boolean full_installation) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Retry?");
        alertDialog.setMessage(text + activity.getString(R.string.download_append_text));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(is_internet_availble(activity)) {
                            new InstallDHCPv6Client(activity, full_installation, true).execute();
                        }else{
                            MsgBoxes msg_box = new MsgBoxes();
                            AlertDialog no_internet = (AlertDialog) msg_box.one_button(activity, "Failure", activity.getString(R.string.failure_no_internet_connection), false);
                            no_internet.show();
                        }
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static void unsupported_arch(final Activity activity, String text) {
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle("Unsupported Architecture");
        alertDialog.setMessage(text);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Do not show again",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(activity);
                        SharedPreferences.Editor editor = shared_preferences.edit();

                        editor.putBoolean(Constants.SHOW_ARCH_WARNING, false);

                        editor.commit();

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public static ArrayList<String> get_all_configured_interfaces(Context context) {
        ArrayList<String> configured_interfaces = new ArrayList<String>();
        SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(shared_preferences.getBoolean("pref_all", false)) {
            for (String inter_face : Misc.get_all_interfaces()) {
                configured_interfaces.add(inter_face);
            }
        }else {
            if (shared_preferences.getBoolean(Constants.PREF_WLAN, false)) {
                configured_interfaces.add("wlan0");
            }

            if (shared_preferences.getBoolean(Constants.PREF_MOBILE, false)) {
                configured_interfaces.add("rmnet0");
            }

            Set<String> additional_interfaces = shared_preferences.getStringSet(Constants.ADDITIONAL_INTERFACES, new HashSet<String>());

            for (String inter_face : additional_interfaces) {
                configured_interfaces.add(inter_face);
            }
        }

        return configured_interfaces;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean is_internet_availble(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
