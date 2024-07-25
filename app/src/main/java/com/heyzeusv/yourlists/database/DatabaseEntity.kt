package com.heyzeusv.yourlists.database

interface DatabaseEntity {
    val csvHeader: List<String>
    val csvRow: List<Any>
}