package net.bloople.manga.audit;

enum Action {
    LIBRARY_CREATED,
    LIBRARY_UPDATED,
    LIBRARY_DESTROYED,
    ACTIVE_LIBRARY_CHANGED,
    BOOK_METADATA_CREATED,
    BOOK_METADATA_UPDATED,
    BOOK_OPENED,
    BOOK_BOOKMARKED,
    BOOK_RESUMED,
    BOOK_CLOSED
}