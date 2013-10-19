
package orz.kassy.opencv_tb_sample1;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
    private static int sTH = 70;
    private static final String TAG = null;
    private ImageView mImageView1;
    private ImageView mImageView2;
    private ImageView mImageView3;
    private ImageView mImageView4;
    private ImageView mImageView5;
    private ImageView mImageView6;
    private ImageView mImageView7;
    private ImageView mImageView8;

    private Bitmap mBitmap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * OpenCVの初期化
         */
        if (!OpenCVLoader.initDebug()) {
            // Report initialization error
            Log.i(TAG,"OpenCV init Error");
        }

        // シークバーのセッティング
        SeekBar seekBar = (SeekBar) findViewById(R.id.pickupSeekbar);
        seekBar.setMax(255);
        seekBar.setProgress(30);
        sTH = 30;
        seekBar.setOnSeekBarChangeListener(new MySeekBarListener());

        // 他のUI部品セッティング
        mImageView1 = (ImageView) findViewById(R.id.imageView1);
        mImageView2 = (ImageView) findViewById(R.id.imageView2);
        mImageView3 = (ImageView) findViewById(R.id.imageView3);
        mImageView4 = (ImageView) findViewById(R.id.imageView4);
        mImageView5 = (ImageView) findViewById(R.id.imageView5);
        mImageView6 = (ImageView) findViewById(R.id.imageView6);
        mImageView7 = (ImageView) findViewById(R.id.imageView7);
        mImageView8 = (ImageView) findViewById(R.id.imageView8);
      
        // オリジナルのビットマップをオープン
        try {
            InputStream is = getResources().getAssets().open("orig.jpg");
            mBitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
        }
        extractObject(mBitmap);
    }

    /**
     * シークバーリスナー
     */
    class MySeekBarListener implements OnSeekBarChangeListener{
        // トラッキング開始時に呼び出されます
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        // トラッキング中に呼び出されます
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
            // しきい値の変更
            sTH = (int) (progress);
        }

        // トラッキング終了時に呼び出されます
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            extractObject(mBitmap);
        }
    }

    
    /**
     * OpenCVで
     * @param bmpOrig
     */
    private void extractObject(Bitmap bmpOrig) {

        // まずオリジナルのビットマップを表示
        mImageView1.setImageBitmap(bmpOrig);
        // 高さと幅を取得
        int height = bmpOrig.getHeight();
        int width = bmpOrig.getWidth();
        
        // OpenCVオブジェクトの用意
        Mat matOrig = new Mat(height,width,CvType.CV_8UC4); 
        // ビットマップをOpenCVオブジェクトに変換
        Utils.bitmapToMat(bmpOrig, matOrig);
        
        /**
         * グレースケールに変換
         */
        Mat matGray = new Mat(height,width,CvType.CV_8UC1);
        Imgproc.cvtColor(matOrig, matGray, Imgproc.COLOR_RGB2GRAY);
        // 表示
        Bitmap bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matGray, bmpGray);
        mImageView2.setImageBitmap(bmpGray);

        /**
         * グレースケール→二値化    
         */
        Mat matBlack = new Mat(height,width,CvType.CV_8UC1);
        // 二値化
        Imgproc.threshold(matGray, matBlack, sTH, 255, Imgproc.THRESH_BINARY);
        // 表示
        Bitmap bmpBlack = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matBlack, bmpBlack);
        mImageView3.setImageBitmap(bmpBlack);

        /**
         * グレースケール→二値化→輪郭塗りつぶし
         */
        // 輪郭を抽出する
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat(matBlack.height(),matBlack.width(),CvType.CV_8UC1);
        int mode = Imgproc.RETR_EXTERNAL;
        int method = Imgproc.CHAIN_APPROX_SIMPLE;

        // 輪郭を抽出する
        Imgproc.findContours(matBlack, contours, hierarchy, mode, method);
        // 輪郭を描く
        Scalar color = new Scalar(255.f, 0.f, 0.f, 0.f);
        Imgproc.drawContours(matBlack, contours, -1, color, 2);
        // 表示
        Bitmap bmpContour = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(matBlack, bmpContour);
        mImageView4.setImageBitmap(bmpContour);

        // 抽出した輪郭の内部を塗りつぶす
        Imgproc.drawContours(matBlack, contours, -1, color, -1);
        // 表示
        Bitmap bmpContour2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(matBlack, bmpContour2);
        mImageView5.setImageBitmap(bmpContour2);

        
        /**
         * 二値化したマスクを使ってオブジェクトだけをとりだす
         */
        Mat matObject = new Mat(height,width,CvType.CV_8UC4); 
        Core.add(matObject, matOrig, matObject, matBlack);  
        // 表示
        Bitmap bmpObject = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(matObject, bmpObject);
        mImageView6.setImageBitmap(bmpObject);

        /**
         * とりだしたオブジェクトに外接する矩形のみをBMPとして抜き出す
         */
        Rect rect = Imgproc.boundingRect(contours.get(0));
        Mat matCut = new Mat(matObject, rect);
        // 表示
        Bitmap bmpCut = Bitmap.createBitmap(matCut.cols(), matCut.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(matCut, bmpCut);
        mImageView7.setImageBitmap(bmpCut);
    }
}
