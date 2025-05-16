package expenseTracker.core

import io.kotest.core.spec.style.AnnotationSpec
import org.assertj.core.api.Assertions.assertThat

class CategoryMatchesTest : AnnotationSpec() {

  @Test
  fun `category is correctly initialized`() {
    // Given
    val categoryRule = CategoryRule("REWE", "Lebensmittel")

    // When

    // Then
    assertThat(categoryRule.categoryName).isEqualTo("Lebensmittel")
    assertThat(categoryRule.keyword).isEqualTo("REWE")
  }

  @Test
  fun `category matching`() {
    val categoryRule = CategoryRule("REWE", "Lebensmittel")
    val transaction1 = Transaction("10.10.2025", 100.0, "REWE")
    val transaction2 = Transaction("10.10.2025", 100.0, "NETTO")
    assertThat(categoryRule.matches(transaction1)).isTrue
    assertThat(categoryRule.matches(transaction2)).isFalse
  }
}