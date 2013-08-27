package jp.mydns.sys1yagi.android.complexpreferencessample;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import jp.mydns.sys1yagi.android.complexpreferencessample.model.User;
import jp.mydns.sys1yagi.android.complexpreferencessample.model.User.AccessToken;
import jp.mydns.sys1yagi.android.complexpreferencessample.model.User2;

import br.com.kots.mob.complex.preferences.ComplexPreferences;

import com.google.gson.Gson;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;

public class ComplexPreferencesSampleActivity extends Activity {
    private final static String TAG = ComplexPreferencesSampleActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complex_preferences_sample);
        
        //ComplexPreferencesと初期化。内部でSharedPreferenceを作っている
        ComplexPreferences complexPrefenreces = 
                ComplexPreferences.getComplexPreferences(
                        getBaseContext(),
                        "preference",
                        MODE_PRIVATE
                );
        AccessToken token = new AccessToken();
        token.setAccessToken("access_token");
        token.setAccessTokenSecret("AeogKE9230KEcs0SaAj");
        User user = new User();
        user.setId("sys1yagi");
        user.setName("八木");
        user.setProfile("つらぽよ");
        user.setToken(token);
        
        //オブジェクトをセットして保存
        complexPrefenreces.putObject("user", user);
        complexPrefenreces.commit();
        
        //読み込み
        User loadedUser = complexPrefenreces.getObject("user", User.class);
        Log.d(TAG, loadedUser.toString());
        
        //パーシャルなクラスで読み込み
        User2 user2 = complexPrefenreces.getObject("user", User2.class);
        Log.d(TAG, "user2:"+user2.toString());
        
        //JSON文字列で読み込み
        String value = complexPrefenreces.getPreferences().getString("user", "empty");
        Log.d(TAG, "value:" + value);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.complex_preferences_sample, menu);
        return true;
    }

    public String toBase64(Object object) {
        // try-with-resourcesつかいてーー！！
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            
            byte[] bytes = baos.toByteArray();
            byte[] base64 = Base64.encode(bytes, Base64.NO_WRAP);
            return new String(base64);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
            }
        }
        return null;
    }
}
