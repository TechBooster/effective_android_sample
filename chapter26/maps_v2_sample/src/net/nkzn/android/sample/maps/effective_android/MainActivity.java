package net.nkzn.android.sample.maps.effective_android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

public class MainActivity extends FragmentActivity {

    /**
     * マップ本体
     */
    GoogleMap mGoogleMap;

    /**
     * ピンをタップした時の挙動
     */
    private OnMarkerClickListener mOnMarkerClickListener = new OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            marker.showInfoWindow(); // ピンの上にバルーンを表示
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Googleマップ操作インスタンスを初期化
        if (mGoogleMap == null) {
            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            mGoogleMap = (fragment != null) ? fragment.getMap() : null;

            if (mGoogleMap != null) {
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // ラベル付き航空写真モード
                mGoogleMap.setOnMarkerClickListener(mOnMarkerClickListener); // ピンをタップした場合の挙動を定義

                // 初期位置はビッグサイト東１ホール
                final LatLng bigSight = new LatLng(35.630665, 139.797055);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(bigSight));
                mGoogleMap.moveCamera(CameraUpdateFactory.zoomTo(16));

                // ピンと区画を描画
                drawFields();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.action_lisence:

            final String message = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this);

            new AlertDialog.Builder(this).setTitle(R.string.action_lisence).setMessage(message)
                    .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * 複数の戦場を描画する
     */
    private void drawFields() {

        {
            Field field = new Field("東１(戦場)", new LatLng[] { new LatLng(35.630124, 139.796915), new LatLng(35.630778, 139.796384),
                    new LatLng(35.631218, 139.7972), new LatLng(35.630556, 139.797725), });
            field.setMemo("東方Project, ゲーム(電源不要), オンラインゲーム, ゲーム(その他)");
            field.setColorRgb(255, 0, 0);
            drawField(field);
        }

        {
            Field field = new Field("東２(戦場)", new LatLng[] { new LatLng(35.630556, 139.797725), new LatLng(35.631218, 139.79721),
                    new LatLng(35.63168, 139.798037), new LatLng(35.631018, 139.798578), });
            field.setMemo("東方Project, ゲーム(電源不要), オンラインゲーム, ゲーム(その他)");
            field.setColorRgb(0, 255, 0);
            drawField(field);
        }

        {
            Field field = new Field("東３(戦場)", new LatLng[] { new LatLng(35.631018, 139.798578), new LatLng(35.631676, 139.798047),
                    new LatLng(35.632121, 139.798873), new LatLng(35.631462, 139.799404), });
            field.setMemo("東方Project, ゲーム(電源不要), オンラインゲーム, ゲーム(その他)");
            field.setColorRgb(0, 0, 255);
            drawField(field);
        }

        {
            Field field = new Field("東４(戦場)", new LatLng[] { new LatLng(35.631384, 139.796497), new LatLng(35.632038, 139.795971),
                    new LatLng(35.632478, 139.796771), new LatLng(35.631816, 139.797307), });
            field.setMemo("ギャルゲー, Leaf & Key, TYPE-MOON, 同人ソフト, スクウェア・エニックス(RPG), ゲーム(RPG)");
            field.setColorRgb(255, 255, 0);
            drawField(field);
        }

        {
            Field field = new Field("東５(戦場)", new LatLng[] { new LatLng(35.631816, 139.797307), new LatLng(35.632483, 139.796808),
                    new LatLng(35.632884, 139.797602), new LatLng(35.632226, 139.798133), });
            field.setMemo("ギャルゲー, Leaf & Key, TYPE-MOON, 同人ソフト, スクウェア・エニックス(RPG), ゲーム(RPG)");
            field.setColorRgb(255, 0, 255);
            drawField(field);
        }

        {
            Field field = new Field("東６(戦場)", new LatLng[] { new LatLng(35.632226, 139.798133), new LatLng(35.632901, 139.797624),
                    new LatLng(35.633346, 139.798444), new LatLng(35.632683, 139.798981), });
            field.setMemo("ギャルゲー, Leaf & Key, TYPE-MOON, 同人ソフト, スクウェア・エニックス(RPG), ゲーム(RPG)");
            field.setColorRgb(0, 255, 255);
            drawField(field);
        }

        {
            Field field = new Field("西棟(戦場)", new LatLng[] { new LatLng(35.628044, 139.794882), new LatLng(35.628419, 139.794625),
                    new LatLng(35.628655, 139.794689), new LatLng(35.628995, 139.794399), new LatLng(35.629282, 139.794823),
                    new LatLng(35.629509, 139.794882), new LatLng(35.62974, 139.795295), new LatLng(35.628737, 139.796121), });
            field.setMemo("へ-26b TechBooster");
            field.setColorRgb(209, 167, 114);
            drawField(field);
        }

    }

    /**
     * 戦場１つを描画する
     */
    private void drawField(Field field) {
        // マーカー定義
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(field.getName()); // 名前を設定
        markerOptions.snippet(field.getMemo()); // 説明を設定
        markerOptions.position(calcCenter(field.getVertexes())); // マーカーの座標を設定（区画の中心を自動算出）
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(field.getColorHue())); // 色を設定

        // マップにマーカーを追加
        mGoogleMap.addMarker(markerOptions);

        // 区画を描画
        final LatLng[] vertexes = field.getVertexes();
        if (vertexes != null && vertexes.length > 3) {
            // ポリゴン定義
            PolygonOptions polygonOptions = new PolygonOptions();

            // RGBそれぞれの色を作成
            final int[] colorRgb = field.getColorRgb();
            int colorRed = colorRgb[0];
            int colorGreen = colorRgb[1];
            int colorBlue = colorRgb[2];

            // 区画の輪郭について設定
            polygonOptions.strokeColor(Color.argb(0x255, colorRed, colorGreen, colorBlue));
            polygonOptions.strokeWidth(5);

            // 区画の塗りつぶしについて設定
            polygonOptions.fillColor(Color.argb(0x40, colorRed, colorGreen, colorBlue));

            // 各頂点の座標を設定
            polygonOptions.add(vertexes); // LatLngでもLatLng[]でもOK

            // マップにポリゴンを追加
            mGoogleMap.addPolygon(polygonOptions);
        }
    }

    /**
     * 中心点の緯度経度座標を算出する
     */
    private LatLng calcCenter(LatLng[] vertexes) {
        if (vertexes.length == 1) {
            return vertexes[0];
        } else if (vertexes.length > 2) {
            double latSum = 0;
            double lngSum = 0;

            for (LatLng latLng : vertexes) {
                latSum += latLng.latitude;
                lngSum += latLng.longitude;
            }

            return new LatLng(latSum / vertexes.length, lngSum / vertexes.length);
        }

        throw new IllegalArgumentException();
    }
}
