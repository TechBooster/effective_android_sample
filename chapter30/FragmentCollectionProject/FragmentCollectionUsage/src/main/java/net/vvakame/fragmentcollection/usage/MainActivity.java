package net.vvakame.fragmentcollection.usage;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

import net.vvakame.fragmentcollection.AndroidBeamFragment;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class MainActivity extends Activity implements AndroidBeamFragment.BeamActionCallbackPicker {

    final MainActivity self = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        { // AndroidBeamFragmentの作成と登録
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            AndroidBeamFragment androidBeamFragment = new AndroidBeamFragment();
            tx.add(androidBeamFragment, "beamFragment");
            tx.commit();
        }
    }

    @Override
    public AndroidBeamFragment.BeamActionCallback getBeamActionCallback() {
        return new BeamActionCallbackImpl();
    }

    class BeamActionCallbackImpl implements AndroidBeamFragment.BeamActionCallback {

        @Override
        public void onNfcNotSupported() {
            Toast.makeText(self, "NFCをサポートしていない端末です", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNfcDisabled() {
            Toast.makeText(self, "NFCがOFFになっています。ONにしてください。", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeamReceived(byte[] msg) {
            String string = new String(msg);
            Toast.makeText(self, string, Toast.LENGTH_SHORT).show();
        }

        @Override
        public byte[] onBeamSendPreprocess() {
            String str = new Date().toString();
            try {
                return str.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                return "unexpected error occured".getBytes();
            }
        }

        @Override
        public void onBeamSendComplete() {
            Toast.makeText(self, "送信！", Toast.LENGTH_SHORT).show();
        }
    }
}
