package usst.edu.cn.sharebooks.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Cheng on 2017/5/24.
 */

public class RecordSQLiteOpenHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "temp.db";
    private static Integer DATABASE_VERSION = 1;
    private static final String TABLE_NAME="records";

    public RecordSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS "+TABLE_NAME+"(id integer primary key autoincrement,name varchar(200))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
