package com.example.applistapplication;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class AppListFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PackageManager packageManager = getActivity().getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(PackageManager.GET_ACTIVITIES);

        CardListAdapter adapter = new CardListAdapter(getActivity());

        if (packageInfoList != null) {
            for (PackageInfo info : packageInfoList) {
                adapter.add(info);
            }
        }

        int padding = (int) (getResources().getDisplayMetrics().density * 8); // 8dip
        ListView listView = getListView();
        listView.setPadding(padding, 0, padding, 0);
        listView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
        listView.setDivider(null);

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View header = inflater.inflate(R.layout.list_header_footer, listView, false);
        View footer = inflater.inflate(R.layout.list_header_footer, listView, false);
        listView.addHeaderView(header, null, false);
        listView.addFooterView(footer, null, false);

        setListAdapter(adapter);
    }

    public class CardListAdapter extends ArrayAdapter<PackageInfo> {

        LayoutInflater mInflater;
        PackageManager packageManager;

        public CardListAdapter(Context context) {
            super(context, 0);
            mInflater = LayoutInflater.from(context);
            packageManager = context.getPackageManager();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_card, parent, false);
            }

            PackageInfo info = getItem(position);

            TextView tv = (TextView) convertView.findViewById(R.id.title);
            tv.setText(info.applicationInfo.loadLabel(packageManager));

            tv = (TextView) convertView.findViewById(R.id.sub);
            tv.setText(info.packageName + "\n" + "versionName : " + info.versionName + "\nversionCode : " + info.versionCode);

            ImageView iv = (ImageView) convertView.findViewById(R.id.icon);
            iv.setImageDrawable(info.applicationInfo.loadIcon(packageManager));


            return convertView;
        }
    }
}
