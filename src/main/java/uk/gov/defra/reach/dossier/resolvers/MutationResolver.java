package uk.gov.defra.reach.dossier.resolvers;

import graphql.kickstart.tools.GraphQLMutationResolver;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.defra.reach.dossier.repository.DossierFileRepository;
import uk.gov.defra.reach.dossier.repository.DossierQueryRepository;
import uk.gov.defra.reach.dossier.service.DossierIndexingService;

@Component
@Slf4j
public class MutationResolver implements GraphQLMutationResolver {

  private final DossierFileRepository dossierFileRepository;
  private final DossierIndexingService dossierIndexingService;
  private final DossierQueryRepository dossierQueryRepository;

  @Autowired
  MutationResolver(
      DossierFileRepository dossierFileRepository,
      DossierIndexingService dossierIndexingService,
      DossierQueryRepository dossierQueryRepository) {
    this.dossierFileRepository = dossierFileRepository;
    this.dossierIndexingService = dossierIndexingService;
    this.dossierQueryRepository = dossierQueryRepository;
  }

  public UUID deleteDossier(UUID id) {
    log.info("Deleting dossier " + id);
    dossierFileRepository.deleteByDossierId(id);
    return id;
  }

  public String indexDossier(String storageLocation) {
    return dossierIndexingService.indexDossier(storageLocation);
  }

  public UUID persistDossier(String storageLocation, UUID dossierId) {
    log.info("Persisting " + storageLocation + " with id " + dossierId);
    dossierQueryRepository.persistDossier(storageLocation, dossierId);
    return dossierId;
  }

  public Integer dossierMaintenance() {
    log.info("Performing cleanup of temp dossiers");
    int deletedRecords = dossierQueryRepository.performMaintenance();
    log.info("Cleanup deleted " + deletedRecords + " items");
    return deletedRecords;
  }



}
