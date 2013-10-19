package jp.muo.obbsample;

import android.os.Bundle;
import android.app.Activity;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.Menu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends Activity {
    static final String TAG="ObbSample";
    static final String PATH_PLAIN_OBB = "/sdcard/obb_files/test_plain.obb";
    static final String PATH_ENCRYPTED_OBB = "/sdcard/obb_files/test_encrypted.obb";
    static final String KEY_ENCRYPTED_OBB = "testpassword";
    static final String SECRET_FILE_RELATIVE_PATH = "secret.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final StorageManager storageManager = (StorageManager)getSystemService(STORAGE_SERVICE);
        storageManager.mountObb(PATH_PLAIN_OBB, null, new OnObbStateChangeListener() {
            @Override
            public void onObbStateChange(String path, int state) {
                super.onObbStateChange(path, state);
                Log.d(TAG, "state of " + path + " changed to " + state);
                boolean isMounted = storageManager.isObbMounted(PATH_PLAIN_OBB);
                Log.d(TAG, "isMounted: " + (isMounted ? "true" : "false"));
                if (isMounted) {
                    String obbMountedPath = storageManager.getMountedObbPath(PATH_PLAIN_OBB);
                    Log.d(TAG, "(encrypted)mounted path: " + obbMountedPath);
                    String fileContentString = readSecretFile(obbMountedPath);
                    if (fileContentString != null) {
                        Log.d(TAG, "(encrypted)fileContent: " + fileContentString);
                    }
                }
            }
        });
        storageManager.mountObb(PATH_ENCRYPTED_OBB, null, new OnObbStateChangeListener() {
            @Override
            public void onObbStateChange(String path, int state) {
                super.onObbStateChange(path, state);
                if (storageManager.isObbMounted(PATH_ENCRYPTED_OBB)) {
                    Log.d(TAG, "This code will never be called.");
                }
            }
        });
        storageManager.mountObb(PATH_ENCRYPTED_OBB, KEY_ENCRYPTED_OBB, new OnObbStateChangeListener() {
            @Override
            public void onObbStateChange(String path, int state) {
                super.onObbStateChange(path, state);
                Log.d(TAG, "state of " + path + " changed to " + state);
                boolean isMounted = storageManager.isObbMounted(PATH_PLAIN_OBB);
                Log.d(TAG, "isMounted: " + (isMounted ? "true" : "false"));
                if (isMounted) {
                    String obbMountedPath = storageManager.getMountedObbPath(PATH_PLAIN_OBB);
                    Log.d(TAG, "(encrypted)mounted path: " + obbMountedPath);
                    String fileContentString = readSecretFile(obbMountedPath);
                    if (fileContentString != null) {
                        Log.d(TAG, "(encrypted)fileContent: " + fileContentString);
                    }
                }
            }
        });
    }

    private String readSecretFile(String obbMountedPath) {
        File file = new File(obbMountedPath, SECRET_FILE_RELATIVE_PATH);
        StringBuilder fileContentString = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                fileContentString.append(line);
                fileContentString.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            return null;
        }
        return fileContentString.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
