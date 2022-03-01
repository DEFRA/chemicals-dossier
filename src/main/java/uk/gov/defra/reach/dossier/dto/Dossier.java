package uk.gov.defra.reach.dossier.dto;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Dossier {

  private final UUID id;

  private final String dossierType;

  private final boolean jointSubmission;

  private final String tonnageBand;

  private final String onSiteIsolated;

  private final String transportIsolated;

  private final String storageLocation;

  private final List<ReasonForUpdate> reasonsForSpontaneousUpdate;

  private final List<ReasonForUpdate> reasonsForRequestedUpdate;

}
