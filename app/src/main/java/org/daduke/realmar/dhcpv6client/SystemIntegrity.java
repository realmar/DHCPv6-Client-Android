package org.daduke.realmar.dhcpv6client;

import android.os.StatFs;

/**
 * Created by Anastassios Martakos on 8/31/15.
 */
public class SystemIntegrity {
    private static final String TAG = "SystemIntegrity";

    private static String[] unsupported_arches = {
            Constants.ARCH_X86
    };

    private static long base   = 117581;
    private static long update = 131166;
    private static long config = 6000;

    public static boolean full_check() {
        return check_disk_space("/system", base + update + config);
    }

    public static boolean update_check() {
        return check_disk_space("/system", update + config);
    }

    public static String check_arch() {
        String current_arch = "ok";
        String system_arch = System.getProperty("os.arch");

        for(String arch : unsupported_arches) {
            if(system_arch.toLowerCase().contains(arch.toLowerCase())) {
                current_arch = arch;
                break;
            }
        }

        return current_arch;
    }

    private static boolean check_disk_space(String path, long size) {
        StatFs stat = new StatFs(path);
        long bytes_available = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();

        if(bytes_available - size > 0) {
            return true;
        }else{
            return false;
        }
    }
}
