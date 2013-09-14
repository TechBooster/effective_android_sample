
package org.superdry.sample.tasker.activity;

import java.util.NoSuchElementException;

import org.superdry.sample.tasker.R;
import org.superdry.sample.tasker.bundle.PluginBundleManager;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public final class EditStateActivity extends AbstractPluginActivity {
    private ListView mList = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_state_activity);
        final Bundle localeBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
        mList = ((ListView) findViewById(R.id.list));
        mList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, android.R.id.text1, getResources().getStringArray(R.array.display_states)));
        if (null == savedInstanceState) {
            if (PluginBundleManager.isStateBundleValid(localeBundle)) {
                final boolean isDisplayOn = localeBundle.getBoolean(PluginBundleManager.BUNDLE_EXTRA_BOOLEAN_STATE);
                final int position = getPositionForIdInArray(getApplicationContext(), R.array.display_states, isDisplayOn ? R.string.list_on : R.string.list_off);
                mList.setItemChecked(position, true);
            }
        }
    }

    @Override
    public void finish() {
        if (!isCanceled()) {
            if (AdapterView.INVALID_POSITION != mList.getCheckedItemPosition()) {
                final int selectedResourceId = getResourceIdForPositionInArray(getApplicationContext(), R.array.display_states, mList.getCheckedItemPosition());
                final boolean isDisplayOn = setDisplaySetting(selectedResourceId);
                final Intent resultIntent = new Intent();
                final Bundle resultBundle = PluginBundleManager.generateBundle(getApplicationContext(), isDisplayOn);
                resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, resultBundle);
                resultIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, generateBlurb(getApplicationContext(), isDisplayOn));
                setResult(RESULT_OK, resultIntent);
            }
        }
        super.finish();
    }

    static boolean setDisplaySetting(int selectedResourceId) {
        if (R.string.list_on == selectedResourceId) {
            return true;
        } else if (R.string.list_off == selectedResourceId) {
            return false;
        } else {
            throw new AssertionError();
        }
    }

    static String generateBlurb(final Context context, final boolean isDisplayOn) {
        if (isDisplayOn)
            return context.getString(R.string.blurb_on);
        return context.getString(R.string.blurb_off);
    }

    static int getPositionForIdInArray(final Context context, final int arrayId, final int elementId) {

        TypedArray array = null;
        try {
            array = context.getResources().obtainTypedArray(arrayId);
            for (int x = 0; x < array.length(); x++) {
                if (array.getResourceId(x, 0) == elementId)
                    return x;
            }
        } finally {
            if (null != array) {
                array.recycle();
                array = null;
            }
        }
        throw new NoSuchElementException();
    }

    static int getResourceIdForPositionInArray(final Context context, final int arrayId, final int position) {

        TypedArray stateArray = null;
        try {
            stateArray = context.getResources().obtainTypedArray(arrayId);
            final int selectedResourceId = stateArray.getResourceId(position, 0);
            if (0 == selectedResourceId)
                throw new IndexOutOfBoundsException();
            return selectedResourceId;
        } finally {
            if (null != stateArray) {
                stateArray.recycle();
                stateArray = null;
            }
        }
    }
}
