package com.example.c84databasesample;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MyCursorAdapter extends CursorAdapter {
    LayoutInflater inflater;

    public MyCursorAdapter(Context context, Cursor c) {
        super(context, c, false);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void bindView(View arg0, Context arg1, Cursor arg2) {
        String name = arg2.getString(arg2.getColumnIndex("NAME"));
        String age = arg2.getString(arg2.getColumnIndex("AGE"));
        
        TextView textView1 = (TextView)arg0.findViewById(R.id.textView1);
        TextView textView2 = (TextView)arg0.findViewById(R.id.textView2);

        textView1.setText(name);
        textView2.setText(age);
    }

    @Override
    public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
        View view = inflater.inflate(R.layout.listitem, arg2, false);
        return view;
    }

}
