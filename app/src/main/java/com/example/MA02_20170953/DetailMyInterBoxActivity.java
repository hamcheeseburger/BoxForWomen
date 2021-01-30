package com.example.MA02_20170953;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetailMyInterBoxActivity extends AppCompatActivity {

    GoogleMap mGoogleMap;
    BoxDto dto;
    View mapView;
    AlertDialog mapDialog;
    Marker centerMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_inter_box_layout);

        TextView tv_info_name = findViewById(R.id.tv_info_name);
        TextView tv_info_loc = findViewById(R.id.tv_info_loc);
        TextView tv_info_weekday_ope = findViewById(R.id.tv_info_weekday_ope);
        TextView tv_info_sat_ope = findViewById(R.id.tv_info_sat_ope);
        TextView tv_info_holy_ope = findViewById(R.id.tv_info_holy_ope);
        TextView tv_info_freeusetime = findViewById(R.id.tv_info_freeusetime);
        TextView tv_show_map = findViewById(R.id.tv_show_map);
        ImageButton btn_detail_box_back = findViewById(R.id.btn_detail_box_back);
        btn_detail_box_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_show_map.setVisibility(View.VISIBLE);

        LayoutInflater inflater = getLayoutInflater();
        mapView = inflater.inflate(R.layout.inter_box_map_alert_layout, null);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.alertMap);
        mapFragment.getMapAsync(callback);
        ImageButton btn_map_close = mapView.findViewById(R.id.btn_map_close);
        btn_map_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapDialog.dismiss();
            }
        });

        AlertDialog.Builder mapAlert = new AlertDialog.Builder(this);
        mapAlert.setView(mapView);
        mapDialog = mapAlert.create();
        mapDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Intent intent = getIntent();
        dto = (BoxDto) intent.getSerializableExtra("dto");

        tv_info_name.setText(dto.getFcltyNm());
        tv_info_loc.setText(dto.getLocation());
        tv_info_weekday_ope.setText(dto.getWeekdayOperOpenHhmm() + " ~ " + dto.getWeekdayOperColseHhmm());
        tv_info_sat_ope.setText(dto.getSatOperOperOpenHhmm() + " ~ " + dto.getSatOperCloseHhmm());
        tv_info_holy_ope.setText(dto.getHolidayOperOpenHhmm() + " ~ " + dto.getHolidayCloseOpenHhmm());
        tv_info_freeusetime.setText(dto.getFreeUseTime() + "시간");

        tv_show_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                centerMarker.setPosition(new LatLng(dto.getLatitude(), dto.getLongitude()));
                mapDialog.show();
            }
        });

    }

    OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            //Log.d(TAG, "onMapReady()");
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(dto.getLatitude(), dto.getLongitude()));
            centerMarker = mGoogleMap.addMarker(options);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerMarker.getPosition(), 17));
        }
    };
}
