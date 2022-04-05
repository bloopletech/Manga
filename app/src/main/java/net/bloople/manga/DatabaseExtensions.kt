package net.bloople.manga

import android.database.Cursor

internal inline operator fun <reified T> Cursor.get(columnName: String): T {
    return when(T::class) {
        Double::class -> this.getDouble(this.getColumnIndexOrThrow(columnName)) as T
        Float::class -> this.getFloat(this.getColumnIndexOrThrow(columnName)) as T
        Int::class -> this.getInt(this.getColumnIndexOrThrow(columnName)) as T
        Long::class -> this.getLong(this.getColumnIndexOrThrow(columnName)) as T
        Short::class -> this.getShort(this.getColumnIndexOrThrow(columnName)) as T
        String::class, CharSequence::class -> this.getString(this.getColumnIndexOrThrow(columnName)) as T
        else -> throw IllegalArgumentException("Cursor does not have a getter for type ${T::class.qualifiedName}")
    }
}