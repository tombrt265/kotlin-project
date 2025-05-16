package expenseTracker.output

import expenseTracker.core.Transaction
import expenseTracker.persistence.CsvManager
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import expenseTracker.utils.Constants.CATEGORIZED_FILE_NAME
import expenseTracker.utils.Constants.OUTPUT_FOLDER
import java.io.File

class OutputTest : AnnotationSpec() {

  private val cm = CsvManager()

  @Test
  fun `write to file`() {
    // Given
    val transactions = listOf(
      Transaction("2023-01-02", 100.00, "REWE", "Lebensmittel"),
      Transaction("2023-01-01", 50.00, "NETFLIX", "Unterhaltung")
    )

    // When
    cm.writeCategorizedCsv(transactions)

    // Then
    val file = File("$OUTPUT_FOLDER/$CATEGORIZED_FILE_NAME")
    assertThat(file.readLines()).isEqualTo(
      listOf(
        "Buchungstag;Kategorie;Betrag",
        "2023-01-02;Lebensmittel;100.0",
        "2023-01-01;Unterhaltung;50.0")
    )
  }
}