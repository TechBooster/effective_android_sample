package net.vvakame.fragmentcollection.usage;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import net.vvakame.fragmentcollection.AndroidBeamFragment;

public class MainActivity extends Activity implements AndroidBeamFragment.BeamActionCallbackPicker {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        { // ShortMessagingFragmentの作成と登録
            FragmentTransaction tx = getFragmentManager().beginTransaction();
            AndroidBeamFragment androidBeamFragment = new AndroidBeamFragment();
            tx.add(androidBeamFragment, "beamFragment");
            tx.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public AndroidBeamFragment.BeamActionCallback getBeamActionCallback() {
        return new BeamActionCallbackImpl();
    }

    class BeamActionCallbackImpl implements AndroidBeamFragment.BeamActionCallback {

        @Override
        public void onNfcNotSupported() {

        }

        @Override
        public void onNfcDisabled() {

        }

        @Override
        public void onBeamReceived(byte[] msg) {

        }

        @Override
        public byte[] onBeamSendPreprocess() {
            return new byte[0];
        }

        @Override
        public void onBeamSendComplete() {

        }
    }
}
