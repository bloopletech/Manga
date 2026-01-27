package net.bloople.manga.audit

import android.content.Context
import android.database.Cursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuditEventsSearcher internal constructor() {
    var resourceId: Long? = null

    suspend fun search(): Cursor {
        val cursor: Cursor

        withContext(Dispatchers.IO) {
            val db = DatabaseHelper.instance()

            cursor = resourceId?.let {
                db.query(
                    "audit_events",
                    null,
                    "resource_id = ?", arrayOf(it.toString()),
                    null,
                    null,
                    "\"when\" DESC"
                )
            } ?: db.query(
                "audit_events",
                null,
                null,
                null,
                null,
                null,
                "\"when\" DESC"
            )

            cursor.moveToFirst()
        }

        return cursor
    }
}
