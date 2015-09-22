package org.daduke.realmar.dhcpv6client;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Anastassios Martakos on 13.08.2015.
 */
public class DHCPv6Integrity {
    private static final String TAG = "DHCPv6Integrity";

    private static String[] required_files = {
            "/system/bin/dhcp6c",
            //  "/system/etc/wide-dhcpv6/dhcp6c.conf",
            "/system/etc/wide-dhcpv6/dhcp6c.script",
            //  "/system/etc/wide-dhcpv6/dhcp6cDNS.conf",
            "/system/etc/wide-dhcpv6/dhcp6cctlkey"
    };

    private static String[] required_files_update = {
            "/system/bin/dhcp6ctl",
            "/system/bin/dhcp6s",
            "/system/etc/wide-dhcpv6/dhcp6s.conf",
            "/system/etc/wide-dhcpv6/dhcp6sctlkey"
    };

    private static String[] config_files = {
            "/system/etc/wide-dhcpv6/dhcp6c.conf",
            "/system/etc/wide-dhcpv6/dhcp6cDNS.conf"
    };

    private static Map<String, String> md5_sums = new HashMap<String, String>();
    static {
        md5_sums.put("/system/bin/dhcp6c",                     "3374072ddc7390e94f5b800e0f6475f4");
        //  md5_sums.put("/system/etc/wide-dhcpv6/dhcp6c.conf",    "1103f18a20f607952c9911cda488c996");
        md5_sums.put("/system/etc/wide-dhcpv6/dhcp6c.script",  "09ab71119edeea93a9517d17501d32be");
        //  md5_sums.put("/system/etc/wide-dhcpv6/dhcp6cDNS.conf", "fb99396abe72d95d172bcaa30b1f3bc3");
        md5_sums.put("/system/etc/wide-dhcpv6/dhcp6cctlkey",   "3602680e0fd4987eb2de8a7a8ea62044");
        md5_sums.put("/system/etc/wide-dhcpv6/dhcp6sctlkey",   "d3539eee2e668fc84c40b918ecfdd9ee");
        md5_sums.put("/system/bin/dhcp6ctl",                   "f36d6f20813c2a70ad736b3e937cb1b3");
        md5_sums.put("/system/bin/dhcp6s",                     "fda776931320ef234911f1c8efe76637");
        md5_sums.put("/system/etc/wide-dhcpv6/dhcp6s.conf",    "8901e99ab22f507bc89dffaed98aa45a");
        md5_sums.put("dhcpv6_base.zip",                        "a8a0a946966474d238cd126202733a4a");
        md5_sums.put("dhcpv6_update.zip",                      "0f49fa94d431e1766a367af9be23bfd3");
    }

    public static String FullCheck() {
        String exists = "ok";

        exists = CheckBase();
        exists = CheckUpdate();

        Log.d(TAG, "Check full result: " + exists);

        return exists;
    }

    public static String CheckBase() {
        String exists = "ok";

        for(String file : required_files) {
            if(!check_md5(file)) { exists = "md5"; }
        }

        for(String file : required_files) {
            if(!check_file(file)) { exists = "exist"; }
        }

        Log.d(TAG, "Check base result: " + exists);

        return exists;
    }

    public static String CheckUpdate() {
        String exists = "ok";

        for(String file : required_files_update) {
            if(!check_md5(file)) { exists = "md5"; }
        }

        for(String file : required_files_update) {
            if(!check_file(file)) { exists = "exist"; }
        }

        Log.d(TAG, "Check update result: " + exists);

        return exists;
    }

    public static boolean check_config_files(Context context) {
        boolean valid = true;

        SharedPreferences shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);

        File file_c = new File(config_files[0]);
        File file_c_dns = new File(config_files[1]);

        if(file_c.exists() && file_c_dns.exists()) {
            if (!shared_preferences.getString(Constants.DHCP6C_CONF, "").equals(DHCPv6Integrity.calculateMD5(file_c)) ||
                    !shared_preferences.getString(Constants.DHCP6CDNS_CONF, "").equals(DHCPv6Integrity.calculateMD5(file_c_dns))) {
                valid = false;
            }
        }else{
            valid= false;
        }


        return valid;
    }

    public static void install_all(Context context, boolean download_files) {
        String tmp_dir = context.getCacheDir().toString();

        if(!download_files) {
            Log.d(TAG, "INSTALLING: tmp_dir: " + tmp_dir);
        }

        SUCalls.mount_rw();

        SUCalls.create_dir("/system/etc/wide-dhcpv6", "root", "shell", "755");
        SUCalls.create_dir("/system/bin", "root", "shell", "755");

        if(!download_files) {

            copy_resource(R.raw.dhcpv6_base, tmp_dir + "/dhcpv6_base.zip", context);

            Log.d(TAG, "INSTALLING: tmp_dir: dhcpv6_base.zip: " + Boolean.toString(check_file(tmp_dir + "/dhcpv6_base.zip")));

            Decompress d = new Decompress(tmp_dir + "/dhcpv6_base.zip", tmp_dir + "/");
            d.unzip();

            Log.d(TAG, "INSTALLING: tmp_dir: dhcp_complete/system/bin/dhcp6c: " + Boolean.toString(check_file(tmp_dir + "/dhcp_complete/system/bin/dhcp6c")));
            Log.d(TAG, "INSTALLING: tmp_dir: dhcp_complete/system/etc/wide-dhcpv6/dhcp6cctlkey: " + Boolean.toString(check_file(tmp_dir + "/dhcp_complete/system/etc/wide-dhcpv6/dhcp6cctlkey")));

            Log.d(TAG, "INSTALLING: only checked 2 files (normal)");

            for (String file : required_files) {
                SUCalls.copy_specific_file(tmp_dir + "/dhcp_complete" + file, file, "root", "shell", "755");
            }

            SUCalls.delete_file(tmp_dir + "/dhcp_complete");
        }else{
            for (String file : required_files) {
                SUCalls.download_file(file);
                SUCalls.set_permissions(file, "755", "root", "shell");
            }
        }
        install_update(context, download_files);

        if(!download_files) {
            SUCalls.delete_file(tmp_dir + "/dhcp_complete");
            SUCalls.delete_file(tmp_dir + "/dhcpv6_base.zip");
            SUCalls.delete_file(tmp_dir + "/dhcpv6_update.zip");
        }

        SUCalls.mount_ro();
    }

    public static void install_update(Context context, boolean download_files) {
        String tmp_dir = context.getCacheDir().toString();

        if(!download_files) {
            Log.d(TAG, "INSTALLING: tmp_dir: " + tmp_dir);
        }

        SUCalls.mount_rw();

        SUCalls.create_dir("/system/etc/wide-dhcpv6", "root", "shell", "755");
        SUCalls.create_dir("/system/bin", "root", "shell", "755");

        if(!download_files) {

            copy_resource(R.raw.dhcpv6_update, tmp_dir + "/dhcpv6_update.zip", context);

            Log.d(TAG, "INSTALLING: tmp_dir: dhcpv6_update.zip: " + Boolean.toString(check_file(tmp_dir + "/dhcpv6_update.zip")));

            Decompress d = new Decompress(tmp_dir + "/dhcpv6_update.zip", tmp_dir + "/");
            d.unzip();

            Log.d(TAG, "INSTALLING: tmp_dir: dhcp_complete/system/bin/dhcp6ctl: " + Boolean.toString(check_file(tmp_dir + "/dhcp_complete/system/bin/dhcp6ctl")));
            Log.d(TAG, "INSTALLING: tmp_dir: dhcp_complete/system/etc/wide-dhcpv6/dhcp6sctlkey: " + Boolean.toString(check_file(tmp_dir + "/dhcp_complete/system/etc/wide-dhcpv6/dhcp6sctlkey")));

            Log.d(TAG, "INSTALLING: only checked 2 files (normal)");

            for (String file : required_files_update) {
                SUCalls.copy_specific_file(tmp_dir + "/dhcp_complete" + file, file, "root", "shell", "755");
            }

            SUCalls.delete_file(tmp_dir + "/dhcpv6_update.zip");
        }else{
            for (String file : required_files_update) {
                SUCalls.download_file(file);
                SUCalls.set_permissions(file, "755", "root", "shell");
            }
        }

        SUCalls.mount_ro();
    }

    public static void delete_client_conf() {
        SUCalls.remove_file("/system/etc/wide-dhcpv6/dhcp6c.conf");
        SUCalls.remove_file("/system/etc/wide-dhcpv6/dhcp6cDNS.conf");
    }

    public static String write_client_conf(String file, String content) {
        SUCalls.write_file(content,file);
        SUCalls.set_permissions(file, "755", "root", "shell");
        return calculateMD5(new File(file));
    }

    private static void copy_resource(int resource, String dest_dir, Context context) {
        try {
            InputStream in = context.getResources().openRawResource(resource);
            OutputStream out = new FileOutputStream(new File(dest_dir));
            byte[] buffer = new byte[1024];
            int len;
            while((len = in.read(buffer, 0, buffer.length)) != -1){
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        }catch (Exception e) {

        }
    }

    private static boolean check_md5(String file) {
        String md5_file = calculateMD5(new File(file));

        if(md5_file == null) {
            return false;
        }

        if(!md5_file.equals(md5_sums.get(file))) {
            return false;
        }else{
            return true;
        }
    }

    private static boolean check_file(String file) {
        File check_file = new File(file);
        return check_file.exists();
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            //  Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            //  Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            Log.d(TAG, "MD5: Unable to process file for MD5: " + e.toString());
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                //  Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    public static class Decompress {
        private String _zipFile;
        private String _location;

        public Decompress(String zipFile, String location) {
            _zipFile = zipFile;
            _location = location;

            _dirChecker("");
        }

        public void unzip() {
            try  {
                FileInputStream fin = new FileInputStream(_zipFile);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {

                    if(ze.isDirectory()) {
                        _dirChecker(ze.getName());
                    } else {
                        FileOutputStream fout = new FileOutputStream(_location + ze.getName());
                        for (int c = zin.read(); c != -1; c = zin.read()) {
                            fout.write(c);
                        }

                        zin.closeEntry();
                        fout.close();
                    }

                }
                zin.close();
            } catch(Exception e) {
                //  Log.e("EXCEPTION", "unzip", e);
            }

        }

        private void _dirChecker(String dir) {
            File f = new File(_location + dir);

            if(!f.isDirectory()) {
                f.mkdirs();
            }
        }
    }
}
