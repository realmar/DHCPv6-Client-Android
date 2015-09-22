package org.daduke.realmar.dhcpv6client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by Anastassios Martakos on 8/24/15.
 */
public class SettingsAdvancedFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_advanced);
        add_click_listener("pref_mobile");
        add_click_listener("pref_all");
        add_click_listener("pref_button_add_interfaces");

        check_preferences(getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        check_preferences(sharedPreferences);
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

    private void add_click_listener(final String element_arg) {
        Preference element = (Preference) findPreference(element_arg);

        element.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                switch (element_arg) {
                    case "pref_mobile":
                        new GenerateClientConfig(getActivity()).execute();
                        break;
                    case "pref_button_add_interfaces":
                        ((MainActivity) getActivity()).go_settings_advanced_add();
                        break;
                    case "pref_all":
                        new GenerateClientConfig(getActivity()).execute();
                        break;
                }

                return true;
            }
        });
    }

    private void check_preferences(SharedPreferences sharedPreferences) {
        boolean is_installed = sharedPreferences.getBoolean("is_installed", false);
        boolean is_installed_udpate = sharedPreferences.getBoolean("is_installed_update", false);

        if(!sharedPreferences.getBoolean("pref_enable", false) || !is_installed || !is_installed_udpate) {
            findPreference("pref_mobile").setEnabled(false);
            findPreference("pref_button_add_interfaces").setEnabled(false);
            findPreference("pref_all").setEnabled(false);
        }else{
            findPreference("pref_mobile").setEnabled(true);
            findPreference("pref_button_add_interfaces").setEnabled(true);
            findPreference("pref_all").setEnabled(true);
        }

        if(sharedPreferences.getBoolean("pref_all", false)) {
            findPreference("pref_mobile").setEnabled(false);
            findPreference("pref_button_add_interfaces").setEnabled(false);
        }else{
            if(sharedPreferences.getBoolean("pref_enable", false) && is_installed && is_installed_udpate) {
                findPreference("pref_mobile").setEnabled(true);
                findPreference("pref_button_add_interfaces").setEnabled(true);
            }
        }
    }
}