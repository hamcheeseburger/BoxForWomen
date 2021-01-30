package com.example.MA02_20170953;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MyPackageAdapter extends CursorAdapter {
    Context context;
    Cursor cursor;
    int layout;
    LayoutInflater inflater;

    public MyPackageAdapter(Context context, int layout, Cursor c){
        super(context, c, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.context = context;
        cursor = c;
        this.layout = layout;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if(viewHolder.tv_package_name == null) {
            viewHolder.tv_package_name = view.findViewById(R.id.tv_package_name);
            viewHolder.tv_package_shop = view.findViewById(R.id.tv_package_shop);
            viewHolder.tv_package_order_date = view.findViewById(R.id.tv_package_order_date);
            viewHolder.tv_package_order_year = view.findViewById(R.id.tv_package_order_year);
            viewHolder.tv_package_received_statement = view.findViewById(R.id.tv_package_received_statement);
        }
        if(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_PACKAGE_RECEIVED_BOX_ID)) != -1) {
            viewHolder.tv_package_received_statement.setText("배송완료");
            viewHolder.tv_package_received_statement.setTextColor(Color.RED);
        }else{
            viewHolder.tv_package_received_statement.setTextColor(Color.GRAY);
            viewHolder.tv_package_received_statement.setText("배송중");
        }

        String date = cursor.getString(cursor.getColumnIndex(DBHelper.COL_PACKAGE_ORDER_DATE));
        String [] dates = date.split("/");

        viewHolder.tv_package_name.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_PACKAGE_NAME)));
        viewHolder.tv_package_shop.setText(cursor.getString(cursor.getColumnIndex(DBHelper.COL_PACKAGE_SHOP)));
        viewHolder.tv_package_order_date.setText(dates[1] + "/" + dates[2]);
        viewHolder.tv_package_order_year.setText(dates[0]);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(layout, parent, false);

        ViewHolder holder = new ViewHolder();
        view.setTag(holder);

        return view;
    }

    static class ViewHolder {
        TextView tv_package_order_year;
        TextView tv_package_received_statement;
        TextView tv_package_name;
        TextView tv_package_shop;
        TextView tv_package_order_date;

        public ViewHolder() {
            tv_package_order_year = null;
            tv_package_received_statement = null;
            tv_package_name = null;
            tv_package_shop = null;
            tv_package_order_date = null;
        }

    }
}
