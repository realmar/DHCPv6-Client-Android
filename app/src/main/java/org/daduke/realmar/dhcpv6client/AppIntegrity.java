package org.daduke.realmar.dhcpv6client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Anastassios Martakos on 8/14/15.
 */
public class AppIntegrity {
    public static void check_shared_preferences(Context context) {
        SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = shared_preferences.edit();

        if(!shared_preferences.contains(Constants.PREF_ENABLE)) { editor.putBoolean(Constants.PREF_ENABLE, true);  }
        if(!shared_preferences.contains(Constants.PREF_DEBUG))  { editor.putBoolean(Constants.PREF_DEBUG, false);  }
        if(!shared_preferences.contains(Constants.PREF_WLAN))   { editor.putBoolean(Constants.PREF_WLAN, true);    }
        if(!shared_preferences.contains(Constants.PREF_MOBILE)) { editor.putBoolean(Constants.PREF_MOBILE, false); }
        if(!shared_preferences.contains(Constants.PREF_ALL))    { editor.putBoolean(Constants.PREF_ALL, false);    }
        if(!shared_preferences.contains(Constants.DHCP6C_CONF)) { editor.putString(Constants.DHCP6C_CONF, "null"); }
        if(!shared_preferences.contains(Constants.DHCP6CDNS_CONF)) { editor.putString(Constants.DHCP6CDNS_CONF, "null"); }
        if(!shared_preferences.contains(Constants.SHOW_ARCH_WARNING)) { editor.putBoolean(Constants.SHOW_ARCH_WARNING, true); }

        editor.commit();
    }
}
