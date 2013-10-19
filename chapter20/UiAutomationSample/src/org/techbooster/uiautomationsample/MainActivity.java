package org.techbooster.uiautomationsample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

public class MainActivity extends Activity {

  private TextView mTextView;
  private BroadcastReceiver mReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mTextView = (TextView) findViewById(R.id.text_view);

    // アプリ起動時の機内モードの状態をTextViewに表示
    if (isAirplaneMode()) {
      mTextView.setText(R.string.airplane_mode_on);
    } else {
      mTextView.setText(R.string.airplane_mode_off);
    }

    // 通信状態の変更を取得するためのBroadcastReceiverを作成し、登録
    mReceiver = new BroadcastReceiver() {

      @Override
      public void onReceive(Context context, Intent intent) {
        // 通信状態の変更通知を受けて、TextViewの内容を更新
        if (isAirplaneMode()) {
          mTextView.setText(R.string.airplane_mode_on);
        } else {
          mTextView.setText(R.string.airplane_mode_off);
        }
      }
    };
    IntentFilter filter = new IntentFilter(
        ConnectivityManager.CONNECTIVITY_ACTION);
    registerReceiver(mReceiver, filter);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mReceiver);
  }

  /**
   * 機内モードがOnならtrueを、Offならfalseを返す
   * 
   * @return
   */
  private boolean isAirplaneMode() {
    return Settings.System.getInt(getContentResolver(),
        Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
  }
}
