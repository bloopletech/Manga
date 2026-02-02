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
import okhttp3.Request
import java.io.IOException

object LibraryService {
    private const val LIBRARY_CACHE_MAX_COUNT = 5
    private val currentLibraries = LruCache<Long, Library>(LIBRARY_CACHE_MAX_COUNT)

    suspend fun ensureLibrary(libraryId: Long, context: Context? = null): Library {
        val library = if(libraryId != -1L) find(libraryId) else findDefault()
        if(library == null) return Library.EMPTY

        val current = currentLibraries[library.id]
        if(current != null && current.root == library.root) return current

        if(load(library, context)) {
            currentLibraries.put(library.id, library)
            return library
        }

        return Library.EMPTY
    }

    private suspend fun load(library: Library, context: Context?): Boolean {
        if(context != null) {
            val loadingLibraryDialog = ProgressDialog.show(
                context,
                "Loading " + library.name,
                "Please wait while the library is loaded...",
                true
            )

            return inflate(library).also { loadingLibraryDialog.dismiss() }
        }
        else {
            return inflate(library)
        }
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
            val request = Request(library.dataUrl.toHttpUrl())

            MangaApplication.okHttpClient.newCall(request).execute().use {
                if(!it.isSuccessful) throw IOException("Request failed. Request: $request, Response: $it")
                books = Json.decodeFromStream(it.body.byteStream())
            }

            library.inflate(books)
        }
    }
}