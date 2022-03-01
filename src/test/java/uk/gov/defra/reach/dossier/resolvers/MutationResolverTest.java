package uk.gov.defra.reach.dossier.resolvers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.reach.dossier.repository.DossierFileRepository;
import uk.gov.defra.reach.dossier.repository.DossierQueryRepository;
import uk.gov.defra.reach.dossier.service.DossierIndexingService;

@ExtendWith(MockitoExtension.class)
class MutationResolverTest {

  @InjectMocks
  private MutationResolver mutationResolver;

  @Mock
  private DossierFileRepository dossierFileRepository;

  @Mock
  private DossierIndexingService dossierIndexingService;

  @Mock
  private DossierQueryRepository dossierQueryRepository;

  @Test
  public void testDeleteDossier_shouldReturnTrue_whenPassedAnId() {
    UUID id = UUID.randomUUID();

    UUID resolvedId = mutationResolver.deleteDossier(id);

    verify(dossierFileRepository).deleteByDossierId(id);
    assertThat(id).isEqualTo(resolvedId);
  }

  @Test
  public void testIndexDossier_shouldCallService() {
    String storageLocation = UUID.randomUUID().toString();
    mutationResolver.indexDossier(storageLocation);
    verify(dossierIndexingService).indexDossier(storageLocation);
  }

  @Test
  public void persistDossier_shouldCallService() {
    String storageLocation = UUID.randomUUID().toString();
    UUID dossierId = UUID.randomUUID();
    mutationResolver.persistDossier(storageLocation, dossierId);
    verify(dossierQueryRepository).persistDossier(storageLocation, dossierId);
  }

  @Test
  public void dossierMaintenance_shouldCallService() {
    mutationResolver.dossierMaintenance();
    verify(dossierQueryRepository).performMaintenance();
  }

}
