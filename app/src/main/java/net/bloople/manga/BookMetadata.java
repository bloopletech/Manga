package net.bloople.manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by i on 2/01/2017.
 */

public class BookMetadata {
    private long _id = -1L;
    private long bookId;
    private long lastOpenedAt;
    private int lastReadPosition;

    public static BookMetadata findById(Context context, long id) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM books_metadata WHERE _id=?", new String[] { String.valueOf(id) });
        result.moveToFirst();

        if(result.getCount() > 0) {
            BookMetadata bookMetadata = new BookMetadata(result);
            result.close();
            return bookMetadata;
        }
        else {
            return null;
        }
    }

    public static BookMetadata findOrCreateByBookId(Context context, long bookId) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM books_metadata WHERE book_id=?", new String[] { String.valueOf(bookId) });
        result.moveToFirst();

        if(result.getCount() > 0) {
            BookMetadata bookMetadata = new BookMetadata(result);
            result.close();
            return bookMetadata;
        }
        else {
            BookMetadata bookMetadata = new BookMetadata();
            bookMetadata.bookId(bookId);
            bookMetadata.save(context);
            return bookMetadata;
        }
    }

    public BookMetadata() {
    }

    public BookMetadata(Cursor result) {
        _id = result.getLong(result.getColumnIndex("_id"));
        bookId = result.getLong(result.getColumnIndex("book_id"));
        lastOpenedAt = result.getLong(result.getColumnIndex("last_opened_at"));
        lastReadPosition = result.getInt(result.getColumnIndex("last_read_position"));
    }

    public long id() {
        return _id;
    }

    public long bookId() {
        return bookId;
    }

    public void bookId(long bookId) {
        this.bookId = bookId;
    }

    public long lastOpenedAt() {
        return lastOpenedAt;
    }

    public void lastOpenedAt(long lastOpenedAt) {
        this.lastOpenedAt = lastOpenedAt;
    }

    public int lastReadPosition() {
        return lastReadPosition;
    }

    public void lastReadPosition(int inLastReadPosition) {
        lastReadPosition = inLastReadPosition;
    }

    public void save(Context context) {
        ContentValues values = new ContentValues();
        values.put("book_id", bookId);
        values.put("last_opened_at", lastOpenedAt);
        values.put("last_read_position", lastReadPosition);

        SQLiteDatabase db = DatabaseHelper.instance(context);

        if(_id == -1L) {
            _id = db.insert("books_metadata", null, values);
        }
        else {
            db.update("books_metadata", values, "_id=?", new String[] { String.valueOf(_id) });
        }
    }
}
