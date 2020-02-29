package com.example.finanalysis;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FinDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "findatabase"; // Имя базы данных
    private static final int DB_VERSION = 1; // Версия базы данных
    private static final String TABLE_CHECK = "CHECKTABLE";
    private static final String TABLE_CATEGORY = "CATEGORY";

    FinDatabaseHelper(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+ TABLE_CHECK + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "NAME TEXT, "
                + "DESCRIPTION TEXT,"
                + "CATEGORY TEXT,"
                + "DATE TEXT,"
                + "EXPENSEREVENUE FLOAT,"
                + "UNIQUE(NAME,DESCRIPTION,DATE,EXPENSEREVENUE) ON CONFLICT IGNORE);");

        /*
        db.execSQL("CREATE TABLE "+ TABLE_CATEGORY + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "NAME TEXT UNIQUE);");*/
    }

    //<solid android:color="#FF66BB64"/>

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVesion)
    {

    }
}
