package com.example.c84databasesample;

import java.util.Random;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
    private static final String TAG = "Test";
    private static final String[] NAMES = { "たろー", "じろー", "ぽん吉", "かなこ", "花子",
            "ゆうた", "りょうた", "まえけん", "ほりけん", "きんたろう" };

    private Random mRand = new Random(System.currentTimeMillis());
    private ListView mListView;
    private ListAdapter mAdapter;
    private MyDatabaseHelper mHelper;
    private SQLiteDatabase mDb;
    private Cursor mCursor;
    private boolean isStop;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView1);
        findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadList();
            }
        });
        findViewById(R.id.button2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                createSampleData();
            }
        });

        mHelper = new MyDatabaseHelper(this);
        mDb = mHelper.getWritableDatabase();

        // 5秒に一回データ生成を行うスレッドをスタート
        startThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isStop = true;
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
        mDb.close();
        mDb = null;
        mHelper.close();
        mHelper = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void loadList() {
        Log.d(TAG, "loadList start");
        if (mDb != null) {
            if (mCursor != null) {
                mCursor.close();
                mCursor = null;
            }
            mCursor = mDb.query("USERS", null, null, null, null, null, null);
            mAdapter = new MyCursorAdapter(this, mCursor);
            mListView.setAdapter(mAdapter);
            Log.d(TAG, "loadList end");
            Log.d(TAG,
                    "isWriteAheadLoggingEnabled="
                            + mDb.isWriteAheadLoggingEnabled());
        }
    }

    private void startThread() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                MyDatabaseHelper helper = new MyDatabaseHelper(
                        MainActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();
                try {
                    // WALを有効にする
                    // db.enableWriteAheadLogging();
                    Log.d(TAG, "WAL :" + db.isWriteAheadLoggingEnabled());
                    db.beginTransaction();
                    int count = 0;
                    do {
                        // 生成処理
                        insertProc(db);

                        // ５回に一回更新処理をしてみる
                        count++;
                        if (count % 5 == 0) {
                            db.setTransactionSuccessful();
                            db.endTransaction();
                            db.beginTransaction();
                        }
                    } while (isStop);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                    db.close();
                    helper.close();
                }
            }
        });
        t.start();
    }

    private void insertProc(SQLiteDatabase db) {
        String name = NAMES[mRand.nextInt(NAMES.length)];
        int age = mRand.nextInt(80);
        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("AGE", age);

        db.insert("USERS", null, values);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }

        Log.d(TAG, "inserted:" + name);

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                loadList();
            }
        });
    }

    public void createSampleData() {
        MyDatabaseHelper helper = new MyDatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        long start = 0;
        try {
            // WALを有効にする
            db.enableWriteAheadLogging();
            Log.d(TAG, "WAL :" + db.isWriteAheadLoggingEnabled());
            db.beginTransaction();
            start = System.nanoTime();
            for (int i = 0; i < 10000; i++) {
                String name = NAMES[mRand.nextInt(NAMES.length)];
                int age = mRand.nextInt(80);
                ContentValues values = new ContentValues();
                values.put("NAME", name);
                values.put("AGE", age);
                db.insert("USERS", null, values);
            }
            start = printTimeLog("Insert処理", start, System.nanoTime());
            db.setTransactionSuccessful();
            start = printTimeLog("setTransactionSuccessful", start,
                    System.nanoTime());
        } finally {
            db.endTransaction();
            start = printTimeLog("endTransaction", start, System.nanoTime());
            db.close();
            helper.close();
            printTimeLog("Close処理", start, System.nanoTime());
        }
    }

    private long printTimeLog(String text, long begin, long now) {
        long dt = now - begin;
        Log.d(TAG, "経過時間(" + text + "):" + dt / 1000);
        return now;
    }

}
