
package org.superdry.sample.tasker.activity;

import org.superdry.sample.tasker.R;
import org.superdry.sample.tasker.bundle.PluginBundleManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

public final class EditActionActivity extends AbstractPluginActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_action_activity);
        final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        if (null == savedInstanceState) {
            if (PluginBundleManager.isActionBundleValid(localeBundle)) {
                final String message = localeBundle.getString(PluginBundleManager.BUNDLE_EXTRA_STRING_MESSAGE);
                ((EditText) findViewById(R.id.toast_text)).setText(message);
            }
        }
    }

    @Override
    public void finish() {
        if (!isCanceled()) {
            final String message = ((EditText) findViewById(R.id.toast_text)).getText().toString();
            if (message.length() > 0) {
                final Intent resultIntent = new Intent();
                final Bundle resultBundle = PluginBundleManager.generateBundle(getApplicationContext(), message);
                final String blurb = generateBlurb(getApplicationContext(), message);
                resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
                resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, blurb);
                setResult(RESULT_OK, resultIntent);
            }
        }
        super.finish();
    }

    static String generateBlurb(final Context context, final String message) {
        final int maxBlurbLength = context.getResources().getInteger(R.integer.twofortyfouram_locale_maximum_blurb_length);
        if (message.length() > maxBlurbLength) {
            return message.substring(0, maxBlurbLength);
        }
        return message;
    }
}
