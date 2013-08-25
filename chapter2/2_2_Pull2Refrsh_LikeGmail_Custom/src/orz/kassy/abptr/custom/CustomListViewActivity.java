
package orz.kassy.abptr.custom;

import java.util.Arrays;
import java.util.LinkedList;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import uk.co.senab.actionbarpulltorefresh.library.*;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher.HeaderTransformer;

public class CustomListViewActivity extends ListActivity
        implements PullToRefreshAttacher.OnRefreshListener {

    // 最初のリスト
    private static final String[] INITIAL_LIST = {
            "最初に", "表示される", "リストの", "項目で", "あります",
    };

    private PullToRefreshAttacher mPullToRefreshAttacher;

    // リストビューに設定するリストとアダプター
    private LinkedList<String> mItemList;
    private ArrayAdapter<String> mAdapter;

    // リスト項目追加の目印
    private static int sAddCount = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = getListView();
        mItemList = new LinkedList<String>();
        mItemList.addAll(Arrays.asList(INITIAL_LIST));
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mItemList);
        listView.setAdapter(mAdapter);

        // PullToRefreshのカスタマイズ ここから
        PullToRefreshAttacher.Options options = new PullToRefreshAttacher.Options();

        // ヘッダービューをカスタマイズ
        options.headerInAnimation = R.anim.fade_in_custom;
        options.refreshScrollDistance = 0.5f;
        options.headerOutAnimation = R.anim.fade_out_custom;
        options.headerLayout = R.layout.header_custom;

        // ヘッダービューをカスタマイズしたときの挙動を再定義
        options.headerTransformer = new HeaderTransformer() {
            private TextView mHeaderTextView;
            private ProgressBar mHeaderProgressBar;
            private final Interpolator mInterpolator = new AccelerateInterpolator();

            // ヘッダービュー生成時、引数にヘッダービューが渡される
            @Override
            public void onViewCreated(Activity activity, View headerView) {

                // 定義したビューを取得
                mHeaderProgressBar = (ProgressBar) headerView.findViewById(R.id.ptr_progress);
                mHeaderTextView = (TextView) headerView.findViewById(R.id.ptr_text);

                // ここでonResetを呼ぶと良い
                onReset();
            }

            // リセット時
            @Override
            public void onReset() {
                // プログレスバーを画す
                mHeaderProgressBar.setVisibility(View.GONE);
                mHeaderProgressBar.setProgress(0);
                // 文言を設定
                mHeaderProgressBar.setIndeterminate(false);
                mHeaderTextView.setVisibility(View.VISIBLE);
                mHeaderTextView.setText("引くなよ！絶対に引くなよ！");
            }

            // リストビューを引っ張っている時
            @Override
            public void onPulled(float percentagePulled) {
                // バーの長さを設定
                mHeaderProgressBar.setVisibility(View.VISIBLE);
                final float progress = mInterpolator.getInterpolation(percentagePulled);
                mHeaderProgressBar.setProgress(Math.round(mHeaderProgressBar.getMax() * progress));
            }

            // 引っ張りきって更新開始する時
            @Override
            public void onRefreshStarted() {
                // 文言を設定
                mHeaderTextView.setText("絶望がゴールだ... ");
                // プログレスバー登場
                mHeaderProgressBar.setVisibility(View.VISIBLE);
                mHeaderProgressBar.setIndeterminate(true);
            }

			@Override
			public void onReleaseToRefresh() {
			}

			@Override
			public void onRefreshMinimized() {
			}
        };

        // カスタマイズした設定を反映させる
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this, options);
        mPullToRefreshAttacher.addRefreshableView(listView, this);
    }

    @Override
    public void onRefreshStarted(View view) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                mItemList.addFirst(sAddCount++ + "番目に追加された項目");
                mAdapter.notifyDataSetChanged();

                // Notify PullToRefreshAttacher that the refresh has finished
                mPullToRefreshAttacher.setRefreshComplete();
            }
        }.execute();
    }

    protected int getActionBarSize(Context context) {
        int[] attrs = {
                android.R.attr.actionBarSize
        };
        TypedArray values = context.getTheme().obtainStyledAttributes(attrs);
        try {
            return values.getDimensionPixelSize(0, 0);
        } finally {
            values.recycle();
        }
    }

    protected Drawable getActionBarBackground(Context context) {
        int[] android_styleable_ActionBar = {
                android.R.attr.background
        };

        // Need to get resource id of style pointed to from actionBarStyle
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarStyle, outValue, true);
        // Now get action bar style values...
        TypedArray abStyle = context.getTheme().obtainStyledAttributes(outValue.resourceId,
                android_styleable_ActionBar);
        try {
            // background is the first attr in the array above so it's index is
            // 0.
            return abStyle.getDrawable(0);
        } finally {
            abStyle.recycle();
        }
    }

}
