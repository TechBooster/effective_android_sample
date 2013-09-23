package org.techbooster.sample.bookmarkloader;

import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.provider.Browser;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends FragmentActivity
				implements LoaderManager.LoaderCallbacks<Cursor> {
	private SimpleCursorAdapter mAdapter = null;
	
	// ブックマークで読み込む項目
	private static final String[] BOOKMARK_PROJECTION = new String[] {
		Browser.BookmarkColumns._ID,
		Browser.BookmarkColumns.TITLE,
		Browser.BookmarkColumns.URL
	};
	// ブックマークで表示する項目
	private static final String[] ADAPTER_FROM = new String[] {
		Browser.BookmarkColumns.TITLE,
		Browser.BookmarkColumns.URL
	};
	// UIでバインディングする項目
	private static final int[]    ADAPTER_TO   = new int[] {
		android.R.id.text1,
		android.R.id.text2
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// SimpleCursorAdapterのインスタンス生成
		mAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2,	// ２行表示のレイアウト指定
				null,									// Cursorは空で設定
				ADAPTER_FROM,							// ブックマークの項目設定
				ADAPTER_TO,								// UIでバインドする
				0);
		
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(mAdapter);
		
		// Loaderの初期化
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_addBookmark:
			// ブックマークの追加
			Browser.saveBookmark(this, "TechBooster", "http://techbooster.org/");
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	};

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// ブックマークをContentProviderから読み込むCursorLoaderの生成
		return new CursorLoader(this,
				Browser.BOOKMARKS_URI,
				BOOKMARK_PROJECTION,
				null,
				null,
				Browser.BookmarkColumns.CREATED + " desc");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// ブックマークの読み込んだデータをAdapterに設定
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> data) {
		// Adapterの中身を空に設定
		mAdapter.swapCursor(null);
	}

}
