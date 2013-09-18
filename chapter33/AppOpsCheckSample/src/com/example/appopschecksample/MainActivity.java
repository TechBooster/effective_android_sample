package com.example.appopschecksample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.appopscheck.AppOpsCheck;

/**
 * 電話帳にある名前と電話番号を表示するサンプルアプリ。
 * 
 * AppOps状態を確認し、無効化されている場合はダイアログを表示して設定を
 * 変更してもらうように誘導します。
 *
 */
public class MainActivity extends FragmentActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {
    // 電話帳から検索する項目
    private static final String[] CONTACT_PROJECTION = {
        ContactsContract.CommonDataKinds.Phone._ID,
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
    };

    // 表示する項目 (表示名と電話番号)
    private static final String[] ADAPTER_FROM = {
        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
        ContactsContract.CommonDataKinds.Phone.NUMBER,
    };

    // 表示項目を表示する部品のID
    private static final int[] ADAPTER_TO = {
            android.R.id.text1,
            android.R.id.text2,
    };

    // CursorLoaderで使用するアダプタ
    private SimpleCursorAdapter mAdapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // アダプタを生成する
        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                null,
                ADAPTER_FROM,
                ADAPTER_TO,
                0);

        // ListViewにアダプタを設定する
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(mAdapter);

        // LoaderManagerを初期化する
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // AppOps設定変更画面から戻った時に最新の設定状態を反映するため、
        // リセットする。
        getSupportLoaderManager().restartLoader(0, null, this);

        // AppOpsCheckを用いて、
        // 電話帳読み出し(OP_READ_CONTACTS)のAppOps設定を取得する
        AppOpsCheck appOpsCheck = new AppOpsCheck(this);
        int appOpsMode = appOpsCheck.checkOpNoThrow(AppOpsCheck.OP_READ_CONTACTS,
                this.getApplicationContext().getPackageName());

        // AppOps設定が許可状態でなければ、エラーダイアログ表示
        if (appOpsMode != AppOpsCheck.MODE_ALLOWED) {
            showErrorDialog(appOpsMode);
        }
    }

    // メニュー設定
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // OSバージョンが4.2以下ならメニューを無効化
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            menu.findItem(R.id.action_settings).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    // メニューから設定が選択されたら、AppOps設定画面を呼び出す。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings",
                "com.android.settings.Settings$AppOpsSummaryActivity");
        startActivity(intent);

        return true;
    }

    // 電話帳表示のためのCursorLoaderのための処理群
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                CONTACT_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    // エラーダイアログ表示用ユーティリティメソッド
    private void showErrorDialog(int mode) {
        ErrorDialogFragment fragment = ErrorDialogFragment.newInstance(mode);
        
        if (fragment != null) {
            fragment.show(getSupportFragmentManager(), "error dialog");
        }
    }
    

    // エラーダイアログを表示するフラグメント
    public static class ErrorDialogFragment extends DialogFragment {
        private static final String APP_OPS_MODE = "APP_OPS_MODE";

        public static ErrorDialogFragment newInstance(int mode) {
            ErrorDialogFragment fragment = new ErrorDialogFragment();

            // 画面回転などでのフラグメント再生成を考慮して、必要な引数はsetArguments()で保持する
            Bundle args = fragment.getArguments();
            if (args == null) {
                args = new Bundle();
            }
            
            args.putInt(APP_OPS_MODE, mode);

            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // getArguments()でエラーダイアログ生成に必要な引数を修得する
            int mode = getArguments().getInt(APP_OPS_MODE);

            Dialog dialog;
            if (mode == AppOpsCheck.MODE_IGNORED) {
                // AppOps状態が無効なら、ダイアログにその旨を表示する。
                // また、ダイアログに設定画面を呼び出すボタンも表示する。
                dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error_dialog_title)
                    .setMessage(R.string.appops_ignored)
                    .setPositiveButton(R.string.appops_invoke, new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent();
                            intent.setClassName("com.android.settings",
                                    "com.android.settings.Settings$AppOpsSummaryActivity");
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.cancel,null)
                    .create();
            } else {
                // AppOps状態がエラーになった場合は、アプリを終了する。
                dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error_dialog_title)
                    .setMessage(R.string.appops_errored)
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton(R.string.cancel,null)
                    .create();
            }

            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }
}
