package org.daduke.realmar.dhcpv6client;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Anastassios Martakos on 8/7/15.
 */

public class CustomDHCPv6Fragment extends Fragment {
    private Button invoke_button;

    @Override
    public void onStart() {
        super.onStart();

        create_interface_menu();

        invoke_button = (Button) getView().findViewById(R.id.button_invoke_dhcpv6);
        invoke_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom_invoke_dhcpv6();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.custom_dhcpv6_fragment, container, false);
    }

    private void create_interface_menu() {
        ArrayList<String> interfaces = Misc.get_all_interfaces();

        LinearLayout linear_layout = (LinearLayout) getView().findViewById(R.id.cust_selection);

        for(Object inter_face : interfaces) {
            CheckBox check_box = new CheckBox(getActivity());

            check_box.setText(inter_face.toString());
            check_box.setChecked(false);

            linear_layout.addView(check_box);
        }
    }

    private void custom_invoke_dhcpv6() {
        ArrayList<String> interfaces_to_process = new ArrayList<String>();

        LinearLayout linear_layout = (LinearLayout) getView().findViewById(R.id.cust_selection);

        int interface_count = linear_layout.getChildCount();

        for(int i = 0; i < interface_count; i++) {
            CheckBox check_box = (CheckBox) linear_layout.getChildAt(i);
            if(check_box.isChecked()) {
                interfaces_to_process.add(check_box.getText().toString());
            }

            check_box.setChecked(false);
        }

        for(String inter_face : interfaces_to_process) {
            Toast.makeText(getActivity(), getString(R.string.trying_ipv6) + inter_face, Toast.LENGTH_SHORT).show();
            new GetIPv6Address(inter_face).execute();
        }
    }
}
