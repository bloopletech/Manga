package net.bloople.manga.audit

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import net.bloople.manga.get
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.jvm.Synchronized
import kotlin.Throws

object DatabaseHelper {
    private const val DB_NAME = "audit"
    private lateinit var database: SQLiteDatabase

    private fun obtainDatabase(context: Context): SQLiteDatabase {
        val db = context.applicationContext.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null)
        loadSchema(db)
        return db
    }

    private fun loadSchema(db: SQLiteDatabase) {
        db.execSQL(
        "CREATE TABLE IF NOT EXISTS audit_events ( " +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "\"when\" INTEGER, " +
            "\"action\" TEXT, " +
            "resource_type TEXT, " +
            "resource_id INTEGER, " +
            "resource_name TEXT, " +
            "detail TEXT" +
            ")"
        )

        var alreadyHasResourceContext = false

        db.rawQuery("PRAGMA table_info(audit_events)", null).use {
            it.moveToFirst()
            while(it.moveToNext()) {
                if(it.get<String>("name") == "resource_context_type") {
                    alreadyHasResourceContext = true
                    break
                }
            }
        }

        if(!alreadyHasResourceContext) {
            db.execSQL("ALTER TABLE audit_events ADD COLUMN resource_context_type TEXT")
            db.execSQL("ALTER TABLE audit_events ADD COLUMN resource_context_id INTEGER")
        }
    }

    @Synchronized
    fun instance(context: Context): SQLiteDatabase {
        if (!::database.isInitialized) {
            database = obtainDatabase(context)
        }
        return database
    }

    @Synchronized
    fun deleteDatabase(context: Context) {
        context.applicationContext.deleteDatabase(DB_NAME)
        database = obtainDatabase(context)
    }

    @Synchronized
    @Throws(IOException::class)
    fun exportDatabase(context: Context, outputStream: OutputStream) {
        val path = instance(context).use { it.path }
        database = obtainDatabase(context)

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
    fun importDatabase(context: Context, inputStream: InputStream) {
        val path = instance(context).use { it.path }
        database = obtainDatabase(context)

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
}