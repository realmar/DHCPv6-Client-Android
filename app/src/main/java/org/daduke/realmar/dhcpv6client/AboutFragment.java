package org.daduke.realmar.dhcpv6client;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Anastassios Martakos on 9/22/15.
 */
public class AboutFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.about_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        String version_name = "not available";

        try {
            version_name = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        }catch(Exception e) {
        }

        TextView about_version_name = (TextView) getView().findViewById(R.id.about_version_name);
        version_name = "App Version: " + version_name;

        about_version_name.setText(version_name);
    }
}
