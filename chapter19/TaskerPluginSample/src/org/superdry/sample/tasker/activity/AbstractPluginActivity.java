
package org.superdry.sample.tasker.activity;

import org.superdry.sample.tasker.R;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public abstract class AbstractPluginActivity extends Activity {
    private boolean mIsCancelled = false;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CharSequence callingApplicationLabel = null;
        CharSequence activityLabel = null;
        try {
            callingApplicationLabel = getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(getCallingPackage(), 0));
            activityLabel = getPackageManager().getActivityInfo(getComponentName(), 0).loadLabel(getPackageManager());
        } catch (final NameNotFoundException e) {
            e.printStackTrace();
        }
        if (null != callingApplicationLabel && null != activityLabel)
            setTitle(callingApplicationLabel + " > " + activityLabel);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.twofortyfouram_locale_help_save_dontsave, menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            getActionBar().setIcon(getPackageManager().getApplicationIcon(getCallingPackage()));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
        final int id = item.getItemId();
        if (android.R.id.home == id) {
            finish();
            return true;
        } else if (R.id.twofortyfouram_locale_menu_dontsave == id) {
            mIsCancelled = true;
            finish();
            return true;
        } else if (R.id.twofortyfouram_locale_menu_save == id) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean isCanceled() {
        return mIsCancelled;
    }
}
