package orz.kassy.pulltorefresh;

import java.util.Arrays;
import java.util.LinkedList;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public final class PullToRefreshListActivity extends ListActivity {

    // 最初のリスト
    private static final String[] INITIAL_LIST = {
            "最初に", "表示される", "リストの", "項目で", "あります",
    };

    // 引っ張れるリストビュー
    private PullToRefreshListView mPullRefreshListView;
    
    // リストビューに設定するリストとアダプター
    private LinkedList<String> mItemList;
    private ArrayAdapter<String> mAdapter;

    // リスト項目追加の目印
    private static int sAddCount = 1;

    
    /**
     *  Activity生成時にシステムから呼ばれる
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_list);

        // レイアウトからカスタムリストビューを取得
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

        // リストを引っ張ったときの処理を記述
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                new GetDataTask().execute();
            }
        });

        // リストビューにアイテム追加
        mItemList = new LinkedList<String>();
        mItemList.addAll(Arrays.asList(INITIAL_LIST));
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mItemList);
        ListView actualListView = mPullRefreshListView.getRefreshableView();
        actualListView.setAdapter(mAdapter);
    }

    /**
     * リストを引っ張った時にデータを取得しにいくワーカータスク
     */
    private class GetDataTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // ここでデータ取得処理（ここではスタブ的に一定時間待機）
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // リストにデータ追加
            mItemList.addFirst(sAddCount++ + "番目に追加された項目");
            mAdapter.notifyDataSetChanged();
            // カスタムリストビューに完了を伝える
            mPullRefreshListView.onRefreshComplete();
            super.onPostExecute(result);
        }
    }
}
