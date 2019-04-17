package net.bloople.manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.dslplatform.json.DslJson;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

class Library {
    private static final String DATA_JSON_PATH = "/data.json";

    private long _id = -1L;
    private String name;
    private String root;
    private HashMap<Long, Book> books = new HashMap<>();
    private String rootUri;

    static Library findById(Context context, long id) {
        SQLiteDatabase db = DatabaseHelper.instance(context);

        Cursor result = db.rawQuery("SELECT * FROM library_roots WHERE _id=?", new String[] { String.valueOf(id) });
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

        Cursor result = db.rawQuery("SELECT * FROM library_roots ORDER BY _id ASC LIMIT 1", new String[] {});
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
        name = result.getString(result.getColumnIndex("name"));
        root = result.getString(result.getColumnIndex("root"));
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

    String root() {
        return root;
    }

    void root(String root) {
        this.root = root;
    }

    String rootUri() {
        if(rootUri == null) rootUri = Uri.parse(root).toString();
        return rootUri;
    }

    String mangos() {
        return rootUri() + "/.mangos";
    }

    HashMap<Long, Book> books() {
        return books;
    }

    void inflate() throws IOException {
        URLConnection connection = new URL(mangos() + DATA_JSON_PATH).openConnection();

        DslJson<Object> dslJson = new DslJson<>();

        List<Book> books = dslJson.deserializeList(Book.class, connection.getInputStream());
        for(Book book : books) book.inflate(this);
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
