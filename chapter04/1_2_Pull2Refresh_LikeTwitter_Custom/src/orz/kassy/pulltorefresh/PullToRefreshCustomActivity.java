package orz.kassy.pulltorefresh;

import java.util.Arrays;
import java.util.LinkedList;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public final class PullToRefreshCustomActivity extends ListActivity {

    private static final String[] INITIAL_LIST = {
        "最初に", "表示される", "リストの", "項目で", "あります",
      };

    static final int MENU_MANUAL_REFRESH = 0;
    static final int MENU_DISABLE_SCROLL = 1;
    static final int MENU_SET_MODE = 2;
    static final int MENU_DEMO = 3;

    private LinkedList<String> mIemsList;
    private PullToRefreshListView mPullRefreshListView;
    private ArrayAdapter<String> mAdapter;

    // リスト項目追加の目印
    private static int sAddCount = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_list);

        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            // ひっぱりきって指をはなしたとき？
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getApplicationContext(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                // Do work to refresh the list here.
                new GetDataTask().execute();
            }
        });

        /**
         * customize
         */
        mPullRefreshListView.setMode(Mode.BOTH);

        // LoadingLayoutに関してカスタマイズ（主に文言）
        ILoadingLayout iLoadingLayout = mPullRefreshListView.getLoadingLayoutProxy(true, true);
        iLoadingLayout.setLastUpdatedLabel("");
        iLoadingLayout.setReleaseLabel("離してください、更新します");
        iLoadingLayout.setPullLabel("さらに下に引いて下さい");
        iLoadingLayout.setRefreshingLabel("更新中です");

        // Add an end-of-list listener
        mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Toast.makeText(PullToRefreshCustomActivity.this, "End of List!", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        /**
         *  リスト表示
         */
        mIemsList = new LinkedList<String>();
        mIemsList.addAll(Arrays.asList(INITIAL_LIST));
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mIemsList);

        ListView actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setAdapter(mAdapter);
    }

    /**
     * リストを引っ張った時にデータを取得しにいくワーカータスク
     */
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // ここでデータ取得処理（ここではスタブ的に一定時間待機）
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            return INITIAL_LIST;
        }

        @Override
        protected void onPostExecute(String[] result) {
            // リストにデータ追加
            mIemsList.addFirst(sAddCount++ + "番目に追加された項目");
            mAdapter.notifyDataSetChanged();
            mPullRefreshListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }

}
