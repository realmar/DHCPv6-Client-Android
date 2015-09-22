package org.daduke.realmar.dhcpv6client;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Anastassios Martakos on 8/24/15.
 */
public class MainFragment extends Fragment {
    private Button refresh_button;
    private BroadcastReceiver resultReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultReceiver = createBroadcastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(resultReceiver, new IntentFilter("org.daduke.realmar.dhcpv6client"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        refresh_button = (Button) getView().findViewById(R.id.button_refresh);

        refresh_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_ip_addresses();
            }
        });

        get_ip_addresses();
    }

    @Override
    public void onResume() {
        super.onResume();
        get_ip_addresses();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (resultReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(resultReceiver);
        }
    }

    private BroadcastReceiver createBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getStringExtra("refresh_ips") == "refresh_ips") {
                    get_ip_addresses();
                }
            }
        };
    }

    public void get_ip_addresses() {
        ArrayList[] ips = Misc.get_ips();

        final ListView list_view = (ListView) getView().findViewById(R.id.ip_addresses);

        ArrayList<Object> ip_collection = new ArrayList<>();

        ip_collection.add("IPv4 Addresses");

        for(Object ip : ips[0]) {
            ip_collection.add(new IpCollector(ip.toString()));
        }

        ip_collection.add("IPv6 Addresses");

        for(Object ip : ips[1]) {
            ip_collection.add(new IpCollector(ip.toString()));
        }

        list_view.setAdapter(new CustomListViewAdpater(getActivity(), ip_collection));

        list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                IpCollector ip_address = (IpCollector) ((ListView) getView().findViewById(R.id.ip_addresses)).getItemAtPosition(position);

                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ip_address", ip_address.getIp());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getActivity(), getString(R.string.ip_to_clipboard), Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }
}
