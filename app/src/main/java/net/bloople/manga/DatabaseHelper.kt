package net.bloople.manga

import android.database.sqlite.SQLiteDatabase
import android.content.ContentValues
import android.content.Context
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.jvm.Synchronized
import kotlin.Throws

internal object DatabaseHelper {
    private const val DB_NAME = "books"
    private lateinit var database: SQLiteDatabase

    private fun obtainDatabase(): SQLiteDatabase {
        val db = MangaApplication.context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null)
        loadSchema(db)
        return db
    }

    private fun loadSchema(db: SQLiteDatabase) {
        db.execSQL(
        "CREATE TABLE IF NOT EXISTS books ( " +
            "key TEXT PRIMARY KEY, " +
            "last_opened_at INTEGER DEFAULT 0" +
            ")"
        )

        createBooksMetadataSchema(db)

        createLibraryRootsSchema(db)

        createQueriesSchema(db)
    }

    @Synchronized
    fun instance(): SQLiteDatabase {
        if (!::database.isInitialized) {
            database = obtainDatabase()
        }
        return database
    }

    @Synchronized
    fun deleteDatabase() {
        MangaApplication.context.deleteDatabase(DB_NAME)
        database = obtainDatabase()
    }

    @Synchronized
    @Throws(IOException::class)
    fun exportDatabase(outputStream: OutputStream) {
        val path = instance().use { it.path }
        database = obtainDatabase()

        var inputStream: InputStream? = null
        try {
            inputStream = FileInputStream(path)
            val buffer = ByteArray(1024)
            var length: Int
            while(inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        }
        finally {
            inputStream?.close()
            outputStream.close()
        }
    }

    @Synchronized
    @Throws(IOException::class)
    fun importDatabase(inputStream: InputStream) {
        val path = instance().use { it.path }
        database = obtainDatabase()

        var outputStream: OutputStream? = null
        try {
            outputStream = FileOutputStream(path)
            val buffer = ByteArray(1024)
            var length: Int
            while(inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        }
        finally {
            inputStream.close()
            outputStream?.close()
        }
    }

    private fun createBooksMetadataSchema(db: SQLiteDatabase) {
        db.execSQL(
        "CREATE TABLE IF NOT EXISTS books_metadata ( " +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "book_id INTEGER, " +
            "last_opened_at INTEGER DEFAULT 0, " +
            "last_read_position INTEGER DEFAULT 0" +
            ")"
        )

        if(!hasColumn(db, "books_metadata", "opened_count")) {
            db.execSQL("ALTER TABLE books_metadata ADD COLUMN opened_count INTEGER")
            db.execSQL("UPDATE books_metadata SET opened_count=0")
        }
    }

    private fun createLibraryRootsSchema(db: SQLiteDatabase) {
        db.execSQL(
        "CREATE TABLE IF NOT EXISTS library_roots ( " +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT, " +
            "root TEXT" +
            ")"
        )

        if(!hasColumn(db, "library_roots", "position")) {
            db.execSQL("ALTER TABLE library_roots ADD COLUMN position INTEGER")
            db.execSQL("UPDATE library_roots SET position=_id")
        }

        val result = db.rawQuery("SELECT COUNT(*) FROM library_roots", arrayOf())
        result.moveToFirst()

        if(!hasColumn(db, "library_roots", "username")) {
            db.execSQL("ALTER TABLE library_roots ADD COLUMN username TEXT")
            db.execSQL("ALTER TABLE library_roots ADD COLUMN password TEXT")
        }

        if(result.getInt(0) == 0) {
            val values = ContentValues()
            values.put("name", "Manga")
            values.put("root", "http://192.168.1.100:9292/h/Manga-OG/")
            db.insert("library_roots", null, values)
        }
        result.close()
    }

    private fun createQueriesSchema(db: SQLiteDatabase) {
        db.execSQL(
        "CREATE TABLE IF NOT EXISTS queries ( " +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "\"text\" TEXT, " +
            "created_at INTEGER, " +
            "last_used_at INTEGER, " +
            "used_count INTEGER" +
            ")"
        )
    }

    private fun hasColumn(db: SQLiteDatabase, tableName: String, columnName: String): Boolean {
        db.rawQuery("PRAGMA table_info($tableName)", null).use {
            while (it.moveToNext()) {
                if (it.get<String>("name") == columnName) {
                    return true
                }
            }
        }
        return false
    }
}