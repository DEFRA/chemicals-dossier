package uk.gov.defra.reach.dossier.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

class DossierIndexRequestTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  public void testValidation() {
    DossierIndexRequest dossierIndexRequest = new DossierIndexRequest();

    assertThat(validator.validate(dossierIndexRequest)).hasSize(2);

    dossierIndexRequest.setDossierId(UUID.randomUUID());
    assertThat(validator.validate(dossierIndexRequest)).hasSize(1);

    dossierIndexRequest.setStorageLocation("location");
    assertThat(validator.validate(dossierIndexRequest)).isEmpty();
  }

}
