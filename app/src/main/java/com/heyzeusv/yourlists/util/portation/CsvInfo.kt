package com.heyzeusv.yourlists.util.portation

interface CsvInfo {
    val csvName: String
    val csvHeader: List<String>
    val csvRow: List<Any?>
}