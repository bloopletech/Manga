package net.bloople.manga

import java.util.ArrayList

fun inflatePagePaths(pagesString: String): ArrayList<String> {
    val paths = ArrayList<String>()
    val parts = pagesString.split("|")

    for(part in parts) {
        if(part.contains("/")) {
            val partParts = part.split("/")
            val name = partParts[0]
            val count = Integer.valueOf(partParts[1])
            val lastPeriod = name.lastIndexOf(".")
            var lastBase = name.substring(0, lastPeriod)
            val lastExt = name.substring(lastPeriod)
            paths.add(name)

            for(i in 0 until count) {
                lastBase = StringNextUtil.next(lastBase)
                paths.add(lastBase + lastExt)
            }
        }
        else {
            paths.add(part)
        }
    }

    return paths
}