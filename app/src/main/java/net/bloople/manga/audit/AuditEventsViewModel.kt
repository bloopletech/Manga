package net.bloople.manga.audit

import android.app.Application
import android.database.Cursor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors

class AuditEventsViewModel(application: Application) : AndroidViewModel(application) {
    val searchResults: MutableLiveData<Cursor> by lazy {
        MutableLiveData<Cursor>()
    }

    private val searcher = AuditEventsSearcher()

    fun setResourceId(resourceId: Long?) {
        searcher.resourceId = resourceId
        resolve()
    }

    private fun resolve() {
        val service = Executors.newSingleThreadExecutor()
        service.submit { searchResults.postValue(searcher.search(getApplication())) }
    }
}