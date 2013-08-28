
package org.superdry.sample.tasker.receiver.state;

import org.superdry.sample.tasker.Constants;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public final class ServiceWakeLockManager {
    private static WakeLock sWakeLock;

    public static void aquireLock(final Context context) {

        if (null == sWakeLock) {
            sWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, Constants.TAG);
            sWakeLock.setReferenceCounted(true);
        }
        sWakeLock.acquire();
    }

    public static void releaseLock() {
        sWakeLock.release();
    }
}
