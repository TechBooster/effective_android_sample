
package org.superdry.sample.tasker;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public final class Constants
{
    public static final String TAG = "SuperdryTaskerPlugin";

    public static int getVersionCode(final Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
