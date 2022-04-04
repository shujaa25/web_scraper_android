package com.ishujaa.scraper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBC {
    private Context context;
    public String TARGET_TABLE_NAME = "target_table";
    public String LOG_TABLE_NAME = "log_table";
    SQLiteOpenHelper sqLiteOpenHelper;
    DBC(Context context){
        this.context = context;
        sqLiteOpenHelper = new DBHelper(context);
    }

    public String readTargetData(int id) throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(TARGET_TABLE_NAME, new String[]{"data"},
                "_id=?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        String data = cursor.getString(0);
        cursor.close();
        database.close();
        return data;
    }

    public void updateTargetData(int id, String data) throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        ContentValues targetValues = new ContentValues();
        targetValues.put("data", data);
        database.update("target_table", targetValues, "_id=?",
                new String[]{String.valueOf(id)});
        database.close();
    }

    public ArrayList<Target> retrieveAllTargets() throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(TARGET_TABLE_NAME,
                new String[]{"_id, name, url, primary_selector," +
                        "secondary_selector, group_selector," +
                        "sleep, data"}, null, null,
                null, null, null);
        cursor.moveToFirst();

        ArrayList<Target> targets = new ArrayList<>();

        do{
            Target target = new Target();
            target.setId(Integer.parseInt(cursor.getString(0)));
            target.setName(cursor.getString(1));
            target.setUrl(cursor.getString(2));
            target.setPrimarySelector(cursor.getString(3));
            target.setSecondarySelector(cursor.getString(4));
            target.setGroupSelector(cursor.getString(5));
            target.setSleepDuration(Integer.parseInt(cursor.getString(6)));
            target.setCurrentData(cursor.getString(7));
            targets.add(target);
        }while (cursor.moveToNext());
        cursor.close();
        database.close();
        return targets;
    }

    public void putLog(String label, String text) throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("dd/mm/yy HH:mm:ss");
        Date date = new Date();
        String time = format.format(date);
        Log.i("DBC", "beginng txn");
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

        ContentValues targetValues = new ContentValues();
        targetValues.put("label", label);
        targetValues.put("text", text);
        targetValues.put("time", time);
        database.insert(LOG_TABLE_NAME, null, targetValues);

        Log.i("DBC", "log insrtef.");
        database.close();
    }

    public StringBuilder getLogs() throws Exception{
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.query(LOG_TABLE_NAME, new String[]{"label, text, time"},
                null, null, null, null, null);
        cursor.moveToFirst();
        StringBuilder stringBuilder = new StringBuilder("NO LOGS YET");
        while (cursor.moveToNext()){
            stringBuilder.append(cursor.getString(0) + ": ");
            stringBuilder.append(cursor.getString(1) + " ");
            stringBuilder.append(cursor.getString(2));
            stringBuilder.append("\n");
        }

        cursor.close();
        database.close();
        return stringBuilder;
    }
    public void clearLogs(){
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.delete(LOG_TABLE_NAME, null, null);
        database.close();
    }
}
