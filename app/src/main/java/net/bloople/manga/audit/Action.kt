package net.bloople.manga.audit

internal enum class Action {
    UNKNOWN,
    LIBRARY_CREATED,
    LIBRARY_UPDATED,
    LIBRARY_DESTROYED,
    LIBRARY_SELECTED,
    BOOK_METADATA_CREATED,
    BOOK_METADATA_UPDATED,
    BOOK_OPENED,
    BOOK_CLOSED
}