package org.techbooster.uiautomationsample.test;

import java.util.List;

import org.techbooster.uiautomationsample.MainActivity;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.UiAutomation;
import android.app.UiAutomation.OnAccessibilityEventListener;
import android.content.Intent;
import android.provider.Settings;
import android.test.InstrumentationTestCase;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

public class MainActivityTest extends InstrumentationTestCase {

  private static final String TARGET_PKG = "org.techbooster.uiautomationsample";
  private static final String SETTINGS_PKG = "com.android.settings";

  private boolean mMainLaunched;

  public void testAirplaneModeToOn() {
    UiAutomation uiAutomation = getInstrumentation().getUiAutomation();
    // Activityの起動を監視するリスナーをセット
    mMainLaunched = false;
    uiAutomation
        .setOnAccessibilityEventListener(new OnAccessibilityEventListener() {
          @Override
          public void onAccessibilityEvent(AccessibilityEvent event) {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
              // ウィンドウのコンテンツが変わった
              if (TARGET_PKG.equals(event.getPackageName())) {
                // MainActivityが起動した
                mMainLaunched = true;
              }
            }
          }
        });

    // MainActivity起動
    Activity target = launchActivity(TARGET_PKG, MainActivity.class, null);
    try {
      // MainActivity起動待ち
      do {
        Thread.sleep(1000);
      } while (!mMainLaunched);

      // 機内モードをOnにする
      // Settingsの起動
      Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      getInstrumentation().getContext().startActivity(intent);

      // Settingsの起動待ち
      AccessibilityNodeInfo root;
      while (true) {
        root = uiAutomation.getRootInActiveWindow();
        if (root != null && SETTINGS_PKG.equals(root.getPackageName())) {
          break;
        } else {
          Thread.sleep(1000);
        }
      }

      // ボタンを押す
      List<AccessibilityNodeInfo> list = root
          .findAccessibilityNodeInfosByViewId("android:id/list");
      AccessibilityNodeInfo listViewInfo = list.get(0);
      AccessibilityNodeInfo airplaneModeView = listViewInfo.getChild(0);
      List<AccessibilityNodeInfo> checkList = airplaneModeView
          .findAccessibilityNodeInfosByViewId("android:id/checkbox");
      AccessibilityNodeInfo airplaneModeCheck = checkList.get(0);
      if (!airplaneModeCheck.isChecked()) {
        airplaneModeView.performAction(AccessibilityNodeInfo.ACTION_CLICK);
      }

      // Backキーを押してSettingsの終了
      uiAutomation.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

      // 機内モード反映待ち
      Thread.sleep(10000);

      // TextViewの文字列検証
      String expected = target
          .getString(org.techbooster.uiautomationsample.R.string.airplane_mode_off);
      TextView textView = (TextView) target
          .findViewById(org.techbooster.uiautomationsample.R.id.text_view);
      assertEquals(expected, textView.getText().toString());

    } catch (Exception e) {
      fail(e.getMessage());
      e.printStackTrace();
    } finally {
      if (target != null) {
        target.finish();
      }
    }
  }
}
