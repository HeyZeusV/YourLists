package com.heyzeusv.yourlists.database

interface DatabaseEntity {
    val csvName: String
    val csvHeader: List<String>
    val csvRow: List<Any?>
}