package org.daduke.realmar.dhcpv6client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Anastassios Martakos on 8/6/15.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onStart() {
        super.onStart();
        check_preferences(getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        add_click_listener("pref_wlan");
        add_click_listener("pref_button_advanced");

        check_preferences(getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        check_preferences(getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        check_preferences(sharedPreferences);
    }

    private void check_preferences(SharedPreferences sharedPreferences) {
        boolean is_installed = sharedPreferences.getBoolean("is_installed", false);
        boolean is_installed_udpate = sharedPreferences.getBoolean("is_installed_update", false);

        if(!sharedPreferences.getBoolean("pref_enable", false) || !is_installed || !is_installed_udpate) {
            findPreference("pref_wlan").setEnabled(false);
            findPreference("pref_button_advanced").setEnabled(false);

            findPreference("pref_enable").setTitle(R.string.pref_disabled);
            findPreference("pref_enable").setSummary(R.string.pref_disabled_summ);
        }else{
            findPreference("pref_wlan").setEnabled(true);
            findPreference("pref_button_advanced").setEnabled(true);

            findPreference("pref_enable").setTitle(R.string.pref_enabled);
            findPreference("pref_enable").setSummary(R.string.pref_enabled_summ);
        }

        if(sharedPreferences.getBoolean("pref_all", false)) {
            findPreference("pref_wlan").setEnabled(false);
        }else{
            if(sharedPreferences.getBoolean("pref_enable", false) && is_installed && is_installed_udpate) {
                findPreference("pref_wlan").setEnabled(true);
            }
        }

        if(!is_installed || !is_installed_udpate) {
            findPreference("pref_enable").setEnabled(false);
        }else{
            findPreference("pref_enable").setEnabled(true);
        }
    }

    private void add_click_listener(final String element_arg) {
        Preference element = (Preference) findPreference(element_arg);

        element.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                switch (element_arg) {
                    case "pref_wlan":
                        new GenerateClientConfig(getActivity()).execute();
                        break;
                    case "pref_button_advanced":
                        ((MainActivity) getActivity()).go_settings_advanced();
                        break;
                }

                return true;
            }
        });
    }

    public static ArrayList<String> filter_all_interfaces(ArrayList<String> unfiltered_interfaces, Set<String> configured_interfaces) {
        ArrayList<String> filtered_interfaces = new ArrayList<String>();

        for (String unfiltered_interface : unfiltered_interfaces) {
            boolean exists = false;

            for(String configured_interface : configured_interfaces) {
                if(configured_interface.equals(unfiltered_interface)) exists = true;
            }

            if(unfiltered_interface.equals("wlan0") || unfiltered_interface.equals("rmnet0")) exists = true;

            if(!exists) filtered_interfaces.add(unfiltered_interface);
        }

        return filtered_interfaces;
    }
}
