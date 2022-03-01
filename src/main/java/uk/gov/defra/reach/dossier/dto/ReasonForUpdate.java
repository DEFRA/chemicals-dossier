package uk.gov.defra.reach.dossier.dto;

import lombok.Data;

@Data
public class ReasonForUpdate {

  private final String justificationCode;

  private final String justificationText;

  private final String number;

  private final String remarks;

}
