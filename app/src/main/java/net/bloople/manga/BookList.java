package net.bloople.manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by i on 29/12/2016.
 */

public class BookList {
    private long _id = -1L;
    private String name;

    public static BookList findById(Context context, long id) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM lists WHERE _id=?", new String[] { String.valueOf(id) });
        result.moveToFirst();

        if(result.getCount() > 0) {
            BookList bookList = new BookList(result);
            result.close();
            return bookList;
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

    public long id() {
        return _id;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public ArrayList<Long> bookIds(Context context) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT book_id FROM lists_books WHERE list_id=?", new String[] { String.valueOf(_id) });

        ArrayList<Long> bookIds = new ArrayList<>();
        while(result.moveToNext()) bookIds.add(result.getLong(result.getColumnIndex("book_id")));
        result.close();

        return bookIds;
    }

    public void bookIds(Context context, ArrayList<Long> bookIds) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        db.delete("lists_books", "list_id=?", new String[] { String.valueOf(_id) });

        for(long bookId : bookIds) {
            ContentValues values = new ContentValues();
            values.put("list_id", _id);
            values.put("book_id", bookId);

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

    public void destroy(Context context) {
        SQLiteDatabase db = DatabaseHelper.instance(context);
        db.delete("lists", "_id=?", new String[] { String.valueOf(_id) });
    }
}
