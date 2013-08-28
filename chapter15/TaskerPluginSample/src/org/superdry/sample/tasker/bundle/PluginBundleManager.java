
package org.superdry.sample.tasker.bundle;

import org.superdry.sample.tasker.Constants;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

public final class PluginBundleManager {
    private static final String PACKAGE_NAME = PluginBundleManager.class.getPackage().getName();
    public static final String BUNDLE_EXTRA_BOOLEAN_STATE = PACKAGE_NAME + ".extra.BOOLEAN_STATE";
    public static final String BUNDLE_EXTRA_STRING_MESSAGE = PACKAGE_NAME + ".extra.STRING_MESSAGE";
    public static final String BUNDLE_EXTRA_INT_VERSION_CODE = PACKAGE_NAME + ".extra.INT_VERSION_CODE";

    public static boolean isActionBundleValid(final Bundle bundle) {
        if (null == bundle)
            return false;
        if (!bundle.containsKey(BUNDLE_EXTRA_STRING_MESSAGE))
            return false;
        if (!bundle.containsKey(BUNDLE_EXTRA_INT_VERSION_CODE))
            return false;
        if (2 != bundle.keySet().size())
            return false;
        if (TextUtils.isEmpty(bundle.getString(BUNDLE_EXTRA_STRING_MESSAGE)))
            return false;
        if (bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 0) != bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 1))
            return false;
        return true;
    }

    public static boolean isStateBundleValid(final Bundle bundle) {
        if (null == bundle)
            return false;
        if (!bundle.containsKey(BUNDLE_EXTRA_BOOLEAN_STATE))
            return false;
        if (!bundle.containsKey(BUNDLE_EXTRA_INT_VERSION_CODE))
            return false;
        if (2 != bundle.keySet().size())
            return false;
        if (bundle.getBoolean(BUNDLE_EXTRA_BOOLEAN_STATE, true) != bundle.getBoolean(BUNDLE_EXTRA_BOOLEAN_STATE, false))
            return false;
        if (bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 0) != bundle.getInt(BUNDLE_EXTRA_INT_VERSION_CODE, 1))
            return false;
        return true;
    }

    public static Bundle generateBundle(final Context context, final String message) {
        final Bundle result = new Bundle();
        result.putInt(BUNDLE_EXTRA_INT_VERSION_CODE, Constants.getVersionCode(context));
        result.putString(BUNDLE_EXTRA_STRING_MESSAGE, message);
        return result;
    }

    public static Bundle generateBundle(final Context context, final boolean isDisplayOn) {
        final Bundle result = new Bundle();
        result.putInt(BUNDLE_EXTRA_INT_VERSION_CODE, Constants.getVersionCode(context));
        result.putBoolean(BUNDLE_EXTRA_BOOLEAN_STATE, isDisplayOn);
        return result;
    }

}
