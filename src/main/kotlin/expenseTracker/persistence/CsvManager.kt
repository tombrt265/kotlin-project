package expenseTracker.persistence

import expenseTracker.utils.Constants.CSV_SEPARATOR
import expenseTracker.utils.Constants.INPUT_FOLDER
import expenseTracker.core.Transaction
import expenseTracker.utils.Columns.*
import expenseTracker.utils.Constants.CATEGORIZED_FILE_NAME
import expenseTracker.utils.Constants.OUTPUT_FOLDER
import java.io.File

class CsvManager : ReadFilePort, WriteFilePort {
  override fun readCsvFiles(): MutableSet<MutableList<String>> {
    val files = getFilesFromDir()
    var shortenedRow: List<String>
    val listOfTransactions: MutableList<MutableList<String>> = mutableListOf()
    val importantColumns: MutableMap<String, Int> = getImportantColumns(files.first())

    for (file in files) {
      val indicesToKeep = importantColumns.values.toSet()
      val fileLines = file.readLines()
      if (fileLines.count() == 1) throw NoContentInFileException(file)
      for (line in fileLines) {
        if (line.isBlank()) continue
        val cols: List<String> = line.split(CSV_SEPARATOR).map { it.trim().removeSurrounding("\"") }
        shortenedRow = cols.filterIndexed { index, _ -> index in indicesToKeep }
          .toMutableList()
        listOfTransactions.add(shortenedRow)
      }
    }

    val uniqueTransactions = listOfTransactions.toMutableSet()
    return uniqueTransactions
  }

  private fun getImportantColumns(file: File): MutableMap<String, Int> {
    val fileLines = file.readLines()
    if (fileLines.isEmpty()) throw EmptyFileException(file)
    val headerCols = fileLines.first().split(CSV_SEPARATOR).map { it.trim().removeSurrounding("\"") }
    val importantCols = mutableMapOf<String, Int>()
    for ((index, col) in headerCols.withIndex()) {
      when {
        col.contains(Buchungstag.toString(), ignoreCase = true) -> importantCols[Buchungstag.toString()] = index
            col.contains(Beguenstigter.toString(), ignoreCase = true) -> importantCols[Beguenstigter.toString()] = index
            col.contains(Betrag.toString(), ignoreCase = true) -> importantCols[Betrag.toString()] = index
      }
    }
    if (importantCols.values.toSet().size != 3) throw NotAllImportantColumnsInFileException(file)
    return importantCols
  }

  private fun getFilesFromDir(): Sequence<File> {
    val path = File(INPUT_FOLDER)
    if (!path.exists()) throw EmptyFolderException(path)
    val files: Sequence<File> = path.walk().filter { it.name.endsWith(".csv") }
    if (files.count() == 0) throw EmptyFolderException(path)
    return files
  }

  override fun writeCategorizedCsv(transactions: List<Transaction>) {
    val path = File(OUTPUT_FOLDER)
    val file = File("$OUTPUT_FOLDER/$CATEGORIZED_FILE_NAME")
    path.mkdirs()
    if (!file.createNewFile()) file.delete()
    file.createNewFile()
    val header = "Buchungstag;Kategorie;Betrag"
    file.appendText("$header\n")
    for (transaction in transactions) {
      val line = "${transaction.date};${transaction.category};${transaction.amount}"
      file.appendText("$line\n")
    }
  }
}

class EmptyFolderException(path: File) : Exception(
  "The folder ${path.name} does not contain any .csv files. Please check the folder and try again."
)

class EmptyFileException(file: File) : Exception(
  "The file ${file.name} is empty. Please check the file and try again."
)

class NoContentInFileException(file: File) : Exception(
  "The file ${file.name} has no content. Please check the file and try again."
)

class NotAllImportantColumnsInFileException(file: File) : Exception(
  "The file ${file.name} does not contain all important columns (Buchungstag, Beguenstigter, Betrag). Please check the file and try again."
)
