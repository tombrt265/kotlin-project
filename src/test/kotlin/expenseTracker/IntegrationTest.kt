package expenseTracker

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import expenseTracker.main.main
import expenseTracker.utils.Constants.CATEGORIZED_FILE_NAME
import expenseTracker.utils.Constants.INPUT_FOLDER
import expenseTracker.utils.Constants.OUTPUT_FOLDER
import java.io.File

class IntegrationTest : AnnotationSpec() {

  private val inputFolder = File(INPUT_FOLDER)
  private val inputFile = File("$INPUT_FOLDER/input.csv")

  @BeforeEach
  fun setup() {
    inputFolder.deleteRecursively()
    inputFolder.mkdirs()
    inputFile.createNewFile()
  }

  @AfterEach
  fun tearDown() {
    inputFile.delete()
  }

  @Test
  fun `columns are in wrong order`() {
    // Given
    val categorizedFile = File("$OUTPUT_FOLDER/$CATEGORIZED_FILE_NAME")
    inputFile.writeText(
      """
      "Betrag";"Buchungstag/Zustand";"Beguenstigter";"Waehrung"
      "100.0€";"2023-01-01";"NETTO";"EUR"
      "200.0€";"2023-01-03";"REWE";"EUR"
      "50.0€";"2023-01-02";"Netflix";"EUR"
      """.trimIndent())

    // When
    main()

    // Then
    assertThat(categorizedFile.readLines()).isEqualTo(
      listOf(
        "Buchungstag;Kategorie;Betrag",
        "2023-01-01;Lebensmittel;100.0",
        "2023-01-02;;50.0",
        "2023-01-03;Lebensmittel;200.0"
      )
    )
  }
}