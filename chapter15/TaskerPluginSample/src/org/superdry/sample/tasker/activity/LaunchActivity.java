
package org.superdry.sample.tasker.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.twofortyfouram.locale.PackageUtilities;

public final class LaunchActivity extends Activity {
    private static final String APP_STORE_URI = "market://details?id=%s&referrer=utm_source=%s&utm_medium=app&utm_campaign=plugin";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final PackageManager manager = getPackageManager();
        final String compatiblePackage = PackageUtilities.getCompatiblePackage(manager, null);
        if (null != compatiblePackage) {
            final Intent i = manager.getLaunchIntentForPackage(compatiblePackage);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.US, APP_STORE_URI, "com.twofortyfouram.locale", getPackageName()))).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        finish();
    }
}
