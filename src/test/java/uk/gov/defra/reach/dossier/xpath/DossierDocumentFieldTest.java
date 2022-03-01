package uk.gov.defra.reach.dossier.xpath;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DossierDocumentFieldTest {

  @Test
  void fromFieldName_returnsField() {
    for (DossierDocumentField field : DossierDocumentField.values()) {
      assertThat(DossierDocumentField.fromFieldName(field.getFieldName())).isSameAs(field);
    }
  }

  @Test
  void fromFieldName_returnsNullForUnknownField() {
    assertThat(DossierDocumentField.fromFieldName("unknown")).isNull();
  }

}
