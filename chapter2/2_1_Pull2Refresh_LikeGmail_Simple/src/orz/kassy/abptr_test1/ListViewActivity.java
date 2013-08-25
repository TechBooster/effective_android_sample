
package orz.kassy.abptr_test1;

import java.util.Arrays;
import java.util.LinkedList;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import uk.co.senab.actionbarpulltorefresh.library.*;

public class ListViewActivity extends ListActivity
        implements PullToRefreshAttacher.OnRefreshListener {

    // 最初のリスト
    private static final String[] INITIAL_LIST = {
        "最初に", "表示される", "リストの", "項目で", "あります",
    };

    // リストビューに設定するリストとアダプター
    private LinkedList<String> mItemList;
    private ArrayAdapter<String> mAdapter;

    // PullToRefreshに必要なフィールド
    private PullToRefreshAttacher mPullToRefreshAttacher;
    
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
        mPullToRefreshAttacher = PullToRefreshAttacher.get(this);
        mPullToRefreshAttacher.addRefreshableView(listView, this);
    }

    /**
     *  リスト更新開始時に呼ばれる
     */
    @Override
    public void onRefreshStarted(View view) {
        /**
         * 裏で更新処理を行うワーカータスク
         */
        new AsyncTask<Void, Void, Void>() {

            // ワーカースレッドで呼ばれる
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // ４秒待機する（フェイク）
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            // ４秒待機後にメインスレッドから呼ばれる
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                mItemList.addFirst(sAddCount++ + "番目に追加された項目");
                mAdapter.notifyDataSetChanged();
                mPullToRefreshAttacher.setRefreshComplete();
            }
        }.execute();
    }
}
