package net.bloople.manga

import net.bloople.manga.Library.Companion.findById
import net.bloople.manga.Library.Companion.findDefault
import android.app.ProgressDialog
import android.content.Context
import android.util.LruCache
import kotlinx.serialization.ExperimentalSerializationApi
import java.io.IOException

class LibraryService {
    companion object {
        private const val LIBRARY_CACHE_MAX_COUNT = 5
        private val currentLibraries = LruCache<Long, Library>(LIBRARY_CACHE_MAX_COUNT)

        suspend fun ensureLibrary(context: Context, libraryId: Long): Library? {
            var library = findById(context, libraryId)
            if(library == null) library = findDefault(context)

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
                library.inflate()
                true
            }
            catch(e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }
}