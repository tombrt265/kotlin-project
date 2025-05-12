package persistence

import core.Transaction

interface ReadFilePort {
    fun readCsvFiles(): Set<List<String>>
}

interface WriteFilePort {
    fun writeCategorizedCsv(transactions: List<Transaction>)
}