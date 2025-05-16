package expenseTracker.core

// TODO("transactionlist can somehow still be less than 3 elements")

class TransactionHandler {
  fun toTransactions(listOfTransactions: MutableSet<MutableList<String>>): List<Transaction> {
    val transactions = mutableListOf<Transaction>()
    for (transaction in listOfTransactions) {
      transaction[2] = transaction[2].replace("€", "").replace(",", ".")
      transactions.add(
        Transaction(
        date = transaction[0],
        description = transaction[1],
        amount = transaction[2].toDouble())
      )
    }
    return transactions
  }

  fun categorize(transactions: List<Transaction>, rules: List<CategoryRule>): List<Transaction> {
    return transactions.map {
      var category = ""
      for (rule in rules) {
        if (rule.matches(it)) {
          category = rule.categoryName
          break
        }
      }
      it.copy(category = category)
    }
  }

  fun sortByDate(transactions: List<Transaction>): List<Transaction> {
    return transactions.sortedBy { it.date }
  }
}
