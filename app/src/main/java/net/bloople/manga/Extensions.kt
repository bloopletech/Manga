package net.bloople.manga

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