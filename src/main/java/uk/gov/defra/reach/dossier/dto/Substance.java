package uk.gov.defra.reach.dossier.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Substance {

  private final String substanceName;

  private final String ownerLegalEntityConfidentiality;

  private final String referenceSubstanceConfidentiality;

}
