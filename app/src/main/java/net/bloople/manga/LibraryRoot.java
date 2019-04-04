package net.bloople.manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class LibraryRoot {
    private long _id = -1L;
    private String name;
    private String root;

    static LibraryRoot findById(Context context, long id) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM library_roots WHERE _id=?", new String[] { String.valueOf(id) });
        result.moveToFirst();

        if(result.getCount() > 0) {
            LibraryRoot libraryRoot = new LibraryRoot(result);
            result.close();
            return libraryRoot;
        }
        else {
            return null;
        }
    }

    static LibraryRoot findDefault(Context context) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM library_roots ORDER BY _id ASC LIMIT 1", new String[] {});
        result.moveToFirst();

        if(result.getCount() > 0) {
            LibraryRoot libraryRoot = new LibraryRoot(result);
            result.close();
            return libraryRoot;
        }
        else {
            return null;
        }
    }

    LibraryRoot() {
    }

    LibraryRoot(Cursor result) {
        _id = result.getLong(result.getColumnIndex("_id"));
        name = result.getString(result.getColumnIndex("name"));
        root = result.getString(result.getColumnIndex("root"));
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

    public String root() {
        return root;
    }

    public void root(String root) {
        this.root = root;
    }

    void save(Context context) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("root", root);

        SQLiteDatabase db = DatabaseHelper.instance(context);

        if(_id == -1L) {
            _id = db.insert("library_roots", null, values);
        }
        else {
            db.update("library_roots", values, "_id=?", new String[] { String.valueOf(_id) });
        }
    }

    void destroy(Context context) {
        SQLiteDatabase db = DatabaseHelper.instance(context);
        db.delete("library_roots", "_id=?", new String[] { String.valueOf(_id) });
    }
}
