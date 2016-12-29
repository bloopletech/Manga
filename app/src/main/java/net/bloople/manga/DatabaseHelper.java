package net.bloople.manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by i on 19/10/2016.
 */

public class DatabaseHelper {
    public static final String DB_NAME = "books";
    private static SQLiteDatabase mInstance;

    public static SQLiteDatabase obtainDatabase(Context context) {
        SQLiteDatabase db = context.getApplicationContext().openOrCreateDatabase(DB_NAME,
                Context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS books ( " +
                "key TEXT PRIMARY KEY, " +
                "last_opened_at INTEGER DEFAULT 0" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS lists ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT" +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS lists_books ( " +
                "list_id INTEGER, " +
                "book_key TEXT" +
                ")");

        Cursor result = db.rawQuery("SELECT COUNT(*) FROM lists", new String[] {});
        result.moveToFirst();

        if(result.getInt(0) == 0) {
            ContentValues values = new ContentValues();
            values.put("name", "All Books");
            db.insert("lists", null, values);
        }
        result.close();

        return db;
    }

    public static SQLiteDatabase instance(Context context) {
        if(mInstance == null) {
            mInstance = obtainDatabase(context);
        }

        return mInstance;
    }

    public static void deleteDatabase(Context context) {
        context.getApplicationContext().deleteDatabase(DB_NAME);
        mInstance = null;
    }
}
