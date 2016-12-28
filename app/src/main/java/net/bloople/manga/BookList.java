package net.bloople.manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import static android.R.attr.path;

/**
 * Created by i on 29/12/2016.
 */

public class BookList {
    private long _id = -1L;
    private String name;

    public static ArrayList<BookList> all(Context context) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM lists", new String[] {});

        if(result.getCount() > 0) {
            ArrayList<BookList> lists = new ArrayList<>();

            while(result.moveToNext()) {
                lists.add(new BookList(result));
            }

            result.close();
            return lists;
        }
        else {
            return null;
        }
    }

    public BookList() {
    }

    public BookList(Cursor result) {
        _id = result.getLong(result.getColumnIndex("_id"));
        name = result.getString(result.getColumnIndex("name"));
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public ArrayList<String> bookKeys(Context context) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM lists_books WHERE list_id=?", new String[] { String.valueOf(_id) });

        if(result.getCount() > 0) {
            ArrayList<String> keys = new ArrayList<>();

            while(result.moveToNext()) {
                keys.add(result.getString(result.getColumnIndex("book_key")));
            }

            result.close();
            return keys;
        }
        else {
            return null;
        }
    }

    public void bookKeys(Context context, ArrayList<String> keys) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        db.rawQuery("DELETE FROM lists_books WHERE list_id=?", new String[] { String.valueOf(_id) });

        for(String key : keys) {
            ContentValues values = new ContentValues();
            values.put("list_id", _id);
            values.put("book_key", key);

            db.insert("lists_books", null, values);
        }
    }

    public void save(Context context) {
        ContentValues values = new ContentValues();
        values.put("name", name);

        SQLiteDatabase db = DatabaseHelper.instance(context);

        if(_id == -1L) {
            _id = db.insert("lists", null, values);
        }
        else {
            db.update("lists", values, "_id=?", new String[] { String.valueOf(_id) });
        }
    }
}