package net.bloople.manga.audit

import android.content.Context
import android.database.Cursor

class AuditEventsSearcher internal constructor() {
    var resourceId: Long? = null

    fun search(context: Context): Cursor {
        val db = DatabaseHelper.instance(context)

        val cursor = resourceId?.let {
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
        return cursor
    }
}
