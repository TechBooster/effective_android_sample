package net.vvakame.fragmentcollection;

import java.nio.charset.Charset;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import net.vvakame.fragmentcollection.androidbeam.R;

/**
 * Android Beamを簡単に利用するためのFragment.
 *
 * @author vvakame
 */
public class AndroidBeamFragment extends Fragment implements
        CreateNdefMessageCallback, OnNdefPushCompleteCallback {

    static final String TAG = AndroidBeamFragment.class.getSimpleName();

    /**
     * {@link AndroidBeamFragment.BeamActionCallback} を取得するためのPicker.
     *
     * @author vvakame
     */
    public static interface BeamActionCallbackPicker {
        public BeamActionCallback getBeamActionCallback();
    }

    /**
     * Beamに関する何かしらのイベントが発生した場合のコールバック.
     *
     * @author vvakame
     */
    public static interface BeamActionCallback {
        /**
         * NFC がサポートされていない時のコールバック.
         */
        public void onNfcNotSupported();

        /**
         * NFC が有効になっていない時のコールバック.
         */
        public void onNfcDisabled();

        /**
         * Beamでメッセージを受け取った時のコールバック.
         */
        public void onBeamReceived(byte[] msg);

        /**
         * Beamで送るメッセージを生成する.
         */
        public byte[] onBeamSendPreprocess();

        /**
         * Beamでメッセージ送信を行った時のコールバック.
         */
        public void onBeamSendComplete();
    }

    NfcAdapter mNfcAdapter;

    BeamActionCallback mCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Context context = getActivity();
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);

            // Permissionのチェック
            if (packageInfo.requestedPermissions == null) {
                throw new IllegalStateException(
                        "Android Beam required 1 permission and 1 feature\n" +
                                "<uses-permission android:name=\"android.permission.NFC\" />\n" +
                                "<uses-feature android:name=\"android.hardware.nfc\" />");
            }
            boolean existsNFCPermission = false;
            for (String permission : packageInfo.requestedPermissions) {
                if ("android.permission.NFC".equals(permission)) {
                    existsNFCPermission = true;
                }
            }
            if (!existsNFCPermission) {
                throw new IllegalStateException(
                        "Android Beam required 1 permission\n" +
                                "<uses-permission android:name=\"android.permission.NFC\" />");
            }

            // intent filterのチェック
            Intent intent = new Intent("android.nfc.action.NDEF_DISCOVERED");
            intent.setPackage(context.getPackageName());
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setType("application/" + context.getPackageName());
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent, 0);
            boolean exsitsThisActivity = false;
            for (ResolveInfo info : resolveInfos) {
                if (getActivity().getClass().getCanonicalName().equals(info.activityInfo.name)) {
                    exsitsThisActivity = true;
                }
            }
            if (!exsitsThisActivity) {
                throw new IllegalStateException(
                        "activity required has a intent-filter\n" +
                                "<intent-filter>\n" +
                                "<action android:name=\"android.nfc.action.NDEF_DISCOVERED\" />\n" +
                                "<category android:name=\"android.intent.category.DEFAULT\" />\n" +
                                "<data android:mimeType=\"application/" + context.getPackageName() + "\" />" +
                                "</intent-filter>");
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalStateException("package name " + context.getPackageName() + " not exsists", e);
        }

        // コールバックの取得
        if (activity instanceof BeamActionCallback) {
            mCallback = (BeamActionCallback) activity;
        } else if (activity instanceof BeamActionCallbackPicker) {
            BeamActionCallbackPicker picker = (BeamActionCallbackPicker) activity;
            mCallback = picker.getBeamActionCallback();
        } else {
            throw new IllegalStateException("acitivity must implemented BeamActionCallback or BeamActionCallbackPicker");
        }

        // NFC有無のチェック
        mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (mNfcAdapter == null) {
            mCallback.onNfcNotSupported();
            return;
        } else if (mNfcAdapter.isEnabled() == false) {
            mCallback.onNfcDisabled();
        }

        // コールバックの設定
        mNfcAdapter.setNdefPushMessageCallback(this, activity);
        mNfcAdapter.setOnNdefPushCompleteCallback(this, activity);
    }

    /**
     * NdefMessageが要求された時 (=Beamで送るメッセージを要求された時) の動作.
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String packageName = getActivity().getPackageName();
        byte[] msgBytes = mCallback.onBeamSendPreprocess();
        NdefRecord msg = createMimeRecord("application/" + packageName,
                msgBytes);
        NdefRecord aar = NdefRecord.createApplicationRecord(packageName);

        // AARは必ず後
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{msg, aar});

        return ndefMessage;
    }

    /**
     * メッセージを作成する. 可能な限りmimeTypeがアプリケーション固有になるように留意する.
     *
     * @param mimeType 本文のmimeType
     * @param payload  本文
     * @return 作成した NdefRecord
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("UTF-8"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                mimeBytes, new byte[0], payload);
        return mimeRecord;
    }

    /**
     * Beam成功時のコールバック. そのまま {@link AndroidBeamFragment.BeamActionCallback} に渡す.<br>
     * onNdefPushComplete はUIスレッド以外で実行されるのでUIスレッドで実行するようにしてやる.
     */
    @Override
    public void onNdefPushComplete(NfcEvent event) {
        new Handler(Looper.getMainLooper()) {
            @Override
            public void dispatchMessage(Message msg) {
                mCallback.onBeamSendComplete();
            }

        }.sendEmptyMessage(0);
    }

    int mIntentHashCode = 0;

    /**
     * NDEF通知のIntentで、かつ処理済でなければ処理する.
     */
    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getActivity().getIntent();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())
                && mIntentHashCode != intent.hashCode()) {
            processIntent(intent);
            mIntentHashCode = intent.hashCode();
        }
    }

    /**
     * NdefMessageが含まれるIntentの処理を行う.
     * アプリ内で複数の手段のためにNFCを利用する場合、もう少し細かく内容のチェックをしたほうがよい.
     */
    void processIntent(Intent intent) {
        Parcelable[] rawMsgs = intent
                .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        mCallback.onBeamReceived(msg.getRecords()[0].getPayload());
    }

    /**
     * メニューを生成する. NFCが未サポートの場合、メニューを表示しない.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mNfcAdapter == null) {
            return;
        }
        inflater.inflate(R.menu.android_beam, menu);
    }

    /**
     * メニューが選択された場合の動作.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.beam_settings) {
            Intent intent;
            if (Build.VERSION.SDK_INT < 15) {
                intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
            } else {
                intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
            }
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
