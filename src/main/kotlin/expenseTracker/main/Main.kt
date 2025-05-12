package main

import core.CategoryRule

class Main {

  val rules = listOf(
    CategoryRule("REWE", "Lebensmittel"),
    CategoryRule("IKEA", "Möbel"),
    CategoryRule("Netflix", "Unterhaltung")
  )

  //val transactions = persistence.CsvManager.readCsvFile(inputFolder)
  //core.TransactionAnalyzer.categorize(transactions, rules)
  //val sorted = core.TransactionAnalyzer.sortByDate(transactions)
  //persistence.CsvManager.writeCategorizedCsv(sorted, outputFile)

}