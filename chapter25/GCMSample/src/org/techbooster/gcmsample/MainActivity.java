/*
 * Copyright TechBooster/mhidaka, kei_i_t
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.techbooster.gcmsample;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.techbooster.gcmsample.CommonUtilities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{

	private static final String TAG = "TechBoosterSample";

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    GoogleCloudMessaging gcm;
    String regid;
    Context context;
    
    /**
     * サーバー通信用AsyncTask
     */
    AsyncTask<Void, Void, Void> mRegisterTask;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btnRegist = (Button) findViewById(R.id.btn_regist);
        btnRegist.setOnClickListener(this);
        Button btnUnregist = (Button) findViewById(R.id.btn_unregist);
        btnUnregist.setOnClickListener(this);
        
        context = getApplicationContext();
        
        // デバイスにPlayサービスAPKが入っているか検証する
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);
        } else {
            Log.i(TAG, "Google Play Services APKが見つかりません");
        }
    }
    
    /*
     * PlayサービスのAPKチェック
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "Playサービスがサポートされていない端末です");
                finish();
            }
            return false;
        }
        return true;
    }
    
    /*
     * レジストレーションIDの取得
     */   
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        // プリファレンスに格納されていない場合は空で返却
        if (regid.equals("")) {
            Log.i(TAG, "レジストレーションIDが見つかりません");
            return "";
        }
        // アプリケーションがバージョンアップされていた場合、レジストレーションIDを必ずクリアしないといけません
        // すでにレジストレーションIDが存在していた場合、再生成は行いません。
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "アプリケーションバージョンが変更されています");
            return "";
        }
        return registrationId;
    }
    
    /*
     * アプリケーションバージョン情報を取得する
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("パッケージ名が見つかりません : " + e);
        }
    }
    
    /*
     * SharedPreferencesを取得
     */
    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    
    /*
     * レジストレーションIDの保存
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "レジストレーションIDを登録。登録時のアプリケーションバージョン: " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null) {
            mRegisterTask.cancel(true);
        }
        gcm.close();
        super.onDestroy();
    }
    
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_regist){
            if (regid.equals("")) {
                // GCM登録用AsyncTaskの実行
                mRegisterTask = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        if (gcm == null) {
                            // インスタンスがなければ取得する
                            gcm = GoogleCloudMessaging.getInstance(context);
                        }
                        try {
                            // GCMサーバーへ登録する
                            regid = gcm.register(CommonUtilities.SENDER_ID);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // レジストレーションIDを自分のサーバーへ送信する
                        // レジストレーションIDをつかえば、アプリケーションにGCMメッセージを送信できるようになります
                        Log.i(TAG,"送信対象のレジストレーションID: " + regid);
                        register(regid);
                        
                        // レジストレーションIDを端末に保存
                        storeRegistrationId(context, regid);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }
                };
                mRegisterTask.execute(null, null, null);
            }
        }else if(v.getId() == R.id.btn_unregist){
            //GCMサーバーから登録を解除するAsyncTaskの実行
            mRegisterTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    if (gcm == null) {
                        // インスタンスがなければ取得する
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    try {
                        // GCMサーバーの登録を解除する
                        gcm.unregister();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // レジストレーションIDを自分のサーバーでも削除する
                    unregister(regid);
                    
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    mRegisterTask = null;
                }

            };
            mRegisterTask.execute(null, null, null);
        }
    }

    /*
     * 
     */
    public static boolean register(String regId) {
        
        String serverUrl = CommonUtilities.SERVER_URL + "/register.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static void unregister(String regId) {
        
        String serverUrl = CommonUtilities.SERVER_URL + "/unregister.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        try {
            post(serverUrl, params);
        } catch (IOException e) {
        }
    }
    
    public static void post(String endpoint, Map<String, String> params)
            throws IOException {
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // POSTするパラメータ
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // ポスト送信
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // サーバーレスポンス受信
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }
}
