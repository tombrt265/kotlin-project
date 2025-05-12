package core

import java.time.LocalDate

data class Transaction(
  val date: LocalDate,
  val amount: Double,
  val description: String,
  var category: String? = null
  )
