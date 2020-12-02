package net.bloople.manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Query {
    private long _id = -1L;
    private String text;
    private long createdAt;
    private long lastUsedAt;
    private long usedCount;

    static Query findById(Context context, long id) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM queries WHERE _id=?", new String[] { String.valueOf(id) });
        result.moveToFirst();

        if(result.getCount() > 0) {
            Query query = new Query(result);
            result.close();
            return query;
        }
        else {
            return null;
        }
    }

    static Query findByText(Context context, String text) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM queries WHERE \"text\"=?", new String[] { text });
        result.moveToFirst();

        if(result.getCount() > 0) {
            Query query = new Query(result);
            result.close();
            return query;
        }
        else {
            return null;
        }
    }

    Query() {
    }

    Query(
        String text,
        long createdAt,
        long lastUsedAt,
        long usedCount
    ) {
        this.text = text;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
        this.usedCount = usedCount;
    }

    Query(Cursor result) {
        _id = result.getLong(result.getColumnIndex("_id"));
        text = result.getString(result.getColumnIndex("text"));
        createdAt = result.getLong(result.getColumnIndex("created_at"));
        lastUsedAt = result.getLong(result.getColumnIndex("last_used_at"));
        usedCount = result.getLong(result.getColumnIndex("used_count"));
    }

    long id() {
        return _id;
    }

    String text() {
        return text;
    }

    void text(String text) {
        this.text = text;
    }

    long createdAt() {
        return createdAt;
    }

    void createdAt(long createdAt) {
        this.createdAt = createdAt;
    }

    long lastUsedAt() {
        return lastUsedAt;
    }

    void lastUsedAt(long lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    long usedCount() {
        return usedCount;
    }

    void usedCount(long usedCount) {
        this.usedCount = usedCount;
    }

    void save(Context context) {
        ContentValues values = new ContentValues();
        values.put("\"text\"", text);
        values.put("created_at", createdAt);
        values.put("last_used_at", lastUsedAt);
        values.put("used_count", usedCount);

        SQLiteDatabase db = DatabaseHelper.instance(context);

        if(_id == -1L) {
            _id = db.insertOrThrow("queries", null, values);
        }
        else {
            db.update("queries", values, "_id=?", new String[] { String.valueOf(_id) });
        }
    }
}
