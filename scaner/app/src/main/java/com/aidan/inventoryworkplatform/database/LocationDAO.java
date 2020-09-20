package com.aidan.inventoryworkplatform.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.aidan.inventoryworkplatform.Entity.Location;
import com.aidan.inventoryworkplatform.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aidan on 2017/2/21.
 */


public class LocationDAO {
    // 表格名稱
    public static final String TAG = "LocationDAO";
    public static final String TABLE_NAME = "Location";

    // 編號表格欄位名稱，固定不變
    public static final String KeyID = "id";

    // 其它表格欄位名稱
    public static final String content = "content";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KeyID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    content + " TEXT NOT NULL)";
    private SQLiteDatabase db;
    private static LocationDAO locationDAO;

    public static void init(Context context) {
        Singleton.log("LocationDAO init");
        locationDAO = new LocationDAO(context);
    }

    public static LocationDAO getInstance() {
        if (locationDAO == null) return null;
        return locationDAO;
    }

    private LocationDAO(Context context) {
        db = DBHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public Location insert(Location location) {
        return insert(location, TABLE_NAME);
    }

    public Location insert(Location location, String tableName) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();
        cv.put(content, location.toJSON().toString());

        long id = db.insert(tableName, null, cv);

        // 設定編號
        location.setId(id);
        // 回傳結果
        return location;
    }

    // 修改參數指定的物件
    public boolean update(Location location) {
        return update(location, TABLE_NAME);
    }

    public boolean update(Location location, String tableName) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(content, location.toJSON().toString());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KeyID + "=" + location.getId();
        long test = db.update(tableName, cv, where, null);
        // 執行修改資料並回傳修改的資料數量是否成功
        Log.e(TAG, test + "");
        return test > 0;
    }

    public boolean delete(long id) {
        return delete(id, TABLE_NAME);
    }

    public boolean delete(long id, String tableNmae) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KeyID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(tableNmae, where, null) > 0;
    }

    public void removeAll() {
        removeAll(TABLE_NAME);
    }

    public void removeAll(String tableName) {
        db.delete(tableName, null, null);
    }

    public List<Location> getAll() {
        return getAll(TABLE_NAME);
    }

    public List<Location> getAll(String tableName) {
        List<Location> result = new ArrayList<>();
        Cursor cursor = db.query(
                tableName, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public Location get(long id) {

        return get(id, TABLE_NAME);
    }

    public Location get(long id, String tableName) {
        // 準備回傳結果用的物件
        Location location = null;
        // 使用編號為查詢條件
        String where = KeyID + "=" + id;
        // 執行查詢
        Cursor result = db.query(
                tableName, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            location = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return location;
    }

    // 把Cursor目前的資料包裝為物件
    public static Location getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        Location result = new Location();

        result.setId(cursor.getLong(0));
        result.setData(cursor.getString(1));
        // 回傳結果
        return result;
    }
    public void dropTable(){
        db.execSQL("DROP TABLE IF EXISTS " + LocationDAO.TABLE_NAME);
        db.execSQL(LocationDAO.CREATE_TABLE);
    }
}

