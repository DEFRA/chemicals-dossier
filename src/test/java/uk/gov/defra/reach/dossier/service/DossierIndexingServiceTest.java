package uk.gov.defra.reach.dossier.service;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.reach.dossier.controller.DossierIndexRequest;
import uk.gov.defra.reach.dossier.domain.DossierFile;
import uk.gov.defra.reach.dossier.domain.StorageContainer;
import uk.gov.defra.reach.dossier.dto.Dossier;
import uk.gov.defra.reach.dossier.repository.DossierFileRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class DossierIndexingServiceTest {

  @InjectMocks
  private DossierIndexingService dossierIndexingService;

  @Mock
  private DossierLoader dossierLoader;

  @Mock
  private DossierReader dossierReader;

  @Mock
  private DossierFileRepository dossierFileRepository;

  @Test
  public void indexDossier_obtainsMapsAndStoresDossier() {
    DossierIndexRequest dossierIndexRequest = buildDossierIndexRequest();

    InputStream inputStream = Mockito.mock(InputStream.class);
    List<DossierFile> dossierFiles = List.of(new DossierFile("manifest", null));

    when(dossierLoader.loadDossier(StorageContainer.TEMPORARY, dossierIndexRequest.getStorageLocation())).thenReturn(inputStream);
    when(dossierReader.readDossier(inputStream)).thenReturn(dossierFiles);

    dossierIndexingService.indexDossier(dossierIndexRequest.getStorageLocation());

    verify(dossierFileRepository).saveDossierData(dossierIndexRequest.getStorageLocation(), dossierFiles);
  }

  private static DossierIndexRequest buildDossierIndexRequest() {
    DossierIndexRequest dossierIndexRequest = new DossierIndexRequest();
    dossierIndexRequest.setDossierId(UUID.randomUUID());
    dossierIndexRequest.setStorageLocation("aStorageLocation");
    return dossierIndexRequest;
  }

  @Test
  public void indexDossier_deletesExistingDossier_whenDossierAlreadyExists() {
    String storageLocation = "storageLocation";
    Dossier mockDossier = mock(Dossier.class);
    when(dossierFileRepository.getDossierByStorageLocation(storageLocation)).thenReturn(mockDossier);
    dossierIndexingService.indexDossier(storageLocation);
    verify(dossierFileRepository, times(1)).deleteByStorageLocation(storageLocation);
  }
}
