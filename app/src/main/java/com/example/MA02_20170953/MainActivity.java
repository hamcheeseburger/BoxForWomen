package com.example.MA02_20170953;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    static final String TAG = "MainActivity";
    NetworkManager netManager;
    StringBuilder urlBuilder;
    BoxXmlParser parser;
    ArrayList<BoxDto> resultList;

    GoogleMap mGoogleMap;
    MarkerOptions markerOptions;
    ArrayList<Marker> markerList;
    Location lastLocation;
    LatLng currentLatLng = null;
    AddressResultReceiver addressResultReceiver;
    final static int PERMISSION_REQ_CODE = 100;

    String [] currentArea = new String[2];
    ProgressDialog progressDialog;

    LocationManager locationManager;

    private boolean requestingLocationUpdates;

    LinearLayout searchOptionLayout;
    TextView tv_search_loc;
    TextView tv_search_input;

    View infoView;
    TextView tv_info_name;
    TextView tv_info_loc;
    TextView tv_info_weekday_ope;
    TextView tv_info_sat_ope;
    TextView tv_info_holy_ope;
    TextView tv_info_freetimeuse;

    AlertDialog infoDialog;
    AlertDialog searchDialog;

    DrawerLayout drawerLayout;
    int clickedIndex;
    DBManager manager;

    Bitmap smallMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Foreground foreground = Foreground.get();
        foreground.init(getApplication());

        searchOptionLayout = findViewById(R.id.searchLayout);
        tv_search_input = findViewById(R.id.tv_search_input);
        tv_search_loc = findViewById(R.id.tv_search_loc);

        createNotificationChannel();

        LayoutInflater inflater = getLayoutInflater();

        // 검색 alertDialog
        View dialogView = inflater.inflate(R.layout.search_alert_layout, null);
        EditText et_city = dialogView.findViewById(R.id.et_city);
        EditText et_gu = dialogView.findViewById(R.id.et_gu);
        Button btn_box_search = dialogView.findViewById(R.id.btn_box_search);
        Button btn_box_search_cancel = dialogView.findViewById(R.id.btn_box_search_cancel);

        btn_box_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = et_city.getText().toString();
                String gu = et_gu.getText().toString();

                if(city.equals("") || gu.equals("")){
                    Toast.makeText(MainActivity.this, "내용을 빠짐없이 입력하세요", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        searchResultwithParam(city, gu);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    searchDialog.dismiss();
                }
            }
        });

        btn_box_search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog.dismiss();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        searchDialog = builder.create();
        searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        // 정보 alertDialog
        infoView = inflater.inflate(R.layout.detail_box_alert, null);
        tv_info_name = infoView.findViewById(R.id.tv_alert_name);
        tv_info_loc = infoView.findViewById(R.id.tv_alert_loc);
        tv_info_weekday_ope = infoView.findViewById(R.id.tv_alert_weekday_ope);
        tv_info_sat_ope = infoView.findViewById(R.id.tv_alert_sat_ope);
        tv_info_holy_ope = infoView.findViewById(R.id.tv_alert_holy_ope);
        tv_info_freetimeuse = infoView.findViewById(R.id.tv_alert_freeusetime);
        Button btn_alert_save = infoView.findViewById(R.id.btn_alert_set_inter_box);
        Button btn_alert_cancel = infoView.findViewById(R.id.btn_alert_cancel);

        btn_alert_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoxDto dto = resultList.get(clickedIndex);
                if(manager.findInterBoxWithLatLng(dto)) {
                    Toast.makeText(MainActivity.this, "이미 관심보관함으로 등록이 되어 있습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(manager.addInterBox(dto)){
                    Marker m = markerList.get(clickedIndex);
                    m.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                    Toast.makeText(MainActivity.this, "관심보관함으로 설정되었습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "설정에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
                infoDialog.dismiss();
            }
        });

        btn_alert_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDialog.dismiss();
            }
        });

        AlertDialog.Builder infoBuilder = new AlertDialog.Builder(this);
        infoBuilder.setView(infoView);

        infoDialog = infoBuilder.create();
        infoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 관심보관함 마커 등록
        BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.ic_star);
        Bitmap b = bitmapDrawable.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);

        // Drawer 관련 변수 초기화
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);

        // DB 관련 변수 초기화
        manager = new DBManager(this);
        // clickListener
        tv_search_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                try {
                    searchResultwithParam(currentArea[0], currentArea[1]);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                searchOptionLayout.setVisibility(View.INVISIBLE);
            }
        });
        tv_search_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog.show();
                searchOptionLayout.setVisibility(View.INVISIBLE);
            }
        });

        // 위치 관련 변수 초기화
        requestingLocationUpdates = false;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        lastLocation = null;

        // 네트워크 관련 변수 초기화
        netManager = new NetworkManager(this);
        urlBuilder = new StringBuilder(getString(R.string.api_url)); /*URL*/

        try {
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + getString(R.string.api_key)); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("type","UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8")); /*XML/JSON 여부*/

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        parser = new BoxXmlParser();
        resultList = new ArrayList<BoxDto>();
        addressResultReceiver = new AddressResultReceiver(new Handler(Looper.getMainLooper()));

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("데이터 로드중");

        markerList = new ArrayList<Marker>();
        markerOptions = new MarkerOptions();

        requirePerms();

        if(checkPermission()){
            mapLoad();
        }
    }

    private void mapLoad(){
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallBack);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLastLocation();
        startLocationUpdate();
        removeMarker();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(requestingLocationUpdates) {
            stopLocationUpdate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(requestingLocationUpdates) {
            stopLocationUpdate();
        }
    }

    public void getLastLocation(){
        Log.d(TAG, "getLastLocation() 호출됨");
        if(checkPermission()){
            lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
    }

    public void searchResultwithParam (String city, String gu) throws UnsupportedEncodingException {
        progressDialog.show();
        resultList.clear();
        String address = urlBuilder.toString();
        if(currentArea.length != 0) {
            String area = "&" + URLEncoder.encode(Constants.PARAM_CITY,"UTF-8") + "=" + URLEncoder.encode(city, "UTF-8")
                    + "&" + URLEncoder.encode(Constants.PARAM_GU,"UTF-8") + "=" + URLEncoder.encode(gu, "UTF-8"); // 시도명과 구 명으로 검색
            address += area;
        }
        Log.d(TAG, address);

        new ThreadTask<String, ArrayList<BoxDto>>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected ArrayList<BoxDto> doInBackground(String arg) {
                Log.d(TAG, arg);
                String result = netManager.downloadContents(arg);
                if(result == null){
                    return null;
                }
                resultList = parser.parse(result);

                return resultList;
            }

            @Override
            protected void onPostExecute(ArrayList<BoxDto> resultList) {
                String info = "";

                for(BoxDto dto : resultList){
                    info += dto.getFcltyNm() + "\n";
                }
//                        Log.d(TAG, info);
                putMarkersWithResult();
            }
        }.execute(address);
    }


    public void onClick(View v){
        switch(v.getId()){
            case R.id.button:
                String s = "현재 내 위치로 검색\n \"" + currentArea[0] + " " + currentArea[1] + "\"";
                tv_search_loc.setText(s);
                tv_search_input.setText("입력하여 검색");
                searchOptionLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void drawerOnClick(View v){
        switch (v.getId()){
            case R.id.tv_drawer_mypackage:
                Intent intent2 = new Intent(this, MyPackageActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_drawer_interbox:
                Log.d(TAG, "tv_drawer_interbox is clicked");
                Intent intent = new Intent(this, MyInterBoxActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_get_drawer:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }

    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            if(checkPermission()) {
                mGoogleMap.setMyLocationEnabled(true);
            }

            if(lastLocation == null){
                lastLocation = new Location("");
                lastLocation.setLatitude(37.60758125705513);
                lastLocation.setLongitude(127.04240465765618);
                Toast.makeText(MainActivity.this, "오른쪽 상단의 버튼을 클릭하여 위치를 업데이트 하세요.", Toast.LENGTH_SHORT).show();
            }



            // 맵 로드 시 초기 위치 설정
            startAddressService(lastLocation.getLatitude(), lastLocation.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 17));

            mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    //지오코딩으로 현재 있는 구 정보 가져오기

                    Location getMyLocation = mGoogleMap.getMyLocation();
                    if(getMyLocation != null) {
                        Log.d(TAG, getMyLocation.getLatitude() + ", " + getMyLocation.getLongitude());
                        startAddressService(getMyLocation.getLatitude(), getMyLocation.getLongitude());
                    }else{
                        Log.d(TAG, "getMyLocation is null");
                        startAddressService(lastLocation.getLatitude(), lastLocation.getLongitude());
                    }

                    if(checkPermission()) {
                        Log.d(TAG, "현재위치로 애니매이트");
                       // startAddressService(getMyLocation.getLatitude(), getMyLocation.getLongitude());
                        //mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getMyLocation.ge, currentLatLng.longitude), 17));
                    }
                    return false;
                }
            });

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    if(marker.getTag() != null) {
                        clickedIndex = (Integer) marker.getTag();
                        BoxDto dto = resultList.get(clickedIndex);

                        tv_info_name.setText(dto.getFcltyNm());
                        tv_info_loc.setText(dto.getLocation());
                        tv_info_weekday_ope.setText(dto.getWeekdayOperOpenHhmm() + " ~ " + dto.getWeekdayOperColseHhmm());
                        tv_info_sat_ope.setText(dto.getSatOperOperOpenHhmm() + " ~ " + dto.getSatOperCloseHhmm());
                        tv_info_holy_ope.setText(dto.getHolidayOperOpenHhmm() + " ~ " + dto.getHolidayCloseOpenHhmm());
                        tv_info_freetimeuse.setText(dto.getFreeUseTime() + "시간");
                    }
                    infoDialog.show();
                }
            });

            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng latLng) {
                    searchOptionLayout.setVisibility(View.INVISIBLE);
                }
            });

        }
    };

    private void startAddressService(double lat, double lng) {
        Log.d(TAG, "startAddressService() 호출됨");
        Intent intent = new Intent();
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LATITUDE, lat);
        intent.putExtra(Constants.LONGITUDE, lng);

        GeoCordingService.enqueueWork(getApplicationContext(), GeoCordingService.class, 10, intent);
    }


    private void putMarkersWithResult(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(resultList.size() == 0){
                    Toast.makeText(MainActivity.this, "데이터가 없습니다. 다른 지역으로 조회해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    for (Marker marker : markerList) {
                        marker.remove();
                    }
                    markerList.clear();
                    Cursor cursor = manager.getAllInterBox();
                    int i;
                    for (i = 0; i < resultList.size(); i++) {
                        BoxDto dto = resultList.get(i);
                        markerOptions.title(dto.fcltyNm);
                        markerOptions.position(new LatLng(dto.latitude, dto.longitude));
                        Marker marker = mGoogleMap.addMarker(markerOptions);
                        marker.setTag(i);

                        for(int j = 0; cursor.moveToPosition(j); j++){ // 관심보관함에 저장된 보관함인지 확인
                            double lat = cursor.getDouble(cursor.getColumnIndex(DBHelper.COL_INTER_LAT));
                            double lng = cursor.getDouble(cursor.getColumnIndex(DBHelper.COL_INTER_LNG));
                            //Log.d(TAG, "관심보관함 (" + lat + ", " + lng + ") / 결과dto (" + dto.getLatitude() + ", " + dto.getLongitude() + ")");

                            if(lat == dto.getLatitude() && lng == dto.getLongitude()) {
                                //Log.d(TAG, "일치");
                                if(smallMarker == null){Log.d(TAG, "SmallMarker is null");}
                                else {Log.d(TAG, "smallMarker is not null");}
                                marker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                            }
                        }

                        markerList.add(marker);
                        Log.d(TAG, dto.latitude + ", " + dto.longitude + ", " + dto.fcltyNm);
                    }
                    progressDialog.cancel();
                    LatLng lastMark = new LatLng(resultList.get(0).latitude, resultList.get(0).longitude);
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastMark, 13)); // 데이터 로드 된 지역으로 카메라 이동

                    Toast.makeText(MainActivity.this, "데이터 로드 완료", Toast.LENGTH_SHORT).show();
                }

                progressDialog.cancel();
            }
        });
    }

    public void removeMarker(){
        for(Marker m : markerList){
            m.remove();
        }
    }

    private void startLocationUpdate() {
        if (checkPermission()) {
            Log.d(TAG, "위치 업데이트 요청 완료");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, locationListener);
        }
        requestingLocationUpdates = true;
    }

    private void stopLocationUpdate() {
        locationManager.removeUpdates(locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "onLocationChanged() 실행됨");
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void requirePerms(){
        String[] permissions = {Manifest.permission.RECEIVE_SMS};
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQ_CODE);
                Log.d(TAG, "checkPermission() false");
                return false;
            }
        }
        Log.d(TAG, "checkPermission() true");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQ_CODE) {
            if(grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG, "퍼미션 등록 완료");
                mapLoad();
                startLocationUpdate(); // 수락이 되면 실시간으로 내 위치 받아올 것
            }
            else{
                Log.d(TAG, "퍼미션 등록이 필요합니다");
            }
        }
    }

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }
        String addressOutput = null;
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                if (addressOutput == null) addressOutput = "";
                String [] array = addressOutput.split(" ");
                currentArea[0] = array[1]; // 시도명 저장
                currentArea[1] = array[2]; // 구 저장
                Log.d(TAG, addressOutput);
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);       // strings.xml 에 채널명 기록
            String description = getString(R.string.channel_description);       // strings.xml에 채널 설명 기록
            int importance = NotificationManager.IMPORTANCE_DEFAULT;    // 알림의 우선순위 지정
            NotificationChannel channel = new NotificationChannel(getString(R.string.CHANNEL_ID), name, importance);    // CHANNEL_ID 지정
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);  // 채널 생성
            notificationManager.createNotificationChannel(channel);
        }
    }
}



