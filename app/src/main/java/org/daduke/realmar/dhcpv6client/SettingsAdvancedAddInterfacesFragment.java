package org.daduke.realmar.dhcpv6client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Anastassios Martakos on 8/24/15.
 */
public class SettingsAdvancedAddInterfacesFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Preference.OnPreferenceChangeListener preference_change_listener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String selected_item = value.toString();

            if (preference.getKey().equals(getActivity().getString(R.string.pref_button_add_interface_key))) {
                if (selected_item.equals("")) return false;

                SharedPreferences shared_preference = PreferenceManager.getDefaultSharedPreferences(getActivity());

                Set<String> additional_interfaces = new HashSet<String>();

                if (shared_preference.contains(Constants.ADDITIONAL_INTERFACES)) {
                    additional_interfaces = new HashSet<String>(shared_preference.getStringSet(Constants.ADDITIONAL_INTERFACES, new HashSet<String>()));
                }

                assert additional_interfaces != null;
                additional_interfaces.add(selected_item);

                SharedPreferences.Editor editor = shared_preference.edit();

                editor.putStringSet(Constants.ADDITIONAL_INTERFACES, additional_interfaces);
                editor.commit();

                update_add_interfaces(shared_preference);

                new GenerateClientConfig(getActivity()).execute();
            }

            return false;
        }
    };

    private Preference.OnPreferenceClickListener preference_click_listener = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            if(preference.getKey().equals(getActivity().getString(R.string.cust_interface_button))) {
                String interface_name = preference.getTitle().toString();
                SharedPreferences shared_preference = PreferenceManager.getDefaultSharedPreferences(getActivity());

                Set<String> all_custom_interfaces = new HashSet<String>(shared_preference.getStringSet(Constants.ADDITIONAL_INTERFACES, new HashSet<String>()));
                all_custom_interfaces.remove(interface_name);

                SharedPreferences.Editor editor = shared_preference.edit();

                editor.putStringSet(Constants.ADDITIONAL_INTERFACES, all_custom_interfaces);
                editor.commit();

                update_add_interfaces(shared_preference);

                new GenerateClientConfig(getActivity()).execute();
            }
            return false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences_advanced_add_interfaces);

        update_add_interfaces(getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        update_add_interfaces(getPreferenceManager().getSharedPreferences());
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private void update_add_interfaces(SharedPreferences shared_preferences) {
        Set<String> additional_interfaces = shared_preferences.getStringSet(Constants.ADDITIONAL_INTERFACES, new HashSet<String>());

        PreferenceScreen preference_screen = (PreferenceScreen) findPreference("pref_add_interfaces");
        preference_screen.removeAll();

        if(additional_interfaces != null) {
            String cust_interface_button_key = getActivity().getString(R.string.cust_interface_button);

            for (String inter_face : additional_interfaces) {
                Preference new_interface = new Preference(getActivity());

                new_interface.setTitle(inter_face);
                new_interface.setSummary(R.string.cust_interface);
                new_interface.setKey(cust_interface_button_key);

                new_interface.setOnPreferenceClickListener(preference_click_listener);

                preference_screen.addPreference(new_interface);
            }
        }

        ArrayList<String> all_interfaces = SettingsFragment.filter_all_interfaces(Misc.get_all_interfaces(), additional_interfaces);

        ListPreference add_interface_button = new ListPreference(getActivity());

        add_interface_button.setTitle(R.string.pref_button_add_interface);
        add_interface_button.setKey(getActivity().getString(R.string.pref_button_add_interface_key));
        add_interface_button.setIcon(R.mipmap.drawable_plus);

        add_interface_button.setEntries(all_interfaces.toArray(new CharSequence[all_interfaces.size()]));
        add_interface_button.setEntryValues(all_interfaces.toArray(new CharSequence[all_interfaces.size()]));
        add_interface_button.setDefaultValue("1");

        add_interface_button.setOnPreferenceChangeListener(preference_change_listener);

        preference_screen.addPreference(add_interface_button);
    }
}
