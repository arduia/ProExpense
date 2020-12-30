package com.arduia.backup

import java.util.LinkedHashMap


class SheetRow private constructor(): LinkedHashMap<String, String>(){
    companion object{
        fun createFromMap(map: Map<String, String>): SheetRow {
            return SheetRow().apply {
                putAll(map)
            }
        }
    }
}
