package uk.gov.defra.reach.dossier.resolvers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import graphql.language.Field;
import graphql.language.Selection;
import graphql.language.SelectionSet;
import graphql.schema.DataFetchingEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.defra.reach.dossier.dto.Dossier;
import uk.gov.defra.reach.dossier.dto.ReasonForUpdate;
import uk.gov.defra.reach.dossier.dto.ReferenceSubstance;
import uk.gov.defra.reach.dossier.dto.Substance;
import uk.gov.defra.reach.dossier.repository.DossierQueryRepository;
import uk.gov.defra.reach.dossier.repository.query.Query;

@ExtendWith(MockitoExtension.class)
class DossierFieldResolverTest {

  UUID dossierId = UUID.randomUUID();

  String storageLocation = UUID.randomUUID().toString();

  String subName = "Heptanoate";

  @Mock
  private DossierQueryRepository dossierQueryRepository;

  @Mock
  private DataFetchingEnvironment env;

  @InjectMocks
  private DossierFieldResolver dossierFieldResolver;

  @Test
  void getSubstance_shouldReturnSubstance() {
    Dossier dossier = Dossier.builder()
        .id(dossierId)
        .storageLocation(storageLocation)
        .build();

    mockEnv(env);

    when(dossierQueryRepository.queryTempDossier(ArgumentMatchers.<Query<Map<String, String>>>any(), eq(storageLocation)))
        .thenReturn(getMockSubstance());

    Substance sub = dossierFieldResolver.getSubstance(dossier, env);

    assertThat(sub.getSubstanceName()).isEqualTo(subName);
  }

  @Test
  void getSubstance_shouldReturnNull_WhenQueryDossierReturnsNull() {
    Dossier mockDossier = mock(Dossier.class);
    mockEnv(env);
    when(dossierQueryRepository.queryDossier(any(), any())).thenReturn(null);
    assertThat(dossierFieldResolver.getSubstance(mockDossier, env)).isNull();
  }

  @Test
  void getSubstance_shouldReturnNull_WhenQueryTempDossierReturnsNull() {
    Dossier mockDossier = mock(Dossier.class);
    when(mockDossier.getStorageLocation()).thenReturn("");
    mockEnv(env);
    when(dossierQueryRepository.queryTempDossier(any(), any())).thenReturn(null);
    assertThat(dossierFieldResolver.getSubstance(mockDossier, env)).isNull();
  }

  @Test
  void getReferenceSubstance_shouldReturnSubstance() {
    Dossier dossier = Dossier.builder()
        .id(dossierId)
        .build();

    mockEnv(env);

    when(dossierQueryRepository.queryDossier(ArgumentMatchers.<Query<Map<String, String>>>any(), eq(dossierId)))
        .thenReturn(getMockSubstance());

    ReferenceSubstance sub = dossierFieldResolver.getReferenceSubstance(dossier, env);

    assertThat(sub.getIupacName()).isEqualTo(subName);
  }

  @Test
  void getReasonsForSpontaneousUpdate_shouldReturnReasons() {
    Dossier dossier = Dossier.builder()
        .id(dossierId)
        .build();

    List<ReasonForUpdate> reasons = List.of(new ReasonForUpdate("9999", "text", null, "remark"));
    when(dossierQueryRepository.queryDossier(ArgumentMatchers.<Query<List<ReasonForUpdate>>>any(), eq(dossierId)))
        .thenReturn(reasons);

    List<ReasonForUpdate> results = dossierFieldResolver.getReasonsForSpontaneousUpdate(dossier);

    assertThat(results).isEqualTo(reasons);
  }

  @Test
  void getReasonsForRequestedUpdate_shouldReturnReasons() {
    Dossier dossier = Dossier.builder()
        .id(dossierId)
        .build();

    List<ReasonForUpdate> reasons = List.of(new ReasonForUpdate(null, null, "123456789", "remark"));
    when(dossierQueryRepository.queryDossier(ArgumentMatchers.<Query<List<ReasonForUpdate>>>any(), eq(dossierId)))
        .thenReturn(reasons);

    List<ReasonForUpdate> results = dossierFieldResolver.getReasonsForRequestedUpdate(dossier);

    assertThat(results).isEqualTo(reasons);
  }

  private Map<String, String> getMockSubstance() {
    return Map.of(
        "substanceName", subName,
        "iupacName", subName
    );
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
