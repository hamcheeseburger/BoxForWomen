package com.example.MA02_20170953;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    final static String TAG = "DBHelper";

    private final static String DB_NAME = "box_db";
    public final static String INTER_TABLE_NAME = "interest_box_table";
    public final static String COL_INTER_ID = "_id";
    public final static String COL_INTER_NAME = "name";
    public final static String COL_INTER_LOCATION = "location";
    public final static String COL_INTER_LAT = "lat";
    public final static String COL_INTER_LNG = "lng";
    public final static String COL_INTER_WEEKDAY_OPEN = "weekdayopen";
    public final static String COL_INTER_WEEKDAY_CLOSE = "weekdayclose";
    public final static String COL_INTER_SAT_OPEN = "satopen";
    public final static String COL_INTER_SAT_CLOSE = "satclose";
    public final static String COL_INTER_HOLI_OPEN = "holyopen";
    public final static String COL_INTER_HOLI_CLOSE = "holyclose";
    public final static String COL_INTER_FREETIMEUSE = "freetimeuse";

    public final static String PACKAGE_TABLE_NAME = "package_table";
    public final static String COL_PACKAGE_ID = "_id";
    public final static String COL_PACKAGE_NAME = "packageName";
    public final static String COL_PACKAGE_SHOP = "packageShop";
    public final static String COL_PACKAGE_ORDER_DATE = "packageOrderDate";
    public final static String COL_PACKAGE_RECEIVED_DATE = "packageReceivedDate";
    public final static String COL_PACKAGE_RECEIVED_BOX_ID = "boxId";
    public final static String COL_PACKAGE_RECEIVED_PASSWORD = "password";
    public final static String COL_PACKAGE_RECEIVED_PARTITION = "partition";
    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 관심보관함 테이블 생성
        String createSql = "create table " + INTER_TABLE_NAME + " ("+ COL_INTER_ID + " integer primary key autoincrement, "
                + COL_INTER_NAME + " TEXT, " + COL_INTER_LOCATION + " TEXT, " + COL_INTER_LAT + " double, " + COL_INTER_LNG + " double ,"
                + COL_INTER_WEEKDAY_OPEN + " TEXT, " + COL_INTER_WEEKDAY_CLOSE + " TEXT, " + COL_INTER_SAT_OPEN + " TEXT, " + COL_INTER_SAT_CLOSE + " TEXT, "
                + COL_INTER_HOLI_OPEN + " TEXT, " + COL_INTER_HOLI_CLOSE + " TEXT, " + COL_INTER_FREETIMEUSE + " TEXT);";
        Log.d(TAG, createSql);
        db.execSQL(createSql);

        // 나의 주문물품 테이블 생성
        String sql = "create table " + PACKAGE_TABLE_NAME + " (" + COL_PACKAGE_ID + " integer primary key autoincrement, "
                + COL_PACKAGE_NAME + " TEXT, " + COL_PACKAGE_SHOP + " TEXT, " + COL_PACKAGE_ORDER_DATE + " TEXT, " + COL_PACKAGE_RECEIVED_DATE + " TEXT, "
                + COL_PACKAGE_RECEIVED_BOX_ID + " int, " + COL_PACKAGE_RECEIVED_PASSWORD +" TEXT, " + COL_PACKAGE_RECEIVED_PARTITION + " int);";
        Log.d(TAG, sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + INTER_TABLE_NAME);
        onCreate(db);
    }
}
