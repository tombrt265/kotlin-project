package expenseTracker.core

data class CategoryRule(
  val keyword: String,
  val categoryName: String
) {
  fun matches(transaction: Transaction): Boolean {
    return transaction.description.contains(keyword, ignoreCase = true)
  }
}
