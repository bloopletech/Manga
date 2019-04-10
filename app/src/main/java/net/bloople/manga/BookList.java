package net.bloople.manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

class BookList {
    private long _id = -1L;
    private String name;

    static BookList findById(Context context, long id) {
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

    BookList() {
    }

    BookList(Cursor result) {
        _id = result.getLong(result.getColumnIndex("_id"));
        name = result.getString(result.getColumnIndex("name"));
    }

    long id() {
        return _id;
    }

    String name() {
        return name;
    }

    void name(String name) {
        this.name = name;
    }

    ArrayList<Long> bookIds(Context context) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT book_id FROM lists_books WHERE list_id=?", new String[] { String.valueOf(_id) });

        ArrayList<Long> bookIds = new ArrayList<>();
        while(result.moveToNext()) bookIds.add(result.getLong(result.getColumnIndex("book_id")));
        result.close();

        return bookIds;
    }

    void bookIds(Context context, ArrayList<Long> bookIds) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        db.delete("lists_books", "list_id=?", new String[] { String.valueOf(_id) });

        for(long bookId : bookIds) {
            ContentValues values = new ContentValues();
            values.put("list_id", _id);
            values.put("book_id", bookId);

            db.insert("lists_books", null, values);
        }
    }

    void save(Context context) {
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

    void destroy(Context context) {
        SQLiteDatabase db = DatabaseHelper.instance(context);
        db.delete("lists", "_id=?", new String[] { String.valueOf(_id) });
    }
}
