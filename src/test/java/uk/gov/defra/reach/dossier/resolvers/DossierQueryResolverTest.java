package uk.gov.defra.reach.dossier.resolvers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import graphql.language.Field;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.reach.dossier.dto.Dossier;
import uk.gov.defra.reach.dossier.repository.DossierFileRepository;
import uk.gov.defra.reach.dossier.repository.DossierQueryRepository;
import uk.gov.defra.reach.dossier.repository.query.DossierDocumentFieldQuery;

@ExtendWith(MockitoExtension.class)
class DossierQueryResolverTest {

  @Mock
  private DossierFileRepository dossierFileRepository;

  @Mock
  private DossierQueryRepository dossierQueryRepository;

  @Mock
  private DataFetchingEnvironment env;

  @InjectMocks
  private DossierQueryResolver resolver;

  @Test
  void getDossier_returnsNull_WhenNoDossierIsFound() {
    when(dossierFileRepository.getDossierById(any())).thenReturn(null);
    Dossier dossier = resolver.getDossier(UUID.randomUUID().toString(), env);
    assertThat(dossier).isNull();
  }


  @Test
  void getTempDossier_returnsNull_whenNoDossierIsFound() {
    when(dossierFileRepository.getDossierByStorageLocation(any())).thenReturn(null);
    Dossier dossier = resolver.getTempDossier(UUID.randomUUID().toString(), env);
    assertThat(dossier).isNull();
  }

  @Test
  void getDossierDetails_fillsInInformationCorrectlyBasedOnquery() {
    mockEnv(env);
    UUID dossierId = UUID.randomUUID();
    String storageLocation = UUID.randomUUID().toString();
    Dossier partialDossier = Dossier.builder()
        .id(dossierId)
        .storageLocation(storageLocation)
        .build();

    when(dossierFileRepository.getDossierById(any())).thenReturn(partialDossier);
    Dossier dossier = resolver.getDossier(dossierId.toString(), env);
    assertThat(dossier.getId()).isEqualTo(dossierId);
    assertThat(dossier.getStorageLocation()).isEqualTo(storageLocation);
  }

  @Test
  void getDossierDetails_fillsInInformationWhenResultsFromQueryAreNotNull() {
    mockEnv(env);
    UUID dossierId = UUID.randomUUID();
    String storageLocation = UUID.randomUUID().toString();
    Dossier partialDossier = Dossier.builder()
            .id(dossierId)
            .storageLocation(storageLocation)
            .build();
    String dossierType = "R_ABOVE_1000";
    String tonnageBand = "TONNAGE_10_TO_100";
    String onsiteIsolated = "ONSITE_ISOLATED_TONNAGE_10_TO_100";
    String transportIsolated = "TRANSPORT_ISOLATED_TONNAGE_10_TO_100";
    Map<String, String> result = Map.of(
            "jointSubmission", "true",
            "dossierType", dossierType,
            "tonnageBand", tonnageBand,
            "onsiteIsolated", onsiteIsolated,
            "transportIsolated", transportIsolated
    );
    when(dossierFileRepository.getDossierById(any())).thenReturn(partialDossier);
    when(dossierQueryRepository.queryTempDossier(any(DossierDocumentFieldQuery.class), any(String.class)))
            .thenReturn(result);
    Dossier dossier = resolver.getDossier(dossierId.toString(), env);
    assertThat(dossier.getId()).isEqualTo(dossierId);
    assertThat(dossier.getStorageLocation()).isEqualTo(storageLocation);
    assertThat(dossier.isJointSubmission()).isTrue();
    assertThat(dossier.getDossierType()).isEqualTo(dossierType);
    assertThat(dossier.getTonnageBand()).isEqualTo(tonnageBand);
    assertThat(dossier.getOnSiteIsolated()).isEqualTo(onsiteIsolated);
    assertThat(dossier.getTransportIsolated()).isEqualTo(transportIsolated);
  }

  @Test
  void getDossierDetails_QueriesForTempDossier_WhenDossierStorageLocationIsNotNull() {
    mockEnv(env);
    UUID dossierId = UUID.randomUUID();
    String storageLocation = UUID.randomUUID().toString();
    Dossier partialDossier = Dossier.builder()
            .id(dossierId)
            .storageLocation(storageLocation)
            .build();

    when(dossierFileRepository.getDossierById(any())).thenReturn(partialDossier);
    resolver.getDossier(dossierId.toString(), env);
    verify(dossierQueryRepository).queryTempDossier(any(DossierDocumentFieldQuery.class), any(String.class));
  }

  @Test
  void getDossierDetails_QueriesForDossier_WhenDossierStorageLocationIsNull() {
    mockEnv(env);
    UUID dossierId = UUID.randomUUID();
    Dossier partialDossier = Dossier.builder()
            .id(dossierId)
            .storageLocation(null)
            .build();

    when(dossierFileRepository.getDossierById(any())).thenReturn(partialDossier);
    resolver.getDossier(dossierId.toString(), env);
    verify(dossierQueryRepository).queryDossier(any(DossierDocumentFieldQuery.class), any(UUID.class));
  }

  private void mockEnv(DataFetchingEnvironment env) {
    Field field = mock(Field.class);
    SelectionSet selectionSet = mock(SelectionSet.class);
    List<Selection> selections = List.of(new Field("dossierType"));
    when(env.getField()).thenReturn(field);
    when(field.getSelectionSet()).thenReturn(selectionSet);
    when(selectionSet.getSelections()).thenReturn(selections);
  }
}
