package uk.gov.defra.reach.dossier.service;

import java.io.InputStream;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.defra.reach.dossier.domain.DossierFile;
import uk.gov.defra.reach.dossier.domain.StorageContainer;
import uk.gov.defra.reach.dossier.dto.Dossier;
import uk.gov.defra.reach.dossier.repository.DossierFileRepository;

/**
 * Service which loads a dossier archive, parses the data and stores within the data store.
 */
@Service
@Slf4j
public class DossierIndexingService {

  private final DossierLoader dossierLoader;

  private final DossierReader dossierReader;

  private final DossierFileRepository dossierFileRepository;

  public DossierIndexingService(DossierLoader dossierLoader, DossierReader dossierReader,
      DossierFileRepository dossierFileRepository) {
    this.dossierLoader = dossierLoader;
    this.dossierReader = dossierReader;
    this.dossierFileRepository = dossierFileRepository;
  }

  /**
   * Stores a dossier within the data store.
   *
   * @param storageLocation details of the dossier to be stored.
   */
  public String indexDossier(String storageLocation) {
    log.info("Indexing dossier: {}", storageLocation);
    Dossier dossier = dossierFileRepository.getDossierByStorageLocation(storageLocation);
    if (dossier != null) {
      log.info("Dossier already exists at storageLocation: {}, overwriting", storageLocation);
      dossierFileRepository.deleteByStorageLocation(storageLocation);
    }
    InputStream inputStream = dossierLoader.loadDossier(StorageContainer.TEMPORARY, storageLocation);
    List<DossierFile> dossierFiles = dossierReader.readDossier(inputStream);
    dossierFileRepository.saveDossierData(storageLocation, dossierFiles);
    return storageLocation;
  }
}
