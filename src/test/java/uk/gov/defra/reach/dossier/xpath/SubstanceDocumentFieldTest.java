package uk.gov.defra.reach.dossier.xpath;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SubstanceDocumentFieldTest {

  @Test
  void fromFieldName_returnsField() {
    for (SubstanceDocumentField field : SubstanceDocumentField.values()) {
      assertThat(SubstanceDocumentField.fromFieldName(field.getFieldName())).isSameAs(field);
    }
  }

  @Test
  void fromFieldName_returnsNullForUnknownField() {
    assertThat(SubstanceDocumentField.fromFieldName("unknown")).isNull();
  }
}
