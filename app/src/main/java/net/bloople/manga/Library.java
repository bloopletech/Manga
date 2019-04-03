package net.bloople.manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

class Library {
    private long _id = -1L;
    private String root;

    static Library findById(Context context, long id) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM libraries WHERE _id=?", new String[] { String.valueOf(id) });
        result.moveToFirst();

        if(result.getCount() > 0) {
            Library library = new Library(result);
            result.close();
            return library;
        }
        else {
            return null;
        }
    }

    static Library findDefault(Context context) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM libraries ORDER BY _id ASC LIMIT 1", new String[] {});
        result.moveToFirst();

        if(result.getCount() > 0) {
            Library library = new Library(result);
            result.close();
            return library;
        }
        else {
            return null;
        }
    }

    Library() {
    }

    Library(Cursor result) {
        _id = result.getLong(result.getColumnIndex("_id"));
        root = result.getString(result.getColumnIndex("root"));
    }

    public long id() {
        return _id;
    }

    public String root() {
        return root;
    }

    public void root(String root) {
        this.root = root;
    }

    void save(Context context) {
        ContentValues values = new ContentValues();
        values.put("root", root);

        SQLiteDatabase db = DatabaseHelper.instance(context);

        if(_id == -1L) {
            _id = db.insert("libraries", null, values);
        }
        else {
            db.update("libraries", values, "_id=?", new String[] { String.valueOf(_id) });
        }
    }

    void destroy(Context context) {
        SQLiteDatabase db = DatabaseHelper.instance(context);
        db.delete("libraries", "_id=?", new String[] { String.valueOf(_id) });
    }
}
