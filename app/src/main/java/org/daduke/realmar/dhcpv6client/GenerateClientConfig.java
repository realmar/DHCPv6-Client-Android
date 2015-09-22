package org.daduke.realmar.dhcpv6client;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by Anastassios Martakos on 8/17/15.
 */
public class GenerateClientConfig extends AsyncTask<String, String, String> {
    private ArrayList<ArrayList<String>> interfaces = new ArrayList<ArrayList<String>>();
    private String final_config_c = "";
    private String final_config_c_dns = "";

    private Context context;

    private String config_template_c =
            "interface <INTERFACE> {\\n" +
                "send ia-na <IA-NA>;\\n" +
                "request domain-name-servers;\\n" +
                "request domain-name;\\n" +
                "script \\\"/etc/wide-dhcpv6/dhcp6c.script\\\";\\n" +
            "};\\n\\n" +

            "id-assoc na <IA-NA> {\\n" +
            "};\\n\\n";

    private String config_template_c_dns =
            "interface <INTERFACE> {\\n" +
                "request domain-name-servers;\\n" +
                "request domain-name;\\n" +
                "information-only;\\n" +
                "script \\\"/etc/wide-dhcpv6/dhcp6c.script\\\";\\n" +
            "};\\n\\n";

    ProgressDialog progDialog;

    public GenerateClientConfig(Context context) {
        this.context = context;
        progDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDialog.setMessage("Generating Configuration ...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setCancelable(false);
        progDialog.show();
    }
    @Override
    protected String doInBackground(String... aurl) {
        generate_client_config();
        return null;
    }
    @Override
    protected void onPostExecute(String unused) {
        super.onPostExecute(unused);
        MsgBoxes msg_box = new MsgBoxes();

        String full_check_result = DHCPv6Integrity.FullCheck();

        if(!full_check_result.equals("ok")) {
            if (full_check_result.equals("md5")) {
                AlertDialog error = (AlertDialog) msg_box.one_button(context, "Error", context.getString(R.string.check_md5), false);
                error.show();
            } else if (full_check_result.equals("exist")) {
                AlertDialog error = (AlertDialog) msg_box.one_button(context, "Error", context.getString(R.string.check_exist), false);
                error.show();
            } else {
                AlertDialog error = (AlertDialog) msg_box.one_button(context, "Error", context.getString(R.string.check_unexpected), false);
                error.show();
            }
        }

        progDialog.dismiss();
    }

    private void generate_client_config() {
        ArrayList<String> all_configured_interfaces_base = Misc.get_all_configured_interfaces(context);
        ArrayList<ArrayList<String>> all_configured_interfaces_final = new ArrayList<ArrayList<String>>();

        ArrayList<String> used_ianas = new ArrayList<String>();

        for(final String conf_interface : all_configured_interfaces_base) {
            boolean valid_iana = false;
            String tmp_iana = "";

            while(!valid_iana) {
                tmp_iana =
                    Integer.toString(Misc.get_random_small()) +
                    Integer.toString(Misc.get_random_small()) +
                    Integer.toString(Misc.get_random_small()) +
                    Integer.toString(Misc.get_random_small()) +
                    Integer.toString(Misc.get_random_small()) +
                    Integer.toString(Misc.get_random_small()) +
                    Integer.toString(Misc.get_random_small()) +
                    Integer.toString(Misc.get_random_small()) +
                    Integer.toString(Misc.get_random_small());

                boolean used = false;

                for(String used_iana : used_ianas) {
                    if(used_iana.equals(tmp_iana)) {
                        used_ianas.add(tmp_iana);
                        used = true;
                    }
                }

                if(!used) {
                    valid_iana = true;
                }
            }

            final String iana = tmp_iana;

            all_configured_interfaces_final.add(new ArrayList<String>() {{
                add(conf_interface);
                add(iana);
            }});
        }

        interfaces = all_configured_interfaces_final;

        generate_config();
        apply_config();
    }


    public void add_interface(ArrayList inter_face) {
        interfaces.add(inter_face);
    }

    public void generate_config() {
        for(int i = 0; i < interfaces.size(); i++) {
            String current_config_c = config_template_c;
            String current_config_c_dns = config_template_c_dns;

            current_config_c = current_config_c.replaceAll("<INTERFACE>", interfaces.get(i).get(0));
            current_config_c = current_config_c.replaceAll("<IA-NA>", interfaces.get(i).get(1));
            current_config_c_dns = current_config_c_dns.replaceAll("<INTERFACE>", interfaces.get(i).get(0));

            final_config_c += current_config_c;
            final_config_c_dns += current_config_c_dns;
        }
    }

    public void apply_config() {
        SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = shared_preferences.edit();

        if(!shared_preferences.contains(Constants.DHCP6C_CONF)) { editor.putString(Constants.DHCP6C_CONF, "null"); }
        if(!shared_preferences.contains(Constants.DHCP6CDNS_CONF)) { editor.putString(Constants.DHCP6CDNS_CONF, "null"); }

        DHCPv6Integrity.delete_client_conf();

        SUCalls.mount_rw();
        String md5_c = DHCPv6Integrity.write_client_conf("/system/etc/wide-dhcpv6/dhcp6c.conf", final_config_c);
        String md5_c_dns = DHCPv6Integrity.write_client_conf("/system/etc/wide-dhcpv6/dhcp6cDNS.conf", final_config_c_dns);
        SUCalls.mount_ro();

        editor.putString(Constants.DHCP6C_CONF, md5_c);
        editor.putString(Constants.DHCP6CDNS_CONF, md5_c_dns);
        editor.commit();
    }
}
