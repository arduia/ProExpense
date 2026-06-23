package com.arduia.expense.storage.mapper

import com.arduia.expense.domain.Participant

/**
 * Minimal hand-rolled JSON for the two SharedCost blob columns. The schema names them `*_json`, and
 * participant names are user text (may contain quotes/commas/backslashes), so encode escapes and the
 * decoder honours escapes. Kept tiny and self-contained to avoid pulling a serialization runtime
 * into the storage module; covered by a round-trip test.
 */
internal object SharedCostJson {

    fun encodeParticipants(participants: List<Participant>): String = buildString {
        append('[')
        participants.forEachIndexed { index, p ->
            if (index > 0) append(',')
            append("{\"id\":\"").append(p.id.escape())
            append("\",\"name\":\"").append(p.name.escape()).append("\"}")
        }
        append(']')
    }

    fun decodeParticipants(json: String): List<Participant> {
        val reader = JsonReader(json)
        val result = mutableListOf<Participant>()
        reader.skipWs()
        if (!reader.consume('[')) return result
        reader.skipWs()
        if (reader.consume(']')) return result
        while (true) {
            reader.skipWs()
            reader.expect('{')
            var id = ""
            var name = ""
            while (true) {
                reader.skipWs()
                val key = reader.readString()
                reader.skipWs()
                reader.expect(':')
                reader.skipWs()
                val value = reader.readString()
                when (key) {
                    "id" -> id = value
                    "name" -> name = value
                }
                reader.skipWs()
                if (reader.consume(',')) continue
                reader.expect('}')
                break
            }
            result.add(Participant(id = id, name = name))
            reader.skipWs()
            if (reader.consume(',')) continue
            reader.consume(']')
            break
        }
        return result
    }

    fun encodeShares(shares: List<Long>?): String? =
        shares?.joinToString(prefix = "[", postfix = "]", separator = ",")

    fun decodeShares(json: String?): List<Long>? {
        if (json == null) return null
        return json.trim().removePrefix("[").removeSuffix("]")
            .split(',')
            .mapNotNull { it.trim().toLongOrNull() }
            .takeIf { it.isNotEmpty() }
    }

    private fun String.escape(): String = buildString {
        for (c in this@escape) {
            when (c) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                else -> append(c)
            }
        }
    }

    private class JsonReader(private val source: String) {
        private var pos = 0

        fun skipWs() {
            while (pos < source.length && source[pos].isWhitespace()) pos++
        }

        fun consume(c: Char): Boolean {
            if (pos < source.length && source[pos] == c) {
                pos++
                return true
            }
            return false
        }

        fun expect(c: Char) {
            require(consume(c)) { "Expected '$c' at $pos in: $source" }
        }

        /** Reads a JSON string token (surrounding quotes), honouring \" and \\ escapes. */
        fun readString(): String {
            expect('"')
            val sb = StringBuilder()
            while (pos < source.length) {
                val c = source[pos++]
                when {
                    c == '\\' && pos < source.length -> sb.append(source[pos++])
                    c == '"' -> return sb.toString()
                    else -> sb.append(c)
                }
            }
            error("Unterminated string in: $source")
        }
    }
}
