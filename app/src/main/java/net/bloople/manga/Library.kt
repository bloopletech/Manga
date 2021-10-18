package net.bloople.manga

import kotlin.Throws
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.HashMap


class Library {
    private var _id = -1L
    private var name: String? = null
    private var position = 0
    private var root: String? = null
    private var username: String? = null
    private var password: String? = null
    private val books = HashMap<Long, Book>()

    internal constructor() {}
    internal constructor(result: Cursor) {
        _id = result.getLong(result.getColumnIndex("_id"))
        name = result.getString(result.getColumnIndex("name"))
        position = result.getInt(result.getColumnIndex("position"))
        root(result.getString(result.getColumnIndex("root")))
        username = result.getString(result.getColumnIndex("username"))
        password = result.getString(result.getColumnIndex("password"))
    }

    fun id(): Long {
        return _id
    }

    fun name(): String? {
        return name
    }

    fun name(name: String?) {
        this.name = name
    }

    fun position(): Int {
        return position
    }

    fun position(position: Int) {
        this.position = position
    }

    fun root(): String? {
        return root
    }

    fun root(root: String?) {
        this.root = Uri.parse(root).toString()
    }

    fun username(): String? {
        return username
    }

    fun username(username: String?) {
        this.username = username
    }

    fun password(): String? {
        return password
    }

    fun password(password: String?) {
        this.password = password
    }

    fun rootUrl(): MangosUrl {
        return MangosUrl(root, username, password)
    }

    fun mangos(): MangosUrl {
        return rootUrl().withAppendedPath("/.mangos")
    }

    fun books(): HashMap<Long, Book> {
        return books
    }

    @ExperimentalSerializationApi
    @Throws(IOException::class)
    fun inflate() {
        val connection = mangos().withAppendedPath(DATA_JSON_PATH).toUrlConnection()

        val books: List<Book>;
        connection.getInputStream().use { books = Json.decodeFromStream(it) }

        for(book in books) book.inflate(this)
    }

    fun save(context: Context?) {
        val values = ContentValues()
        values.put("name", name)
        values.put("position", position)
        values.put("root", root)
        values.put("username", username)
        values.put("password", password)
        val db = DatabaseHelper.instance(context)
        if(_id == -1L) {
            _id = db.insert("library_roots", null, values)
        }
        else {
            db.update("library_roots", values, "_id=?", arrayOf(_id.toString()))
        }
    }

    fun destroy(context: Context?) {
        val db = DatabaseHelper.instance(context)
        db.delete("library_roots", "_id=?", arrayOf(_id.toString()))
    }

    @Throws(JSONException::class)
    fun toJSON(): JSONObject {
        val result = JSONObject()
        result.put("id", _id)
        result.put("name", name)
        result.put("position", position)
        result.put("root", root)
        return result
    }

    companion object {
        private const val DATA_JSON_PATH = "/data.json"
        @JvmStatic
        fun findById(context: Context?, id: Long): Library? {
            val db = DatabaseHelper.instance(context)
            val result = db.rawQuery("SELECT * FROM library_roots WHERE _id=?", arrayOf(id.toString()))
            result.moveToFirst()
            return if(result.count > 0) {
                val library = Library(result)
                result.close()
                library
            }
            else {
                null
            }
        }

        @JvmStatic
        fun findDefault(context: Context?): Library? {
            val db = DatabaseHelper.instance(context)
            val result = db.rawQuery("SELECT * FROM library_roots ORDER BY position ASC LIMIT 1", arrayOf())
            result.moveToFirst()
            return if(result.count > 0) {
                val library = Library(result)
                result.close()
                library
            }
            else {
                null
            }
        }

        @JvmStatic
        fun findHighestPosition(context: Context?): Int {
            val db = DatabaseHelper.instance(context)
            val result = db.rawQuery("SELECT position FROM library_roots ORDER BY position DESC LIMIT 1", arrayOf())
            result.moveToFirst()
            return if(result.count > 0) {
                val position = result.getInt(result.getColumnIndex("position"))
                result.close()
                position
            }
            else {
                0
            }
        }
    }
}