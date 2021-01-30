package com.example.MA02_20170953;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SmsActivity extends AppCompatActivity {

    private final static String TAG = "SmsActivity";
    Cursor packageCursor;
    Cursor boxCursor;

    DBManager dbManager;

    String boxName;
    int partition;
    String password;
    String receivedDate;

    NetworkManager netManager;
    StringBuilder urlBuilder;
    BoxXmlParser parser;

    ArrayList<BoxDto> resultList;

    boolean dataAvailable = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        dbManager = new DBManager(this);

        TextView tv_message_name = findViewById(R.id.tv_message_name);
        TextView tv_message_partition = findViewById(R.id.tv_message_partition);
        TextView tv_message_password = findViewById(R.id.tv_message_password);
        TextView tv_message_received_date = findViewById(R.id.tv_message_received_date);
        Spinner packageSpinner = findViewById(R.id.message_package_spinner);

        Intent intent = getIntent();
        boxName = intent.getStringExtra("boxName");
        partition = intent.getIntExtra("partition", -1);
        password = intent.getStringExtra("password");
        receivedDate = intent.getStringExtra("date");

        // 저장한 물품 정보 가져오기
        ArrayList<String> packageArray = getPackageArray();
        ArrayAdapter spinnerAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, packageArray);
        packageSpinner.setAdapter(spinnerAdapter);

        packageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                packageCursor.moveToPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //textView에 집어넣기
        tv_message_name.setText(boxName);
        tv_message_partition.setText(String.valueOf(partition));
        tv_message_password.setText(password);
        tv_message_received_date.setText(receivedDate);

        // db에서 보관함 검색하기
        boxCursor = findBoxInfo(boxName);

        if(boxCursor.moveToNext()){
            Log.d(TAG, "DB에 보관함 정보 있음");

        }else {  // db에 정보가 없다면 네트워크 검색하여 보관함 정보를 가져오고, 다시 db에 저장, db id값 가져오기
            // 네트워크 관련 변수 초기화
            Log.d(TAG, "DB에 보관함 정보 없음");
            netManager = new NetworkManager(this);
            urlBuilder = new StringBuilder(getString(R.string.api_url)); /*URL*/
            parser = new BoxXmlParser();

            if(searchWithBoxName(boxName)){
                Log.d(TAG, "네트워킹 결과, 데이터 있음");

                if(dbManager.addInterBox(resultList.get(0))){
                    Log.d(TAG, "DB에 보관함 저장 완료");

                    boxCursor = findBoxInfo(boxName);

                    if(!boxCursor.moveToNext()){
                        Log.d(TAG, "[네트워킹 후 저장] DB에 보관함 정보 없음!!!!");
                    }
                }else {
                    Log.d(TAG, "DB에 보관함 저장 실패");
                }

            }else{
                Log.d(TAG, "네트워킹 결과, 데이터 없음");
            }

        }

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_message_save:
                int packageId = packageCursor.getInt(packageCursor.getColumnIndex(DBHelper.COL_PACKAGE_ID));
                int boxId = boxCursor.getInt(packageCursor.getColumnIndex(DBHelper.COL_INTER_ID));
                if(dbManager.setBoxToPackage(packageId, boxId, receivedDate, password, partition)){
                    Toast.makeText(this, "저장완료", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(this, "저장실패", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_message_cancel:
                finish();
                break;

        }

    }

    public boolean searchWithBoxName(String boxName){
        if(resultList != null) {
            resultList.clear();
        }
        try {
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + getString(R.string.api_key)); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("type","UTF-8") + "=" + URLEncoder.encode("xml", "UTF-8")); /*XML/JSON 여부*/
            urlBuilder.append("&" + URLEncoder.encode("fcltyNm","UTF-8") + "=" + URLEncoder.encode(boxName, "UTF-8")); // 보관함 이름으로 검색
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String address = urlBuilder.toString();

        new ThreadTask<String, ArrayList<BoxDto>>() {
            @Override
            protected void onPreExecute() {
                Log.d(TAG, "네트워킹 시작");
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
                if(resultList.size() > 0) { dataAvailable = true; }
                for(BoxDto dto : resultList){
                    info += dto.getFcltyNm() + "\n";
                }
                Log.d(TAG, info);
            }
        }.execute(address);

        if(dataAvailable) { return true; }
        return false;
    }

    public ArrayList<String> getPackageArray(){
        packageCursor = dbManager.getAllPackage();
        ArrayList<String> packageArray = new ArrayList<String>();
        packageArray.clear();
        while(packageCursor.moveToNext()){
            packageArray.add(packageCursor.getString(packageCursor.getColumnIndex(DBHelper.COL_PACKAGE_NAME)));
        }

        return packageArray;
    }

    public Cursor findBoxInfo(String boxName){
        return dbManager.findInterBoxWithName(boxName);
    }
}
