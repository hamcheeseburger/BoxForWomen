package com.example.MA02_20170953;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

public class MyInterBoxActivity extends AppCompatActivity {

    final static String TAG = "MyInterBoxActivity";
    SwipeMenuListView interListView;
    DBManager manager;
    InterBoxAdapter adapter;
    Cursor cursor;

    View cautionView;
    AlertDialog cautionDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_inter_box);

        interListView = findViewById(R.id.interBox_listView);

        ImageView btn_inter_back = findViewById(R.id.btn_inter_back);
        btn_inter_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        manager = new DBManager(this);
        adapter = new InterBoxAdapter(this, R.layout.inter_box_list_layout, null);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        // 정보 alertDialog
        LayoutInflater inflater = getLayoutInflater();
        cautionView = inflater.inflate(R.layout.caution_alert, null);
        ImageButton btn_box_remove = cautionView.findViewById(R.id.btn_box_remove);
        ImageButton btn_box_remove_cancel = cautionView.findViewById(R.id.btn_box_remove_cancel);

        btn_box_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_INTER_ID));
                Log.d(TAG, "id : " + id);

                Cursor packageCursor = manager.findPackageByBoxId(id);
                while(packageCursor.moveToNext()) { // 보관함과 연결된 물품 모두 삭제
                    manager.removePackage(packageCursor.getInt(packageCursor.getColumnIndex(DBHelper.COL_PACKAGE_ID)));
                }

                if(manager.removeInterBox(id)) {
                    refreshCursor();
                    Toast.makeText(MyInterBoxActivity.this, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }
                cautionDialog.dismiss();
            }
        });
        btn_box_remove_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cautionDialog.dismiss();
            }
        });

        AlertDialog.Builder cautionBuilder = new AlertDialog.Builder(this);
        cautionBuilder.setView(cautionView);

        cautionDialog = cautionBuilder.create();
        cautionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        interListView.setAdapter(adapter);
        interListView.setMenuCreator(creator);

        interListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                final int pos = position;
                cursor.moveToPosition(pos);
                switch (index){
                    case 0:
                        cautionDialog.show();
                        break;
                }
                return false;
            }
        });

        interListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                cursor.moveToPosition(pos);
                BoxDto dto = new BoxDto();
                dto.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBHelper.COL_INTER_LAT)));
                dto.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBHelper.COL_INTER_LNG)));
                dto.setFcltyNm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_NAME)));
                dto.setLocation(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_LOCATION)));
                dto.setWeekdayOperOpenHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_WEEKDAY_OPEN)));
                dto.setWeekdayOperColseHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_WEEKDAY_CLOSE)));
                dto.setSatOperOperOpenHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_SAT_OPEN)));
                dto.setSatOperCloseHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_SAT_CLOSE)));
                dto.setHolidayOperOpenHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_HOLI_OPEN)));
                dto.setHolidayCloseOpenHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_HOLI_CLOSE)));
                dto.setFreeUseTime(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_FREETIMEUSE)));

                Intent intent = new Intent(MyInterBoxActivity.this, DetailMyInterBoxActivity.class);
                intent.putExtra("dto", dto);

                startActivity(intent);
            }
        });
    }

    public void refreshCursor(){
        cursor = manager.getAllInterBox();
        adapter.changeCursor(cursor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCursor();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}
