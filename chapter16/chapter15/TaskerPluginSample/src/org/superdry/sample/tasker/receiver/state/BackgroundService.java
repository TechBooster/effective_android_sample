package org.superdry.sample.tasker.receiver.state;

import org.superdry.sample.tasker.activity.EditStateActivity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.PowerManager;

public final class BackgroundService extends Service {
	protected static final Intent INTENT_REQUEST_REQUERY = new Intent(
			com.twofortyfouram.locale.Intent.ACTION_REQUEST_QUERY).putExtra(
			com.twofortyfouram.locale.Intent.EXTRA_ACTIVITY,
			EditStateActivity.class.getName());

	static final String EXTRA_BOOLEAN_WAS_SCREEN_ON = BackgroundService.class
			.getName() + ".extra.BOOLEAN_WAS_SCREEN_ON";

	private BroadcastReceiver mReceiver;
	private boolean mIsOnStartCommandCalled = false;

	@Override
	public void onCreate() {
		super.onCreate();
		mReceiver = new DisplayReceiver();
		final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(mReceiver, filter);
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags,
			final int startId) {
		super.onStartCommand(intent, flags, startId);

		if (null != intent) {
			if (!mIsOnStartCommandCalled) {
				if ((((PowerManager) getSystemService(Context.POWER_SERVICE))
						.isScreenOn()) != intent.getBooleanExtra(
						EXTRA_BOOLEAN_WAS_SCREEN_ON, false))
					sendBroadcast(INTENT_REQUEST_REQUERY);
			}
			ServiceWakeLockManager.releaseLock();
		}
		mIsOnStartCommandCalled = true;
		return START_STICKY;
	}

	@Override
	public IBinder onBind(final Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		mReceiver = null;
	}

	/**
	 * ディスプレイ状態を受け取るReceiver
	 */
	private static final class DisplayReceiver extends BroadcastReceiver {
		public DisplayReceiver() {
			super();
		}

		@Override
		public void onReceive(final Context context, final Intent intent) {
			if (isInitialStickyBroadcast())
				return;
			context.sendBroadcast(INTENT_REQUEST_REQUERY);
		}
	}
}
