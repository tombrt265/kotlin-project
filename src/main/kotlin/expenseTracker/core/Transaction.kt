package expenseTracker.core

data class Transaction(
  val date: String,
  val amount: Double,
  val description: String,
  var category: String = ""
  )
