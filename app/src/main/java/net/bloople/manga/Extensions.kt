package net.bloople.manga

import android.database.Cursor
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import coil3.load
import java.util.Locale

val Number.f: String
    get() = String.format(Locale.getDefault(), "%,d", this)

fun Number.f(plural: String, singular: String = plural.removeSuffix("s")): String {
    return "${this.f} ${if(this == 1) singular else plural}"
}

val String.l: String
    get() = this.lowercase(Locale.getDefault())

val String.u: String
    get() = this.uppercase(Locale.getDefault())

fun <T : Fragment> View.findFragmentById(@IdRes id: Int): T {
    val view: FragmentContainerView = findViewById(id)
    return view.getFragment()
}

inline operator fun <reified T: Any?> Cursor.get(columnName: String): T {
    val columnIndex = getColumnIndexOrThrow(columnName)
    return when (T::class) {
        ByteArray::class -> getBlob(columnIndex) as T
        Double::class -> getDouble(columnIndex) as T
        Float::class -> getFloat(columnIndex) as T
        Int::class -> getInt(columnIndex) as T
        Long::class -> getLong(columnIndex) as T
        Short::class -> getShort(columnIndex) as T
        String::class, CharSequence::class -> getString(columnIndex) as T
        Boolean::class -> (getInt(columnIndex) == 1) as T
        else -> throw IllegalArgumentException("Cursor does not have a getter for type ${T::class.qualifiedName}")
    }
}

fun ImageView.clear() {
    setImageDrawable(null)
}