package net.bloople.manga

import java.lang.StringBuilder

// From: https://dzone.com/articles/java-implementation-stringnext
// Original source: http://saltnlight5.blogspot.com.au/2013/01/java-implementation-of-stringnext.html

/**
 * Utilities method for manipulating String.
 * @author zemian 1/1/13
 */
internal object StringNextUtil {
    /** Calculate string successor value. Similar to Ruby's String#next() method.  */
    fun next(text: String): String {
        // We will not process empty string
        val len = text.length
        if(len == 0) return text

        // Determine where does the first alpha-numeric starts.
        var alphaNum = false
        var alphaNumPos = -1
        for(c in text.toCharArray()) {
            alphaNumPos++
            if(Character.isDigit(c) || Character.isLetter(c)) {
                alphaNum = true
                break
            }
        }

        // Now we go calculate the next successor char of the given text.
        var buf = StringBuilder(text)
        if(!alphaNum || alphaNumPos == 0 || alphaNumPos == len) {
            // do the entire input text
            next(buf, buf.length - 1, alphaNum)
        }
        else {
            // Strip the input text for non alpha numeric prefix. We do not need to process these prefix but to save and
            // re-attach it later after the result.
            val prefix = text.substring(0, alphaNumPos)
            buf = StringBuilder(text.substring(alphaNumPos))
            next(buf, buf.length - 1, alphaNum)
            buf.insert(0, prefix)
        }

        // We are done.
        return buf.toString()
    }

    /** Internal method to calculate string successor value on alpha numeric chars only.  */
    private fun next(buf: StringBuilder, pos: Int, alphaNum: Boolean) {
        // We are asked to carry over next value for the left most char
        if(pos == -1) {
            val c = buf[0]
            var rep: String? = null
            rep = when {
                Character.isDigit(c) -> "1"
                Character.isLowerCase(c) -> "a"
                Character.isUpperCase(c) -> "A"
                else -> (c.code + 1).toChar().toString()
            }
            buf.insert(0, rep)
            return
        }

        // We are asked to calculate next successor char for index of pos.
        val c = buf[pos]
        if(Character.isDigit(c)) {
            if(c == '9') {
                buf.replace(pos, pos + 1, "0")
                next(buf, pos - 1, alphaNum)
            }
            else {
                buf.replace(pos, pos + 1, (c.code + 1).toChar().toString())
            }
        }
        else if(Character.isLowerCase(c)) {
            if(c == 'z') {
                buf.replace(pos, pos + 1, "a")
                next(buf, pos - 1, alphaNum)
            }
            else {
                buf.replace(pos, pos + 1, (c.code + 1).toChar().toString())
            }
        }
        else if(Character.isUpperCase(c)) {
            if(c == 'Z') {
                buf.replace(pos, pos + 1, "A")
                next(buf, pos - 1, alphaNum)
            }
            else {
                buf.replace(pos, pos + 1, (c.code + 1).toChar().toString())
            }
        }
        else {
            // If input text has any alpha num at all then we are to calc next these characters only and ignore the
            // we will do this by recursively call into next char in buf.
            if(alphaNum) {
                next(buf, pos - 1, alphaNum)
            }
            else {
                // However if the entire input text is non alpha numeric, then we will calc successor by simply
                // increment to the next char in range (including non-printable char!)
                if(c == Character.MAX_VALUE) {
                    buf.replace(pos, pos + 1, Character.MIN_VALUE.toString())
                    next(buf, pos - 1, alphaNum)
                }
                else {
                    buf.replace(pos, pos + 1, (c.code + 1).toChar().toString())
                }
            }
        }
    }
}