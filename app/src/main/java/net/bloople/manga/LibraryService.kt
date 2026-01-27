package net.bloople.manga

import net.bloople.manga.Library.Companion.findDefault
import android.app.ProgressDialog
import android.content.Context
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import net.bloople.manga.Library.Companion.find
import okhttp3.OkHttpClient
import java.io.IOException

object LibraryService {
    private const val LIBRARY_CACHE_MAX_COUNT = 5
    private val currentLibraries = LruCache<Long, Library>(LIBRARY_CACHE_MAX_COUNT)
    private val okHttpClient = OkHttpClient()

    suspend fun ensureLibrary(context: Context, libraryId: Long): Library? {
        val library = if(libraryId != -1L) find(libraryId) else findDefault()
        if(library == null) return null

        val current = currentLibraries[library.id]
        if(current != null && current.root == library.root) return current

        if(load(context, library)) {
            currentLibraries.put(library.id, library)
            return library
        }

        return null
    }

    private suspend fun load(context: Context, library: Library): Boolean {
        val loadingLibraryDialog = ProgressDialog.show(
            context,
            "Loading " + library.name,
            "Please wait while the library is loaded...",
            true
        )

        return inflate(library).also { loadingLibraryDialog.dismiss() }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun inflate(library: Library): Boolean {
        return try {
            inflateUnchecked(library)
            true
        }
        catch(e: Exception) {
            e.printStackTrace()
            false
        }
    }

    @ExperimentalSerializationApi
    private suspend fun inflateUnchecked(library: Library) {
        withContext(Dispatchers.IO) {
            val books: List<Book>
            val request = library.dataUrl.toOkHttpRequest()

            okHttpClient.newCall(request).execute().use {
                if(!it.isSuccessful) throw IOException("Request failed. Request: $request, Response: $it")
                books = Json.decodeFromStream(it.body!!.byteStream())
            }

            library.inflate(books)
        }
    }
}