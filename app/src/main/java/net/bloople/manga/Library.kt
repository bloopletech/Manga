package net.bloople.manga

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import java.util.HashMap

class Library {
    var id = -1L
    var name: String? = null
    var position = 0
    var root: String? = null
        set(value) { field = Uri.parse(value).toString() }
    var username: String? = null
    var password: String? = null
    val books = HashMap<Long, Book>()

    val rootUrl: MangosUrl by lazy { MangosUrl(root!!, username, password) }
    val mangos: MangosUrl by lazy { rootUrl / ".mangos" }
    val dataUrl: MangosUrl by lazy { mangos / DATA_JSON_PATH }
    val thumbnailsUrl: MangosUrl by lazy { mangos / "img" / "thumbnails" }

    internal constructor()
    internal constructor(result: Cursor) {
        id = result["_id"]
        name = result["name"]
        position = result["position"]
        root = result["root"]
        username = result["username"]
        password = result["password"]
    }

    fun inflate(deflatedBooks: List<Book>) {
        books.clear()
        books.putAll(deflatedBooks.associateBy { it.id })
        for(book in deflatedBooks) book.library = this
    }

    fun save() {
        val values = ContentValues()
        values.put("name", name)
        values.put("position", position)
        values.put("root", root)
        values.put("username", username)
        values.put("password", password)
        val db = DatabaseHelper.instance()
        if(id == -1L) {
            id = db.insert("library_roots", null, values)
        }
        else {
            db.update("library_roots", values, "_id=?", arrayOf(id.toString()))
        }
    }

    fun destroy() {
        val db = DatabaseHelper.instance()
        db.delete("library_roots", "_id=?", arrayOf(id.toString()))
    }

    companion object {
        private const val DATA_JSON_PATH = "data.json"
        @JvmStatic
        fun findById(id: Long): Library? {
            val db = DatabaseHelper.instance()
            db.rawQuery("SELECT * FROM library_roots WHERE _id=?", arrayOf(id.toString())).use {
                it.moveToFirst()
                return if (it.count > 0) Library(it) else null
            }
        }

        @JvmStatic
        fun findDefault(): Library? {
            val db = DatabaseHelper.instance()
            db.rawQuery("SELECT * FROM library_roots ORDER BY position ASC LIMIT 1", arrayOf()).use {
                it.moveToFirst()
                return if (it.count > 0) Library(it) else null
            }
        }

        @JvmStatic
        fun findHighestPosition(): Int {
            val db = DatabaseHelper.instance()
            db.rawQuery("SELECT position FROM library_roots ORDER BY position DESC LIMIT 1", arrayOf()).use {
                it.moveToFirst()
                return if (it.count > 0) it["position"] else 0
            }
        }
    }
}