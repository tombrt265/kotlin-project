package expenseTracker.core

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class TransactionHandlingTest : AnnotationSpec() {

  private val th = TransactionHandler()

  @Test
  fun `correctly translate transactions`() {
    // Given
    val setOfTransactions = mutableSetOf(
      mutableListOf(
        "10.10.2025",
        "NETTO",
        "100€"
      ),
      mutableListOf(
        "11.10.2025",
        "NETTO",
        "200€"
      )
    )

    // When
    val newTransactions = th.toTransactions(setOfTransactions)

    // Then
    assertThat(newTransactions.size).isEqualTo(2)
    assertThat(newTransactions[0].date).isEqualTo("10.10.2025")
    assertThat(newTransactions[1].date).isEqualTo("11.10.2025")
    assertThat(newTransactions[0].description).isEqualTo("NETTO")
    assertThat(newTransactions[1].description).isEqualTo("NETTO")
    assertThat(newTransactions[0].amount).isEqualTo(100.0)
    assertThat(newTransactions[1].amount).isEqualTo(200.0)
    assertThat(newTransactions[0].category).isEqualTo("")
    assertThat(newTransactions[1].category).isEqualTo("")
  }

  @Test
  fun `correctly categorize transactions`() {
    // Given
    val transactions = listOf(
      Transaction("10.10.2025", 100.0, "REWE"),
      Transaction("11.10.2025", 200.0, "NETTO"),
      Transaction("12.10.2025", 300.0, "IKEA"),
    )
    val rules = listOf(
      CategoryRule("REWE", "Lebensmittel"),
      CategoryRule("NETTO", "Lebensmittel"),
    )

    // When
    val categorizedTransactions = th.categorize(transactions, rules)

    // Then
    assertThat(categorizedTransactions[0].category).isEqualTo("Lebensmittel")
    assertThat(categorizedTransactions[1].category).isEqualTo("Lebensmittel")
    assertThat(categorizedTransactions[2].category).isEqualTo("")
  }

  @Test
  fun `correctly sort transactions`() {
    // Given
    val transactions = listOf(
      Transaction("10.10.2025", 100.0, "REWE"),
      Transaction("11.10.2025", 200.0, "NETTO"),
      Transaction("09.10.2025", 300.0, "IKEA"),
    )

    // When
    val sortedTransactions = th.sortByDate(transactions)

    // Then
    assertThat(sortedTransactions[0].date).isEqualTo("09.10.2025")
    assertThat(sortedTransactions[1].date).isEqualTo("10.10.2025")
    assertThat(sortedTransactions[2].date).isEqualTo("11.10.2025")
  }

}