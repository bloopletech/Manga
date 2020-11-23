package net.bloople.manga.audit;

import android.content.Context;

import net.bloople.manga.Library;

public class LibrariesAuditor {
    private Context context;

    public LibrariesAuditor(Context context) {
        this.context = context;
    }

    public void selected(Library library) {
        AuditEvent event = new AuditEvent(
            System.currentTimeMillis(),
            Action.LIBRARY_SELECTED,
            ResourceType.UNKNOWN,
            AuditEvent.UNKNOWN_ID,
            ResourceType.LIBRARY,
            library.id(),
            library.name(),
            ""
        );
        event.save(context);
    }
}
