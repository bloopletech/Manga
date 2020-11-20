package net.bloople.manga.audit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

class DatabaseHelper {
    private static final String DB_NAME = "audit";
    private static SQLiteDatabase instance;

    private static SQLiteDatabase obtainDatabase(Context context) {
        SQLiteDatabase db = context.getApplicationContext().openOrCreateDatabase(DB_NAME,
                Context.MODE_PRIVATE, null);

        loadSchema(db);

        return db;
    }

    private static void loadSchema(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS audit_events ( " +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "\"when\" INTEGER, " +
            "\"action\" TEXT, " +
            "resource_type TEXT, " +
            "resource_id INTEGER, " +
            "resource_name TEXT, " +
            "detail TEXT" +
            ")");

        boolean alreadyHasResourceContext = false;
        Cursor columns = db.rawQuery("PRAGMA table_info(audit_events)", null);

        while(columns.moveToNext()) {
            if(columns.getString(columns.getColumnIndex("name")).equals("resource_context_type")) {
                alreadyHasResourceContext = true;
                break;
            }
        }

        columns.close();

        if(!alreadyHasResourceContext) {
            db.execSQL("ALTER TABLE audit_events ADD COLUMN resource_context_type TEXT");
            db.execSQL("ALTER TABLE audit_events ADD COLUMN resource_context_id INTEGER");
        }
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
}
