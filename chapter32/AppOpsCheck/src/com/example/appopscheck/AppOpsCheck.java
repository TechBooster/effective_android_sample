package com.example.appopscheck;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * AppOps状態確認用クラス
 */
public class AppOpsCheck {
    /* AppOpsService name from Context in android_4.4.0_r1.0 */
    private static final String APP_OPS_SERVICE = "appops";

    /* class and method names for reflection */
    private static final String APP_OPS_MANAGER = "android.app.AppOpsManager";
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";

    /* AppOps mode from AppOpsManager in android_4.4.0_r1.0 */
    public static final int MODE_ALLOWED = 0;
    public static final int MODE_IGNORED = 1;
    public static final int MODE_ERRORED = 2;

    /* operation switch code from AppOpsmanager in android_4.4.0_r1.0 */
    public static final int OP_NONE = -1;
    public static final int OP_COARSE_LOCATION = 0;
    public static final int OP_FINE_LOCATION = 1;
    public static final int OP_GPS = 2;
    public static final int OP_VIBRATE = 3;
    public static final int OP_READ_CONTACTS = 4;
    public static final int OP_WRITE_CONTACTS = 5;
    public static final int OP_READ_CALL_LOG = 6;
    public static final int OP_WRITE_CALL_LOG = 7;
    public static final int OP_READ_CALENDAR = 8;
    public static final int OP_WRITE_CALENDAR = 9;
    public static final int OP_WIFI_SCAN = 10;
    public static final int OP_POST_NOTIFICATION = 11;
    public static final int OP_NEIGHBORING_CELLS = 12;
    public static final int OP_CALL_PHONE = 13;
    public static final int OP_READ_SMS = 14;
    public static final int OP_WRITE_SMS = 15;
    public static final int OP_RECEIVE_SMS = 16;
    public static final int OP_RECEIVE_EMERGECY_SMS = 17;
    public static final int OP_RECEIVE_MMS = 18;
    public static final int OP_RECEIVE_WAP_PUSH = 19;
    public static final int OP_SEND_SMS = 20;
    public static final int OP_READ_ICC_SMS = 21;
    public static final int OP_WRITE_ICC_SMS = 22;
    public static final int OP_WRITE_SETTINGS = 23;
    public static final int OP_SYSTEM_ALERT_WINDOW = 24;
    public static final int OP_ACCESS_NOTIFICATIONS = 25;
    public static final int OP_CAMERA = 26;
    public static final int OP_RECORD_AUDIO = 27;
    public static final int OP_PLAY_AUDIO = 28;
    public static final int OP_READ_CLIPBOARD = 29;
    public static final int OP_WRITE_CLIPBOARD = 30;
    public static final int OP_TAKE_MEDIA_BUTTONS = 31;
    public static final int OP_TAKE_AUDIO_FOCUS = 32;
    public static final int OP_AUDIO_MASTER_VOLUME = 33;
    public static final int OP_AUDIO_VOICE_VOLUME = 34;
    public static final int OP_AUDIO_RING_VOLUME = 35;
    public static final int OP_AUDIO_MEDIA_VOLUME = 36;
    public static final int OP_AUDIO_ALARM_VOLUME = 37;
    public static final int OP_AUDIO_NOTIFICATION_VOLUME = 38;
    public static final int OP_AUDIO_BLUETOOTH_VOLUME = 39;
    public static final int OP_WAKE_LOCK = 40;
    public static final int OP_MONITOR_LOCATION = 41;
    public static final int OP_MONITOR_HIGH_POWER_LOCATION = 42;
    
    private static final int _NUM_OP_API_LEVEL_18 = 31;
    private static final int _NUM_OP_API_LEVEL_19 = 43;

    /* <operation number>-<operation name> from AppOpsManager in android_4.4.0_r1.0 */
    private static String[] sOpNames = new String[]{
            "COARSE_LOCATION",
            "FINE_LOCATION",
            "GPS",
            "VIBRATE",
            "READ_CONTACTS",
            "WRITE_CONTACTS",
            "READ_CALL_LOG",
            "WRITE_CALL_LOG",
            "READ_CALENDAR",
            "WRITE_CALENDAR",
            "WIFI_SCAN",
            "POST_NOTIFICATION",
            "NEIGHBORING_CELLS",
            "CALL_PHONE",
            "READ_SMS",
            "WRITE_SMS",
            "RECEIVE_SMS",
            "RECEIVE_EMERGECY_SMS",
            "RECEIVE_MMS",
            "RECEIVE_WAP_PUSH",
            "SEND_SMS",
            "READ_ICC_SMS",
            "WRITE_ICC_SMS",
            "WRITE_SETTINGS",
            "SYSTEM_ALERT_WINDOW",
            "ACCESS_NOTIFICATIONS",
            "CAMERA",
            "RECORD_AUDIO",
            "PLAY_AUDIO",
            "READ_CLIPBOARD",
            "WRITE_CLIPBOARD",
            "TAKE_MEDIA_BUTTONS",
            "TAKE_AUDIO_FOCUS",
            "AUDIO_MASTER_VOLUME",
            "AUDIO_VOICE_VOLUME",
            "AUDIO_RING_VOLUME",
            "AUDIO_MEDIA_VOLUME",
            "AUDIO_ALARM_VOLUME",
            "AUDIO_NOTIFICATION_VOLUME",
            "AUDIO_BLUETOOTH_VOLUME",
            "WAKE_LOCK",
            "MONITOR_LOCATION",
            "MONITOR_HIGH_POWER_LOCATION",
    };

    private Context mContext;
    private Object mAppOpsManager;

    public AppOpsCheck(Context context) {
        mContext = context;
        mAppOpsManager = context.getSystemService(APP_OPS_SERVICE);
    }

    public int checkOp(int op, String packageName) {
        int appOpsMode = checkOpNoThrow(op, packageName);

        if (appOpsMode == MODE_ERRORED) {
            throw new SecurityException("Operation not allowed");
        }

        return appOpsMode;
    }

    public int checkOpNoThrow(int op, String packageName) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return MODE_ALLOWED;
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR2 && op >= _NUM_OP_API_LEVEL_18) {
            return MODE_ALLOWED;
        }


        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && op >= _NUM_OP_API_LEVEL_19) {
            return MODE_ALLOWED;
        }

        int appOpsMode = MODE_ERRORED; /* AppOpsManager.MODE_ERRORED */
        Class appOpsClass = null;
        Method checkOpNoThrowMethod = null;
        try {
            int uid = getLinuxUidOfPackage(packageName);
            appOpsClass = Class.forName(APP_OPS_MANAGER); /* Context.APP_OPS_MANAGER */
            checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW,
                    Integer.TYPE, Integer.TYPE, String.class);
            Object ret = checkOpNoThrowMethod.invoke(mAppOpsManager,
                    op, /* AppOpsManager.OP_READ_CONTACTS */
                    uid, /* Linux UID */
                    packageName
            );
            appOpsMode = ((Integer) ret).intValue();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appOpsMode;
    }

    /**
     * get operation number of operation name (like "GPS")
     *
     * @param name operation name
     * @return operation number if name is existing, otherwise -1
     */
    public int opNameToOp(String name) {
        return Arrays.asList(sOpNames).indexOf(name);
    }

    private int getLinuxUidOfPackage(String packageName)
            throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        return pm.getApplicationInfo(packageName, 0).uid;
    }
}