package uk.gov.defra.reach.dossier.xpath;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ReferenceSubstanceDocumentFieldTest {

  @Test
  void fromFieldName_returnsField() {
    for (ReferenceSubstanceDocumentField field : ReferenceSubstanceDocumentField.values()) {
      assertThat(ReferenceSubstanceDocumentField.fromFieldName(field.getFieldName())).isSameAs(field);
    }
  }

  @Test
  void fromFieldName_returnsNullForUnknownField() {
    assertThat(ReferenceSubstanceDocumentField.fromFieldName("unknown")).isNull();
  }

}
