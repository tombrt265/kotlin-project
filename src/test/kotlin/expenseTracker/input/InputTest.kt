package expenseTracker.input

import expenseTracker.persistence.*
import expenseTracker.utils.Constants.INPUT_FOLDER
import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import java.io.File

class InputTest : AnnotationSpec() {

  @BeforeEach
  fun cleanUpBefore() {
    File(INPUT_FOLDER).deleteRecursively()
    val folder = File(INPUT_FOLDER)
    if (!folder.exists()) {
      folder.mkdirs()
    }
  }

  @AfterEach
  fun tearDownAfter() {
    File(INPUT_FOLDER).deleteRecursively()
  }

  @Test
  fun `detect no files in exchange folder`() {
    // Given

    // When

    // Then
    assertThrows<EmptyFolderException> { CsvManager().readCsvFiles() }
  }

  @Test
  fun `detect empty file in exchange folder`() {
    // Given
    File(INPUT_FOLDER, "empty.csv").writeText("")

    // When

    // Then
    assertThrows<EmptyFileException> { CsvManager().readCsvFiles() }
  }

  @Test
  fun `file is not csv`() {
    // Given
    File(INPUT_FOLDER, "notCsv.txt").writeText("""
      "Buchungstag";"Beguenstigter";"Betrag"
    """.trimIndent())

    // When

    // Then
    assertThrows<EmptyFolderException> { CsvManager().readCsvFiles() }
  }

  @Test
  fun `not all files are csv`() {
    // Given
    File(INPUT_FOLDER, "notCsv.txt").writeText("""
      "Buchungstag";"Beguenstigter";"Betrag"   
      """.trimIndent())
    File(INPUT_FOLDER, "test1.csv").writeText("""
      "Buchungstag";"Beguenstigter";"Betrag"
      "10.10.2025";"NETTO";"100€"
      """.trimIndent())

    // When
    val fileContent = CsvManager().readCsvFiles()

    // Then
    assertThat(fileContent).isEqualTo(
      setOf(
        listOf(
          "Buchungstag",
          "Beguenstigter",
          "Betrag"
        ),
        listOf(
          "10.10.2025",
          "NETTO",
          "100€"
        )
      )
    )
  }

  @Test
  fun `detect empty file in exchange folder with header`() {
    // Given
    File(INPUT_FOLDER, "onlyHeader.csv").writeText("Buchungstag;Beguenstigter;Betrag")

    // When

    // Then
    assertThrows<NoContentInFileException> { CsvManager().readCsvFiles() }
  }

  @Test
  fun `detect file in exchange folder without header`() {
    // Given
    File(INPUT_FOLDER, "noHeader.csv").writeText("10.10.2025;NETTO;100€")

    // When

    // Then
    assertThrows<NotAllImportantColumnsInFileException> { CsvManager().readCsvFiles() }
  }

  @Test
  fun `file has header and content`() {
    // Given
    File(
      INPUT_FOLDER,
      "withHeader.csv"
    ).writeText("Buchungstag;Beguenstigter;Betrag;TestColumn\n10.10.2025;NETTO;100€;TestValue\n11.10.2025;NETTO;200€;TestValue2")

    // When
    val fileContent = CsvManager().readCsvFiles()

    // Then
    assertThat(fileContent).isEqualTo(
      setOf(
        listOf(
          "Buchungstag",
          "Beguenstigter",
          "Betrag"
        ),
        listOf(
          "10.10.2025",
          "NETTO",
          "100€"
        ), listOf("11.10.2025", "NETTO", "200€")
      )
    )
  }

  @Test
  fun `file has duplicate rows`() {
    // Given
    File(
      INPUT_FOLDER,
      "duplicateRows.csv"
    ).writeText("Buchungstag;Beguenstigter;Betrag\n10.10.2025;NETTO;100€\n10.10.2025;NETTO;100€")

    // When
    val fileContent = CsvManager().readCsvFiles()

    // Then
    assertThat(fileContent).isEqualTo(
      setOf(
        listOf(
          "Buchungstag",
          "Beguenstigter",
          "Betrag"
        ),
        listOf(
          "10.10.2025",
          "NETTO",
          "100€"
        )
      )
    )
  }

  @Test
  fun `file does not have all important columns`() {
    // Given
    File(
      INPUT_FOLDER,
      "noImportantColumns.csv"
    ).writeText("TestColumn1;TestColumn2;TestColumn3\n10.10.2025;NETTO;100€")
    // When

    // Then
    assertThrows<NotAllImportantColumnsInFileException> { CsvManager().readCsvFiles() }
  }

  @Test
  fun `two files have content`() {
    // Given
    File(
      INPUT_FOLDER,
      "file1.csv"
    ).writeText("Buchungstag;Beguenstigter;Betrag\n10.10.2025;NETTO;100€\n11.10.2025;NETTO;200€")
    File(
      INPUT_FOLDER,
      "file2.csv"
    ).writeText("Buchungstag;Beguenstigter;Betrag\n12.10.2025;NETTO;300€\n13.10.2025;NETTO;400€")

    // When
    val fileContent = CsvManager().readCsvFiles()

    // Then
    assertThat(fileContent).isEqualTo(
      setOf(
        listOf(
          "Buchungstag",
          "Beguenstigter",
          "Betrag"
        ),
        listOf(
          "10.10.2025",
          "NETTO",
          "100€"
        ),
        listOf("11.10.2025", "NETTO", "200€"),
        listOf("12.10.2025", "NETTO", "300€"),
        listOf("13.10.2025", "NETTO", "400€")
      )
    )
  }

  @Test
  fun `two files have duplicates`() {
    // Given
    File(
      INPUT_FOLDER,
      "file1.csv"
    ).writeText("Buchungstag;Beguenstigter;Betrag\n10.10.2025;NETTO;100€\n11.10.2025;NETTO;200€")
    File(
      INPUT_FOLDER,
      "file2.csv"
    ).writeText("Buchungstag;Beguenstigter;Betrag\n10.10.2025;NETTO;100€\n11.10.2025;NETTO;300€")

    // When
    val fileContent = CsvManager().readCsvFiles()

    // Then
    assertThat(fileContent).isEqualTo(
      setOf(
        listOf(
          "Buchungstag",
          "Beguenstigter",
          "Betrag"
        ),
        listOf(
          "10.10.2025",
          "NETTO",
          "100€"
        ),
        listOf("11.10.2025", "NETTO", "200€"),
        listOf("11.10.2025", "NETTO", "300€")
      )
    )
  }
}