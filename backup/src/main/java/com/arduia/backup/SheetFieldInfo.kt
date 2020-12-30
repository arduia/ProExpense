package com.arduia.backup

import java.util.LinkedHashMap

class SheetFieldInfo private constructor(): LinkedHashMap<String, String>() {
    companion object {
        fun createFromMap(map: Map<String, String>): SheetFieldInfo {
            return SheetFieldInfo().apply {
                putAll(map)
            }
        }
    }
}