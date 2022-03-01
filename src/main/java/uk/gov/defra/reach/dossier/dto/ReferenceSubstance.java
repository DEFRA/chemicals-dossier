package uk.gov.defra.reach.dossier.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReferenceSubstance {

  private final String referenceSubstanceName;

  private final String molecularFormula;

  private final String ecNumber;

  private final String casNumber;

  private final String iupacName;

  private final String referenceSubstanceInfoConfidentiality;

  private final String molecularStructureConfidentiality;
  
}
