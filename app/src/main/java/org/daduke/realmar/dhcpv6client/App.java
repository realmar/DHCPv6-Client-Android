package org.daduke.realmar.dhcpv6client;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * @author Mygod
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(new BroadcastIntentReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
}
