package org.daduke.realmar.dhcpv6client;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Created by Anastassios Martakos on 8/7/15.
 */
public class DonationFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    Preference donate_001;
    Preference donate_005;
    Preference donate_010;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.donate);

        donate_001 = (Preference) findPreference("pref_button_donate_amount_1");

        donate_001.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((MainActivity)getActivity()).purchase_item(Constants.DONATION_001);
                return true;
            }
        });

        donate_005 = (Preference) findPreference("pref_button_donate_amount_5");
        donate_005.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((MainActivity)getActivity()).purchase_item(Constants.DONATION_005);
                return true;
            }
        });

        donate_010 = (Preference) findPreference("pref_button_donate_amount_10");
        donate_010.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((MainActivity) getActivity()).purchase_item(Constants.DONATION_010);
                return true;
            }
        });

        findPreference("pref_button_donate_amount_1").setTitle(findPreference("pref_button_donate_amount_1").getTitle() + " " + MainActivity.donation_001_price);
        findPreference("pref_button_donate_amount_5").setTitle(findPreference("pref_button_donate_amount_5").getTitle() + " " + MainActivity.donation_005_price);
        findPreference("pref_button_donate_amount_10").setTitle(findPreference("pref_button_donate_amount_10").getTitle() + " " + MainActivity.donation_010_price);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }
}
