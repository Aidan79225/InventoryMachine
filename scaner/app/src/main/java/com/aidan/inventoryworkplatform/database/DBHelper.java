package com.aidan.inventoryworkplatform.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Aidan on 2017/2/21.
 */

public class DBHelper extends SQLiteOpenHelper {
    private final static int DBVersion = 3; //<-- 版本
    private final static String DBName = "inventory.db";  //<-- db name
    private static SQLiteDatabase database;

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            synchronized (DBHelper.class){
                if (database == null || !database.isOpen()){
                    database = new DBHelper(context, DBName,
                            null, DBVersion).getWritableDatabase();
                }
            }

        }

        return database;
    }

    public static SQLiteDatabase getDatabase() {
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ItemDAO.CREATE_TABLE);
        db.execSQL(DepartmentDAO.CREATE_TABLE);
        db.execSQL(AgentDAO.CREATE_TABLE);
        db.execSQL(LocationDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DepartmentDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AgentDAO.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocationDAO.TABLE_NAME);
        onCreate(db);
    }
}
