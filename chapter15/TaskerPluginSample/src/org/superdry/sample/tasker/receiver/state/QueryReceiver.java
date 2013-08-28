
package org.superdry.sample.tasker.receiver.state;

import org.superdry.sample.tasker.bundle.PluginBundleManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;

public final class QueryReceiver extends BroadcastReceiver {

    private static final int RESULT_CONDITION_UNSATISFIED = com.twofortyfouram.locale.Intent.RESULT_CONDITION_UNSATISFIED;
    private static final int RESULT_CONDITION_SATISFIED = com.twofortyfouram.locale.Intent.RESULT_CONDITION_SATISFIED;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);

        if (PluginBundleManager.isStateBundleValid(bundle)) {
            final boolean isScreenOn = (((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn());
            final boolean conditionState = bundle.getBoolean(PluginBundleManager.BUNDLE_EXTRA_BOOLEAN_STATE);

            if (isScreenOn ^ conditionState) {
                setResultCode(RESULT_CONDITION_UNSATISFIED);
            } else {
                setResultCode(RESULT_CONDITION_SATISFIED);
            }
            context.startService(new Intent(context, BackgroundService.class).putExtra(BackgroundService.EXTRA_BOOLEAN_WAS_SCREEN_ON, isScreenOn));
            ServiceWakeLockManager.aquireLock(context);
        }
    }
}
