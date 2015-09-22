package org.daduke.realmar.dhcpv6client;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

/**
 * Created by Anastassios Martakos on 8/18/15.
 */
public class PostBootComplete extends AsyncTask<String, String, String> {
    private Context context;
    private boolean do_all;

    public PostBootComplete(Context context, boolean do_all) {
        this.context = context;
        this.do_all = do_all;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... aurl) {
        try {
            SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = shared_preferences.edit();

            editor.putInt(Constants.LAST_STATE, -200);
            editor.commit();

            Thread.sleep(20000);
            GetIPv6Address.PreGetIPv6Address(context, do_all);
        }catch (Exception e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(String unused) {
    }

}