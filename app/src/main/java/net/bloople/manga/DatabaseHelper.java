package net.bloople.manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class DatabaseHelper {
    private static final String DB_NAME = "books";
    private static SQLiteDatabase instance;

    private static SQLiteDatabase obtainDatabase(Context context) {
        SQLiteDatabase db = context.getApplicationContext().openOrCreateDatabase(DB_NAME,
                Context.MODE_PRIVATE, null);

        loadSchema(db);

        return db;
    }

    private static void loadSchema(SQLiteDatabase db) {
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
                "book_id INTEGER" +
                ")");

        Cursor result = db.rawQuery("SELECT COUNT(*) FROM lists", new String[] {});
        result.moveToFirst();

        if(result.getInt(0) == 0) {
            ContentValues values = new ContentValues();
            values.put("name", "All Books");
            db.insert("lists", null, values);
        }
        result.close();

        db.execSQL("CREATE TABLE IF NOT EXISTS books_metadata ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "book_id INTEGER, " +
                "last_opened_at INTEGER DEFAULT 0, " +
                "last_read_position INTEGER DEFAULT 0" +
                ")");

        createLibraryRootsSchema(db);
    }

    static SQLiteDatabase instance(Context context) {
        if(instance == null) {
            instance = obtainDatabase(context);
        }

        return instance;
    }

    static void deleteDatabase(Context context) {
        context.getApplicationContext().deleteDatabase(DB_NAME);
        instance = null;
    }

    private static void createLibraryRootsSchema(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS library_roots ( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "root TEXT" +
                ")");

        boolean alreadyHasPosition = false;
        Cursor columns = db.rawQuery("PRAGMA table_info(library_roots)", null);

        while(columns.moveToNext()) {
            if(columns.getString(columns.getColumnIndex("name")).equals("position")) {
                alreadyHasPosition = true;
                break;
            }
        }

        columns.close();

        if(!alreadyHasPosition) {
            db.execSQL("ALTER TABLE library_roots ADD COLUMN position INTEGER");
            db.execSQL("UPDATE library_roots SET position=_id");
        }

        Cursor result = db.rawQuery("SELECT COUNT(*) FROM library_roots", new String[] {});
        result.moveToFirst();

        if(result.getInt(0) == 0) {
            ContentValues values = new ContentValues();
            values.put("name", "Manga");
            values.put("root", "http://192.168.1.100:9292/h/Manga-OG/");
            db.insert("library_roots", null, values);
        }
        result.close();
    }
}
