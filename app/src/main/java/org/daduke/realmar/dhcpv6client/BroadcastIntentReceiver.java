package org.daduke.realmar.dhcpv6client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Anastassios Martakos on 7/30/15.
 */


public class BroadcastIntentReceiver extends BroadcastReceiver {
    private boolean do_all = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            do_all = GetIPv6Address.PreGetIPv6Address(context, do_all);
        }else if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            new PostBootComplete(context, do_all).execute();
        }

        Misc.send_ui_request_refresh_ips();
    }
}
