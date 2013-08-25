package org.zakky.myproject;

import android.app.Activity;
import android.os.Bundle;

import com.googlecode.androidannotations.annotations.EActivity;

@EActivity
public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
