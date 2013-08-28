
package org.superdry.sample.tasker.receiver.action;

import org.superdry.sample.tasker.bundle.PluginBundleManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public final class FireReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        if (PluginBundleManager.isActionBundleValid(bundle)) {
            final String message = bundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    }
}
