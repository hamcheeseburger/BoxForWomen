package com.example.MA02_20170953;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class InterBoxAdapter extends CursorAdapter {
    Cursor cursor;
    Context context;
    int layout;
    LayoutInflater inflater;

    public InterBoxAdapter(Context context, int layout, Cursor c){
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        cursor = c;
        this.context = context;
        this.layout = layout;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
       ViewHolder viewHolder = (ViewHolder) view.getTag();
       if(viewHolder.tv_inter_name == null) {
           viewHolder.tv_inter_name = view.findViewById(R.id.tv_inter_name);
           viewHolder.tv_inter_loc = view.findViewById(R.id.tv_inter_loc);
       }

       viewHolder.tv_inter_name.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_NAME)));
       viewHolder.tv_inter_loc.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_LOCATION)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);

        return view;
    }

    static class ViewHolder {
        TextView tv_inter_name;
        TextView tv_inter_loc;

        public ViewHolder() {
            tv_inter_loc = null;
            tv_inter_name = null;
        }

    }
}
