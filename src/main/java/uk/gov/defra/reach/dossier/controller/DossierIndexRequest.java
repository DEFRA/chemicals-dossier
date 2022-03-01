package uk.gov.defra.reach.dossier.controller;

import java.util.UUID;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DossierIndexRequest {

  @NotNull
  private UUID dossierId;

  @NotEmpty
  private String storageLocation;

}
