package com.example.MA02_20170953;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;

public class DBManager {
    final static String TAG = "DBManager";

    DBHelper helper;
    Context context;
    Cursor cursor;
    SimpleDateFormat dateFormat;

    DBManager(Context c){
        helper = new DBHelper(c);
        context = c;
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    }

    public Cursor findPackageByBoxId(int boxId){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = DBHelper.COL_PACKAGE_RECEIVED_BOX_ID + "=?";
        String [] selectArgs = new String [] {String.valueOf(boxId)};

        cursor = db.query(DBHelper.PACKAGE_TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        return cursor;
    }


    public Cursor findInterBoxById(int boxId) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = DBHelper.COL_INTER_ID + "=?";
        String [] selectArgs = new String [] {String.valueOf(boxId)};

        cursor = db.query(DBHelper.INTER_TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        return cursor;
    }

    public boolean findInterBoxWithLatLng(BoxDto dto){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = DBHelper.COL_INTER_LAT + "=? and " + DBHelper.COL_INTER_LNG + "=?";
        String [] selectArgs = new String [] {String.valueOf(dto.getLatitude()), String.valueOf(dto.getLongitude())};

        cursor = db.query(DBHelper.INTER_TABLE_NAME, null, selection, selectArgs, null, null, null, null);
        if(cursor.moveToNext()){
            Log.d(TAG, "내용있음");
            return true;
        }
        Log.d(TAG, "내용없음");
        return false;
    }

    public Cursor findInterBoxWithName(String name){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = DBHelper.COL_INTER_NAME + "=?";
        String [] selectArgs = new String [] {name};

        cursor = db.query(DBHelper.INTER_TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        return cursor;
    }

    public Cursor getAllInterBox(){
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.INTER_TABLE_NAME, null);

        //helper.close();
        return cursor;
    }

    public boolean addInterBox(BoxDto dto){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_INTER_NAME, dto.getFcltyNm());
        row.put(DBHelper.COL_INTER_LOCATION, dto.getLocation());
        row.put(DBHelper.COL_INTER_LAT, dto.getLatitude());
        row.put(DBHelper.COL_INTER_LNG, dto.getLongitude());
        row.put(DBHelper.COL_INTER_WEEKDAY_OPEN, dto.getWeekdayOperOpenHhmm());
        row.put(DBHelper.COL_INTER_WEEKDAY_CLOSE, dto.getWeekdayOperColseHhmm());
        row.put(DBHelper.COL_INTER_SAT_OPEN, dto.getSatOperOperOpenHhmm());
        row.put(DBHelper.COL_INTER_SAT_CLOSE, dto.getSatOperCloseHhmm());
        row.put(DBHelper.COL_INTER_HOLI_OPEN, dto.getHolidayOperOpenHhmm());
        row.put(DBHelper.COL_INTER_HOLI_CLOSE, dto.getHolidayCloseOpenHhmm());
        row.put(DBHelper.COL_INTER_FREETIMEUSE, dto.getFreeUseTime());

        if(db.insert(DBHelper.INTER_TABLE_NAME, null, row) < 0){
            Log.e(TAG, "addInterBox : insert error");
            helper.close();
            return false;
        }
        helper.close();
        return true;
    }

    public boolean removeInterBox(int id){
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereClause = DBHelper.COL_INTER_ID + "=?";
        String [] whereArgs = new String [] {String.valueOf(id)};

        if(db.delete(DBHelper.INTER_TABLE_NAME, whereClause, whereArgs) < 0){
            helper.close();
            return false;
        }
        helper.close();
        return true;
    }

    public Cursor getAllPackage(){
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBHelper.PACKAGE_TABLE_NAME, null);
        Log.d(TAG, "getAllPackage() 호출");
        //helper.close();
        return cursor;
    }

    public boolean addPackage(PackageDto dto){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PACKAGE_NAME, dto.getPackageName());
        row.put(DBHelper.COL_PACKAGE_SHOP, dto.getShop());
        row.put(DBHelper.COL_PACKAGE_ORDER_DATE, dto.getOrderDate());
        row.put(DBHelper.COL_PACKAGE_RECEIVED_BOX_ID, -1); // MyInterBox id (처음 생성할 때는 무조건 물품보관이 안됐을테니)

        if(db.insert(DBHelper.PACKAGE_TABLE_NAME, null, row) < 0){
            Log.e(TAG, "addPackage : insert error");
            helper.close();
            return false;
        }
        helper.close();
        return true;
    }


    public boolean addPackageWithMessage(PackageDto dto){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PACKAGE_NAME, dto.getPackageName());
        row.put(DBHelper.COL_PACKAGE_SHOP, dto.getShop());
        row.put(DBHelper.COL_PACKAGE_ORDER_DATE, dto.getOrderDate());
        row.put(DBHelper.COL_PACKAGE_RECEIVED_BOX_ID, dto.getIsReceived()); // MyInterBox id
        row.put(DBHelper.COL_PACKAGE_RECEIVED_PARTITION, dto.getPartition());
        row.put(DBHelper.COL_PACKAGE_RECEIVED_PASSWORD, dto.getPassword());
        row.put(DBHelper.COL_PACKAGE_RECEIVED_DATE, dto.getReceivedDate());

        if(db.insert(DBHelper.PACKAGE_TABLE_NAME, null, row) < 0){
            Log.e(TAG, "addPackageWithMessage : insert error");
            helper.close();
            return false;
        }
        helper.close();
        return true;

    }

    public boolean removePackage(int id){
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereClause = DBHelper.COL_PACKAGE_ID + "=?";
        String [] whereArgs = new String [] {String.valueOf(id)};

        if(db.delete(DBHelper.PACKAGE_TABLE_NAME, whereClause, whereArgs) < 0){
            helper.close();
            return false;
        }
        helper.close();
        return true;
    }

    public boolean setBoxToPackage(int packageId, int boxId, String receivedDate, String password, int partition){
        SQLiteDatabase db = helper.getWritableDatabase();

        String whereCaluse = DBHelper.COL_PACKAGE_ID + "=?";
        String [] whereArgs = new String [] {String.valueOf(packageId)};
        ContentValues row = new ContentValues();
        row.put(DBHelper.COL_PACKAGE_RECEIVED_BOX_ID, boxId);
        row.put(DBHelper.COL_PACKAGE_RECEIVED_DATE, receivedDate);
        row.put(DBHelper.COL_PACKAGE_RECEIVED_PASSWORD, password);
        row.put(DBHelper.COL_PACKAGE_RECEIVED_PARTITION, partition);

        if(db.update(DBHelper.PACKAGE_TABLE_NAME, row, whereCaluse, whereArgs) == 1){
            helper.close();
            return true;
        }
        helper.close();
        return false;
    }

    public Cursor getReceivedPackage(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = DBHelper.COL_PACKAGE_RECEIVED_BOX_ID + "!=?";
        String [] selectArgs = new String [] {"-1"};

        Cursor cursor = db.query(DBHelper.PACKAGE_TABLE_NAME, null, selection, selectArgs, null, null, null, null);

        return cursor;
    }
}
