
package com.example.parsetest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Parseを初期化（キーは変えてください）
        Parse.initialize(this, "x9IstpeOC5Ly7BKiJloLo4B4BohvzF2bkJryoZbL", "UD6kKn6YKxHPoi7QStg1i3qFFEzFUieSgSNPxTKm");
        // プッシュ通知から起動したいActivityを指定
        PushService.setDefaultPushCallback(this, MainActivity.class);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        // プッシュ通知からの起動率を見たい場合は以下も追加
        ParseAnalytics.trackAppOpened(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
