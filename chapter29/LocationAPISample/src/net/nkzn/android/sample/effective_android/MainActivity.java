package net.nkzn.android.sample.effective_android;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	/** Google Play Servicesの問題を解決する画面を開くためのrequestCode */
	protected final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	/** 位置情報リクエストを制御する */
	private LocationClient mLocationClient;

	/** 位置情報リクエストの内容を定義する */
	private LocationRequest mLocationRequest;

	/** 位置情報をリクエスト済みかどうか(onPause->onResume対策) */
	boolean mLocationUpdatesRequested = false;

	final Spot SPOT_SKYTREE = new Spot("スカイツリー", 35.710213, 139.812741);
	final Spot SPOT_GORYOKAKU = new Spot("五稜郭", 41.796912, 140.757087);
	final Spot SPOT_SAKURAJIMA = new Spot("桜島", 31.583581, 130.650602);

	Spot selectedSpot = SPOT_SKYTREE; // 初期値：スカイツリー

	Spinner spSpot;
	TextView tvDistance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// スポット用のアダプタを作成
		ArrayAdapter<Spot> adapter = new ArrayAdapter<Spot>(this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add(SPOT_SKYTREE);
		adapter.add(SPOT_GORYOKAKU);
		adapter.add(SPOT_SAKURAJIMA);

		// スピナーを初期化
		Spinner spSpot = (Spinner) findViewById(R.id.sp_spot);
		spSpot.setAdapter(adapter);
		spSpot.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.d("tag", "onItemSelected");
				selectedSpot = (Spot) parent.getItemAtPosition(position);

				if (mLocationClient != null && mLocationClient.isConnected()) {
					Location lastLocation = mLocationClient.getLastLocation();
					if (lastLocation != null) {
						updateDistanceText(lastLocation);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		// 距離表示用TextViewを初期化
		tvDistance = (TextView) findViewById(R.id.tv_distance);
		updateDistanceText(999);

		// LocationRequestを初期化
		{
			mLocationRequest = LocationRequest.create();
			mLocationRequest.setInterval(5000);
			mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			mLocationRequest.setFastestInterval(1000);
		}

		// LocationClientを初期化
		mLocationClient = new LocationClient(this, this, this);

		startLocationUpdates();
	}

	/**
	 * 現在地と目的地の間の距離を計算
	 * 
	 * @param spot 目的地
	 * @param location 現在地
	 * @return ２点間の距離をメートルで返します
	 */
	private float calcDistance(Spot spot, Location location) {
		float[] results = new float[3];
		Location.distanceBetween(spot.getLatitude(), spot.getLongitude(), location.getLatitude(), location.getLongitude(), results);
		return results[0];
	}

	/**
	 * 現在地を指定して、選択中の目的地との距離を画面に表示する
	 * @param location 現在地
	 */
	private void updateDistanceText(Location location) {
		float calcDistance = calcDistance(selectedSpot, location);
		updateDistanceText((int) (calcDistance / 1000)); // kmに変換して画面を更新
	}

	private void updateDistanceText(int kilometers) {
		tvDistance.setText(String.valueOf(kilometers));
	}

	/**
	 * 場所
	 */
	class Spot {
		String name;
		double latitude;
		double longitude;

		public Spot(String name, double latitude, double longitude) {
			this.name = name;
			this.latitude = latitude;
			this.longitude = longitude;
		}

		public String getName() {
			return name;
		}

		public double getLatitude() {
			return latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		@Override
		public String toString() {
			// Spinnerの表示のために必要
			return name == null ? "" : name;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");

		mLocationClient.connect();
		Log.d(TAG, "\tLocation Client requests connection.");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");

		if (mLocationUpdatesRequested && mLocationClient.isConnected()) {
			startLocationUpdates();
		}
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		if (mLocationClient.isConnected()) {
			stopLocationUpdates();
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.d(TAG, "onStop");

		mLocationClient.disconnect();
		Log.d(TAG, "\tLocation Client requests disconnection.");

		super.onStop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		switch (requestCode) {
		case CONNECTION_FAILURE_RESOLUTION_REQUEST:
			switch (resultCode) {
			case RESULT_OK:
				if (servicesConnected()) {
					startLocationUpdates();
					mLocationUpdatesRequested = true;
				}
				break;
			default:
				// TODO Google Play Servicesに関する問題を解決できなかった場合の処理を書く
				break;
			}
			break;
		default:
			Log.w(TAG, "不明なrequestCode");
			break;
		}

	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(TAG, "onLocationChanged(" + location.getLatitude() + "," + location.getLongitude() + ")");

		updateDistanceText(location);
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "onConnected");
		startLocationUpdates(); // 位置情報取得を開始
		mLocationUpdatesRequested = true;

		if (mLocationClient != null) {
			Location lastLocation = mLocationClient.getLastLocation();
			if (lastLocation != null) {
				updateDistanceText(lastLocation);
			}
		}
	}

	@Override
	public void onDisconnected() {
		Log.d(TAG, "onDisconnected");
		stopLocationUpdates();
		mLocationUpdatesRequested = false;
	}

	void startLocationUpdates() {
		if (mLocationClient.isConnected()) {
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
		Log.d(TAG, "Location Client requests update.");
	}

	void stopLocationUpdates() {
		mLocationClient.removeLocationUpdates(this);
		Log.d(TAG, "Location Client removes update.");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d(TAG, "onConnectionFailed");

		/*
		 * Google Play servicesに接続できなかった場合でも、解決策が提示される場合があります。 インテントを投げるとよしなに解決してくれます。
		 */
		if (connectionResult.hasResolution()) {
			try {

				// エラーを解決してくれるインテントを投げます
				connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

				/*
				 * 失敗することもあります
				 */
			} catch (IntentSender.SendIntentException e) {

				// Log the error
				e.printStackTrace();
			}
		} else {

			// 解決策がない場合はエラーダイアログを出します
			showErrorDialog(connectionResult.getErrorCode(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
		}

	}

	protected boolean servicesConnected() {

		// Google Play Servicesが利用可能かどうかチェック
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

		if (ConnectionResult.SUCCESS == resultCode) {
			// Google Play Servicesが利用可能な場合
			return true;
		} else {
			// Google Play Servicesが何らかの理由で利用できない場合

			// 解決策が書いてあるダイアログが貰えるので、DialogFragmentで表示する
			showErrorDialog(resultCode, 0);
			return false;
		}

	}

	/**
	 * エラーダイアログを表示します。
	 * 
	 * @param errorCode
	 *            {@link GooglePlayServicesUtil#isGooglePlayServicesAvailable(android.content.Context)}でもらえたコード
	 * @param requestCode
	 *            特に使わない場合は0にします
	 */
	protected void showErrorDialog(int errorCode, int requestCode) {
		// Google Play servicesからエラーダイアログを受け取る
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, requestCode);

		if (errorDialog != null) {
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();
			errorFragment.setDialog(errorDialog);
			errorFragment.show(getSupportFragmentManager(), ErrorDialogFragment.TAG);
		}
	}

	/**
	 * Dialogを外からセットできるエラー表示用DialogFragment
	 */
	public static class ErrorDialogFragment extends DialogFragment {

		public static final String TAG = ErrorDialogFragment.class.getSimpleName();

		private Dialog mDialog;

		/*
		 * コンストラクタ。Dialogインスタンスの初期化だけを行う。
		 */
		public ErrorDialogFragment() {
			super();
			mDialog = null;
		}

		/**
		 * Dialogをセット
		 * 
		 * @param dialog
		 *            {@link GooglePlayServicesUtil#getErrorDialog(int, android.app.Activity, int)}でもらったDialog
		 */
		public void setDialog(Dialog dialog) {
			mDialog = dialog;
		}

		/*
		 * セット済みのDialogをそのまま使ってダイアログを初期化
		 */
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			return mDialog;
		}
	}
}
