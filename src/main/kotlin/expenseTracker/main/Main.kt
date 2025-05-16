package expenseTracker.main

import expenseTracker.core.CategoryRule
import expenseTracker.core.TransactionHandler
import expenseTracker.persistence.CsvManager
import expenseTracker.utils.Columns.*

fun main() {
  val cm = CsvManager()
  val th = TransactionHandler()
  val listOfTransactions = cm.readCsvFiles()
  val columnNames = listOfTransactions.first()
  val desiredOrder = mutableListOf(Buchungstag.toString(), Beguenstigter.toString(), Betrag.toString())
  var (rearrangedColumns, rearrangedTransactions) = rearrangeColumns(desiredOrder, columnNames, listOfTransactions)

  rearrangedTransactions = rearrangedTransactions.drop(1).toMutableSet()
  val transactions = th.toTransactions(rearrangedTransactions)

  val rules = listOf(
    CategoryRule("REWE", "Lebensmittel"),
    CategoryRule("NETTO", "Lebensmittel"),
    )

  val categorizedTransactions = th.categorize(transactions, rules)
  val sortedTransactions = th.sortByDate(categorizedTransactions)
  val case = cm.writeCategorizedCsv(sortedTransactions)
}

private fun rearrangeColumns(
  desiredOrder: List<String>,
  columnNames: MutableList<String>,
  listOfTransactions: MutableSet<MutableList<String>>,
) : Pair<List<String>, MutableSet<MutableList<String>>> {

  val mapping = mutableMapOf<String, Int>()
  for ((index, colName) in columnNames.withIndex()) {
    val cleanCol = colName.trim().removeSurrounding("\"")
    when {
      cleanCol.contains(Buchungstag.toString(), ignoreCase = true) ->
        mapping[Buchungstag.toString()] = index

      cleanCol.contains(Beguenstigter.toString(), ignoreCase = true) ->
        mapping[Beguenstigter.toString()] = index

      cleanCol.contains(Betrag.toString(), ignoreCase = true) ->
        mapping[Betrag.toString()] = index
    }
  }
  val indexMap = desiredOrder.mapNotNull { mapping[it] }

  val sortedContent = mutableSetOf<MutableList<String>>()
  for (row in listOfTransactions) {
    val sortedRow = indexMap.map { row[it] }.toMutableList()
    sortedContent.add(sortedRow)
  }
  return desiredOrder to sortedContent
}