package net.bloople.manga.audit

import android.content.Context
import net.bloople.manga.Library

class LibrariesAuditor() {
    fun selected(library: Library) {
        val event = AuditEvent(
            System.currentTimeMillis(),
            Action.LIBRARY_SELECTED,
            ResourceType.UNKNOWN,
            AuditEvent.UNKNOWN_ID,
            ResourceType.LIBRARY,
            library.id,
            library.name!!,
            ""
        )
        event.save()
    }
}