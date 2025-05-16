package expenseTracker.persistence

import expenseTracker.core.Transaction

interface ReadFilePort {
    fun readCsvFiles(): MutableSet<MutableList<String>>
}

interface WriteFilePort {
    fun writeCategorizedCsv(transactions: List<Transaction>)
}