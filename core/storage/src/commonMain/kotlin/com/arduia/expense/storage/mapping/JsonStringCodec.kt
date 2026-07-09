package com.arduia.expense.storage.mapping

/**
 * Minimal hand-rolled JSON string codec shared by every hand-written JSON reader/writer in this
 * module (shared-cost participants/splits, finance-record import/export) — pure Kotlin so it's
 * common to Android and iOS. Kept intentionally small rather than pulling in a JSON library for
 * a handful of flat, known-shape objects.
 */
internal fun escapeJsonString(str: String): String =
    str
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")

internal fun unescapeJsonString(str: String): String {
    val sb = StringBuilder(str.length)
    var i = 0
    while (i < str.length) {
        val c = str[i]
        if (c == '\\' && i + 1 < str.length) {
            when (str[i + 1]) {
                '"' -> sb.append('"')
                '\\' -> sb.append('\\')
                'n' -> sb.append('\n')
                'r' -> sb.append('\r')
                't' -> sb.append('\t')
                else -> sb.append(str[i + 1])
            }
            i += 2
        } else {
            sb.append(c)
            i++
        }
    }
    return sb.toString()
}

/**
 * Matches up to the first *unescaped* closing quote — `[^"]*` alone stops early at any escaped
 * quote (`\"`) inside the value, truncating strings that contain one. The matched raw
 * (still-escaped) text is then unescaped so `\n`/`\r`/`\"`/`\\` come back as real characters
 * instead of literal two-char sequences.
 */
internal fun extractJsonString(
    json: String,
    key: String,
): String? {
    val regex = Regex("\"$key\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
    return regex
        .find(json)
        ?.groupValues
        ?.get(1)
        ?.let(::unescapeJsonString)
}

internal fun extractJsonNumber(
    json: String,
    key: String,
): Long? {
    val regex = Regex("\"$key\"\\s*:\\s*(-?\\d+)")
    return regex
        .find(json)
        ?.groupValues
        ?.get(1)
        ?.toLongOrNull()
}

/**
 * Reads a field from inside a nested `"objectKey":{...}` object rather than the top level — e.g.
 * `money.cents` in `"money":{"cents":123,"code":"USD"}`. A plain wildcard between the outer and
 * inner key can't work here since `[^:]*` can never cross the colon that follows the outer key,
 * so the object's contents are isolated first and searched independently.
 */
internal fun extractNestedObject(
    json: String,
    objectKey: String,
): String? = Regex("\"$objectKey\"\\s*:\\s*\\{([^}]*)\\}").find(json)?.groupValues?.get(1)

internal fun extractNestedString(
    json: String,
    objectKey: String,
    fieldKey: String,
): String? = extractNestedObject(json, objectKey)?.let { extractJsonString(it, fieldKey) }

internal fun extractNestedNumber(
    json: String,
    objectKey: String,
    fieldKey: String,
): Long? = extractNestedObject(json, objectKey)?.let { extractJsonNumber(it, fieldKey) }
