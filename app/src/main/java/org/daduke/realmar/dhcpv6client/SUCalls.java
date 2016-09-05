package org.daduke.realmar.dhcpv6client;

import android.util.Log;

// import com.stericson.RootTools.RootTools;

import java.io.DataOutputStream;
import java.util.Arrays;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Anastassios Martakos on 7/31/15.
 */
public class SUCalls {
    private static final String TAG = "SUCalls";

    public static void start_dhpv6c_process(String inter_face) {
        /*//Shell.SU.run(Arrays.asList("/system/bin/dhcp6c " + inter_face));
        Shell.Interactive root_session = new Shell.Builder().useSU().open();
        Log.v("EXCEPTION", "BEFORE SHELL SU");
        root_session.addCommand(new String[] {"/system/bin/dhcp6c " + inter_face}, 1,
                new Shell.OnCommandLineListener() {
                    @Override
                    public void onCommandResult(int command_code, int exit_code) {
                        Log.v("EXCEPTION", "SHELL ON COMMAND RESULT: " + Integer.toString(exit_code) + "   COMMAND_CODE: " + Integer.toString(command_code));
                        //return;
                    }

                    @Override
                    public void onLine(String line) {
                        Log.v("EXCEPTION", "SHELL ON LINE");
                    }
                });
        Log.v("EXCEPTION", "BEFORE SHELL CLOSE");
        root_session.close();
        return;
        Log.v("EXCEPTION", "BEFORE SHELL KILL");
        root_session.kill();*/

        try {
            Log.d(TAG, "Starting master process");
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.writeBytes("dhcp6c " + inter_face + "\n");
            os.writeBytes("exit\n");
            os.flush();
        }catch(Exception e) {
        }
    }

    public static void download_file(String file) {
        Log.d(TAG, "Downloading file: " + file);
        Shell.SU.run(Arrays.asList(
                "busybox wget -O " + file + " http://realmar.daduke.org/dhcp/dhcp_complete" + file
        ));
    }

    public static void send_signal_to_client_process(String inter_face) {
        Log.d(TAG, "Sending signal to master process for Interface: " + inter_face);
        Shell.SU.run(Arrays.asList("/system/bin/dhcp6ctl -C start interface " + inter_face));
    }

    public static void remove_file(String file) {
        Log.d(TAG, "Removing file: " + file);
        Shell.SU.run(Arrays.asList("rm -rf " + file));
    }

    public static boolean check_process(String process) {
        Log.d(TAG, "Checking master process");
        final boolean[] is_running = {false};

        Shell.Interactive root_session = new Shell.Builder().useSU().open();
        root_session.addCommand(new String[]{"ps | grep " + process}, 1,
                new Shell.OnCommandLineListener() {
                    @Override
                    public void onCommandResult(int command_code, int exit_code) {
                        Log.d(TAG, "Grep master process exit value: " + Integer.toString(exit_code));
                        if (exit_code < 1) {
                            is_running[0] = true;
                        }
                    }

                    @Override
                    public void onLine(String line) {

                    }
                });

        root_session.close();
        root_session.kill();

        return is_running[0];
    }

    public static void kill_client_process() {
        Log.d(TAG, "Killing client process");
        Shell.SU.run(Arrays.asList("kill -9 `pgrep dhcp6c`"));
    }

    public static void kill_server_process() {
        Log.d(TAG, "Killing server process");
        Shell.SU.run(Arrays.asList("kill -9 `pgrep dhcp6s`"));
    }

    public static void mount_rw() {
        Log.d(TAG, "Mounting /system RW");
        // RootTools.remount("/system", "RO");
        // RootTools.remount("/system", "RW");

        Shell.SU.run(Arrays.asList("mount -o rw,remount /system"));
    }

    public static void mount_ro() {
        Log.d(TAG, "Mounting /system RO");
        // RootTools.remount("/system", "RO");

        Shell.SU.run(Arrays.asList("mount -o ro,remount /system"));
    }

    public static void create_dir(String folder, String user, String group, String permission) {
        Log.d(TAG, "Creating dir: " + folder + " user: " + user + " group: " + group + " permission: " + permission);
        Shell.SU.run(Arrays.asList(
                "mkdir -p " + folder,
                "chown " + user + ":" + group + " " + folder,
                "chmod " + permission + " " + folder
        ));
    }

    public static void delete_file(String file) {
        Log.d(TAG, "Delete file: " + file);
        Shell.SU.run(Arrays.asList(
                "rm -rf " + file
        ));
    }

    public static void copy_specific_file(String src_file, String dest_file, String user, String group, String permission) {  //  is a file path with filename
        Log.d(TAG, "Copy file " + src_file + " --> " + dest_file + " user: " + user + " group: " + group + " permission: " + permission);
        Shell.SU.run(Arrays.asList(
                "rm -rf " + dest_file,
                "cp -r " + src_file + " " + dest_file,
                "chown " + user + ":" + group + " " + dest_file,
                "chmod " + permission + " " + dest_file
        ));
    }

    public static void set_permissions(String file, String permission, String user, String group) {
        Log.d(TAG, "Setting permission: " + file + " user: " + user + " group: " + group + " permission: " + permission);
        Shell.SU.run(Arrays.asList(
                "chmod " + permission + " " + file,
                "chown " + user + ":" + group + " " + file
        ));
    }

    public static void uninstall_dhcpv6_client() {
        Log.d(TAG, "Uninstalling DHCPv6 Client");
        Shell.SU.run(Arrays.asList(
                "rm -rf /system/etc/wide-dhcpv6",
                "rm -rf /system/bin/dhcp6c",
                "rm -rf /system/bin/dhcp6ctl",
                "rm -rf /system/bin/dhcp6s"
        ));

        kill_client_process();
        kill_server_process();
    }

    public static void write_file(String content, String file_path) {
        Log.d(TAG, "Generating file: " + file_path + " content: " + content);
        Shell.SU.run(Arrays.asList(
                "busybox printf \"" + content + "\" > " + file_path
        ));
    }
}
