package core

import java.time.YearMonth

object TransactionAnalyzer {
  fun sortByDate(transactions: List<Transaction>): List<Transaction> =
    transactions.sortedBy { it.date }

  fun groupByMonth(transactions: List<Transaction>): Map<YearMonth, List<Transaction>> =
    transactions.groupBy { YearMonth.from(it.date) }

  //fun categorize(transactions: List<core.Transaction>, rules: List<core.CategoryRule>) {
  //  for (tx in transactions) {
  //    val match = rules.firstOrNull { it.matches(tx.description) }
  //    tx.category = match?.categoryName ?: "Unkategorisiert"
  //  }
  //}
}