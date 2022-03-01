package uk.gov.defra.reach.dossier.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.reach.dossier.service.DossierIndexingService;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class DossierControllerTest {

  @InjectMocks
  private DossierController dossierController;

  @Mock
  private DossierIndexingService dossierIndexingService;

  private static String OK = "ok";
  @Test
  public void indexDossier_callsService() {
    DossierIndexRequest dossierIndexRequest = mock(DossierIndexRequest.class);
    String storageLocation = UUID.randomUUID().toString();
    when(dossierIndexRequest.getStorageLocation()).thenReturn(storageLocation);

    dossierController.indexDossier(dossierIndexRequest);

    verify(dossierIndexingService).indexDossier(storageLocation);
  }

  @Test
  public void root_ReturnsOk() {
    assertThat(dossierController.root()).isEqualTo(OK);
  }

}
