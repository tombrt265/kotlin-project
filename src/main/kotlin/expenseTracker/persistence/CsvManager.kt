package persistence

import utils.Constants.CSV_SEPARATOR
import utils.Constants.EXCHANGE_FOLDER
import core.Transaction
import utils.Columns
import java.io.File

class CsvManager : ReadFilePort, WriteFilePort {
  override fun readCsvFiles(): Set<List<String>> {
    val files = getFilesFromDir()
    var shortenedRow: List<String>
    val listOfTransactions: MutableList<List<String>> = mutableListOf()

    for (file in files) {
      val importantColumns: MutableMap<String, Int> = getImportantColumns(files.first())
      val indicesToKeep = importantColumns.values.toSet()

      if (file.readLines().count() == 1) throw NoContentInFileException(file)

      for (line in file.readLines()) {
        if (line.isBlank()) continue
        val cols: List<String> = line.split(CSV_SEPARATOR)
        shortenedRow = cols.filterIndexed { index, _ -> index in indicesToKeep }
          .toMutableList()
        listOfTransactions.add(shortenedRow)
      }
    }

    val uniqueTransactions = listOfTransactions.toSet()
    return uniqueTransactions
  }

  private fun getImportantColumns(file: File): MutableMap<String, Int> {
    val headerLine: String = try {
      file.readLines().first()
    } catch (e: Exception) {
      throw(EmptyFileException(file))
    }
    val headerCols: List<String> = headerLine.split(CSV_SEPARATOR)
    val importantCols = mutableMapOf<String, Int>()
    for (col in headerCols) {
      if (col.contains(Columns.Buchungstag.toString()) || col.contains(Columns.Beguenstigter.toString()) || col.contains(
          Columns.Betrag.toString())) {
        importantCols[col] = headerCols.indexOf(col)
      }
    }
    if (importantCols.size < 3) throw NotAllImportantColumnsInFileException(file)
    return importantCols
  }

  private fun getFilesFromDir(): Sequence<File> {
    val path = File(EXCHANGE_FOLDER)
    val files: Sequence<File> = path.walk().filter { it.name.endsWith(".csv") }
    if (files.count() == 0) throw EmptyFolderException(path)
    return files
  }

  override fun writeCategorizedCsv(transactions: List<Transaction>) {
    TODO("Implement CSV writing logic")
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
