package com.example.MA02_20170953;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class DetailMyPackageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_package_layout);
        Intent intent = getIntent();
        PackageDto dto = (PackageDto) intent.getSerializableExtra("dto");

        DBManager dbManager = new DBManager(this);

        //주문물품 정보
        TextView tv_detail_package_name = findViewById(R.id.tv_detail_package_name);
        TextView tv_detail_pacakage_shop = findViewById(R.id.tv_detail_package_shop);
        TextView tv_detail_package_order_date = findViewById(R.id.tv_detail_package_order_date);
        ImageButton btn_package_info_back = findViewById(R.id.btn_package_info_back);

        btn_package_info_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_detail_package_name.setText(dto.getPackageName());
        tv_detail_pacakage_shop.setText(dto.getShop());
        tv_detail_package_order_date.setText(dto.getOrderDate());


        // 보관함에 저장된 물품이라면..
        if(dto.getIsReceived() != -1) {
            ConstraintLayout detailPackageBoxLayout = findViewById(R.id.detailPackageBoxLayout);
            TextView tv_detail_package_box_name = findViewById(R.id.tv_detail_package_box_name);
            TextView tv_detail_package_received_date = findViewById(R.id.tv_detail_package_received_date);
            TextView tv_detail_package_partition = findViewById(R.id.tv_detail_package_partition);
            TextView tv_detail_package_password = findViewById(R.id.tv_detail_package_password);
            TextView textView_box_info = findViewById(R.id.textView_box_info);

            textView_box_info.setVisibility(View.VISIBLE);
            detailPackageBoxLayout.setVisibility(View.VISIBLE);

            Cursor cursor = dbManager.findInterBoxById(dto.getIsReceived());
            BoxDto boxDto = new BoxDto();
            if(cursor.moveToNext()){
                boxDto.setFcltyNm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_NAME)));
                boxDto.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBHelper.COL_INTER_LAT)));
                boxDto.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBHelper.COL_INTER_LNG)));
                boxDto.setLocation(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_LOCATION)));
                boxDto.setWeekdayOperOpenHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_WEEKDAY_OPEN)));
                boxDto.setWeekdayOperColseHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_WEEKDAY_CLOSE)));
                boxDto.setSatOperOperOpenHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_SAT_OPEN)));
                boxDto.setSatOperCloseHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_SAT_CLOSE)));
                boxDto.setHolidayOperOpenHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_HOLI_OPEN)));
                boxDto.setHolidayCloseOpenHhmm(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_HOLI_CLOSE)));
                boxDto.setFreeUseTime(cursor.getString(cursor.getColumnIndex(DBHelper.COL_INTER_FREETIMEUSE)));
            }

            tv_detail_package_box_name.setText(boxDto.getFcltyNm());
            tv_detail_package_box_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent boxIntent = new Intent(DetailMyPackageActivity.this, DetailMyInterBoxActivity.class);
                    boxIntent.putExtra("dto", boxDto);

                    startActivity(boxIntent);
                }
            });

            tv_detail_package_received_date.setText(dto.getReceivedDate());
            tv_detail_package_partition.setText(String.valueOf(dto.getPartition()));
            tv_detail_package_password.setText(dto.getPassword());
        }

    }
}
