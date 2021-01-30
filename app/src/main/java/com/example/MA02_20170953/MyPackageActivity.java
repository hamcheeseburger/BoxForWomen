package com.example.MA02_20170953;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.*;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyPackageActivity extends AppCompatActivity {

    final static String TAG = "MyPackageActivity";
    AlertDialog addDialog;
    AlertDialog setDialog;

    // 물품추가
    EditText et_package_name;
    EditText et_package_shop;
    TextView tv_package_date;
    TextView tv_set_date;
    EditText et_set_partition;
    EditText et_set_password;
    DatePickerDialog datePickerDialog;
    PackageDto packageDto;

    boolean datePicked;
    DBManager dbManager;

    // 물품 보기
    SwipeMenuListView packageListView;
    MyPackageAdapter adapter;
    TabLayout tabLayout;
    Cursor cursor;
    Cursor boxCursor;

    boolean allPicked = true;

    int pos;
    boolean whereUsed; // datePickerDialog를 두 군데서 공유하기 때문에 구분하려고 만든 변수
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_package);

        dbManager = new DBManager(this);
        datePicked = false;
        packageDto = new PackageDto();
        tabLayout = findViewById(R.id.tabLayout);
        ImageButton btn_package_back = findViewById(R.id.btn_package_back);
        btn_package_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position){
                    case 0: // 전체 물품 보기
                        Log.d(TAG, "전체물품");
                        allPicked = true;
                        cursor = dbManager.getAllPackage();
                        break;
                    case 1: // 보관된 물품 보기
                        allPicked = false;
                        Log.d(TAG, "보관된물품");
                        cursor = dbManager.getReceivedPackage();
                        break;
                }
                adapter.changeCursor(cursor);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem setItem = new SwipeMenuItem(getApplicationContext());
                setItem.setBackground(new ColorDrawable(Color.rgb(0xd6, 0xd6, 0xd6)));
                setItem.setWidth(dp2px(90));
                setItem.setTitle("보관함지정");
                setItem.setTitleSize(14);
                setItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(setItem);

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

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View addAlertView = inflater.inflate(R.layout.add_package_alert_layout, null);

        adapter = new MyPackageAdapter(this, R.layout.package_list_layout, null);

        packageListView = findViewById(R.id.package_listView);
        et_package_name = addAlertView.findViewById(R.id.et_package_name);
        et_package_shop = addAlertView.findViewById(R.id.et_package_shop);
        tv_package_date = addAlertView.findViewById(R.id.tv_package_date);
        ImageButton btn_package_save = addAlertView.findViewById(R.id.btn_package_save);
        ImageButton btn_package_save_cancel = addAlertView.findViewById(R.id.btn_package_save_cancel);

        btn_package_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_package_name.getText().toString();
                String shop = et_package_shop.getText().toString();
                String date = tv_package_date.getText().toString();

                if (name.equals("") || shop.equals("") || !datePicked) {
                    Toast.makeText(MyPackageActivity.this, "항목을 모두 입력하세요", Toast.LENGTH_SHORT).show();
                } else {
                    packageDto.setPackageName(name);
                    packageDto.setShop(shop);
                    packageDto.setOrderDate(date);
                    packageDto.setIsReceived(-1);

                    if (dbManager.addPackage(packageDto)) {
                        Toast.makeText(MyPackageActivity.this, "물품이 추가되었습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyPackageActivity.this, "추가 실패", Toast.LENGTH_SHORT).show();
                    }

                    refreshCursor();

                    et_package_name.setText("");
                    et_package_shop.setText("");
                    tv_package_date.setText("날짜를 입력하려면 여기를 클릭하세요");
                    addDialog.dismiss();
                }

            }
        });
        btn_package_save_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog.dismiss();
            }
        });

        packageListView.setAdapter(adapter);
        packageListView.setMenuCreator(creator);

        packageListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                pos = position;
                cursor.moveToPosition(pos);
                Log.d(TAG, cursor.getString(cursor.getColumnIndex(DBHelper.COL_PACKAGE_NAME)));
                Log.d(TAG, pos + "<-포지션");
                int id = cursor.getInt(cursor.getColumnIndex(DBHelper.COL_PACKAGE_ID));
                switch(index) {
                    case 0:
                        whereUsed = false;
                        datePicked = false;
                        setDialog.show();

                        break;
                    case 1:
                        if(dbManager.removePackage(id)){
                            Toast.makeText(MyPackageActivity.this, "삭제완료", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MyPackageActivity.this, "삭제실패", Toast.LENGTH_SHORT).show();
                        }

                        refreshCursor();
                        break;
                }

                return false;
            }
        });
        packageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                cursor.moveToPosition(pos);

                PackageDto dto = new PackageDto();
                dto.setPackageName(cursor.getString(cursor.getColumnIndex(DBHelper.COL_PACKAGE_NAME)));
                dto.setShop(cursor.getString(cursor.getColumnIndex(DBHelper.COL_PACKAGE_SHOP)));
                dto.setOrderDate(cursor.getString(cursor.getColumnIndex(DBHelper.COL_PACKAGE_ORDER_DATE)));
                dto.setIsReceived(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_PACKAGE_RECEIVED_BOX_ID)));
                dto.setReceivedDate(cursor.getString(cursor.getColumnIndex(DBHelper.COL_PACKAGE_RECEIVED_DATE)));
                dto.setPartition(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_PACKAGE_RECEIVED_PARTITION)));
                dto.setPassword(cursor.getString(cursor.getColumnIndex(DBHelper.COL_PACKAGE_RECEIVED_PASSWORD)));

                Intent intent = new Intent(MyPackageActivity.this, DetailMyPackageActivity.class);
                intent.putExtra("dto", dto);

                startActivity(intent);
            }
        });


        // 현재 날짜
        long now = System.currentTimeMillis();
        Date currentTime = new Date(now);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        int currentYear = Integer.parseInt(yearFormat.format(currentTime));
        int currentMonth = Integer.parseInt(monthFormat.format(currentTime));
        int currentDay = Integer.parseInt(dayFormat.format(currentTime));

        datePickerDialog = new DatePickerDialog(this, datePickerCallback, currentYear, currentMonth - 1, currentDay);
        tv_package_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(addAlertView);

        addDialog = builder.create();
        addDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 어떤 보관함에 물품이 보관되었고, 언제 보관되었는지 묻는 alertDialog
        View setBoxView = inflater.inflate(R.layout.set_box_alert_layout, null);
        Spinner box_set_spinner = setBoxView.findViewById(R.id.box_set_spinner);
        tv_set_date = setBoxView.findViewById(R.id.tv_set_date);
        tv_set_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked");
                datePickerDialog.show();
            }
        });
        et_set_password = setBoxView.findViewById(R.id.et_set_password);
        et_set_partition = setBoxView.findViewById(R.id.et_set_partition);
        ImageButton btn_box_save = setBoxView.findViewById(R.id.btn_box_save);
        ImageButton btn_box_save_cancel = setBoxView.findViewById(R.id.btn_box_sace_cancel);

        btn_box_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int boxId = boxCursor.getInt(boxCursor.getColumnIndex(DBHelper.COL_INTER_ID));
                String receivedDate = tv_set_date.getText().toString();
                String password = et_set_password.getText().toString();
                String partition_string = et_set_partition.getText().toString();

                cursor.moveToPosition(pos);

                if(!datePicked || password.equals("") || partition_string.equals("")){
                    Toast.makeText(MyPackageActivity.this, "내용을 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    int partition = Integer.valueOf(partition_string);

                    Log.d(TAG, cursor.getInt(cursor.getColumnIndex(DBHelper.COL_PACKAGE_ID)) + ", " + cursor.getString(cursor.getColumnIndex(DBHelper.COL_PACKAGE_NAME)));
                    if (dbManager.setBoxToPackage(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_PACKAGE_ID)), boxId, receivedDate, password, partition)) {
                        Toast.makeText(MyPackageActivity.this, "저장완료", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyPackageActivity.this, "저장실패", Toast.LENGTH_SHORT).show();
                    }

                    cursor = dbManager.getAllPackage();
                    adapter.changeCursor(cursor);

                    et_set_partition.setText("");
                    et_set_password.setText("");
                    tv_set_date.setText("여기를 클릭하여 날짜선택");
                    setDialog.dismiss();
                }
            }
        });
        btn_box_save_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog.dismiss();
            }
        });

        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setView(setBoxView);
        setDialog = builder2.create();
        setDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 보관함 리스트를 보여주는 spinner 설정
        ArrayList<String> boxArray = getBoxArray();
        ArrayAdapter spinnerAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, boxArray);
        box_set_spinner.setAdapter(spinnerAdapter);

        box_set_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                boxCursor.moveToPosition(pos);
                Log.d(TAG, boxCursor.getString(boxCursor.getColumnIndex(DBHelper.COL_INTER_NAME)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    DatePickerDialog.OnDateSetListener datePickerCallback = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            datePicked = true;
            int printMonth = month + 1;
            if(whereUsed) {
                tv_package_date.setText(year + "/" + printMonth + "/" + dayOfMonth);
            }else{
                tv_set_date.setText(year + "/" + printMonth + "/" + dayOfMonth);
            }
        }
    };

    public ArrayList<String> getBoxArray(){
        boxCursor = dbManager.getAllInterBox();
        ArrayList<String> boxArray = new ArrayList<String>();
        while(boxCursor.moveToNext()){
            boxArray.add(boxCursor.getString(boxCursor.getColumnIndex(DBHelper.COL_INTER_NAME)));
        }

        return boxArray;
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_package_add:
                whereUsed = true;
                addDialog.show();
                break;
        }
    }

    public void refreshCursor(){
        cursor = dbManager.getAllPackage();
        adapter.changeCursor(cursor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(allPicked) {
            refreshCursor();
        }else{
            cursor = dbManager.getReceivedPackage();
            adapter.changeCursor(cursor);
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
