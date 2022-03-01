package uk.gov.defra.reach.dossier.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import uk.gov.defra.reach.dossier.config.DossierConfig;
import uk.gov.defra.reach.dossier.repository.query.DossierDocumentFieldQuery;

@ExtendWith(MockitoExtension.class)
public class DossierQueryRepositoryTest {

  @Mock
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Mock
  private DossierConfig dossierConfig;

  @InjectMocks
  private DossierQueryRepository dossierQueryRepository;


  @Test
  void queryDossier_shouldTriggerQuery() {
    DossierDocumentFieldQuery query = mock(DossierDocumentFieldQuery.class);
    when(query.getSQLQuery()).thenReturn("QUERY");
    when(query.getResultSetExtractor()).thenReturn(mock(ResultSetExtractor.class));
    dossierQueryRepository.queryDossier(query, UUID.randomUUID());
    verify(jdbcTemplate).query(any(String.class), any(Map.class), any(ResultSetExtractor.class));
  }

  @Test
  void queryTempDossier_shouldTriggerQuery() {
    DossierDocumentFieldQuery query = mock(DossierDocumentFieldQuery.class);
    when(query.getTempSQLQuery()).thenReturn("QUERY");
    when(query.getResultSetExtractor()).thenReturn(mock(ResultSetExtractor.class));
    dossierQueryRepository.queryTempDossier(query, UUID.randomUUID().toString());
    verify(jdbcTemplate).query(any(String.class), any(Map.class), any(ResultSetExtractor.class));
  }

  @Test
  void persistDossier_shouldTriggerQuery() {
    dossierQueryRepository.persistDossier(UUID.randomUUID().toString(), UUID.randomUUID());
    verify(jdbcTemplate).update(any(String.class), any(Map.class));
  }

  @Test
  void performMaintenance_shouldTriggerQuery() {
    when(dossierConfig.getDaysToExpire()).thenReturn(1);
    dossierQueryRepository.performMaintenance();
    verify(jdbcTemplate).update(any(String.class), any(Map.class));
  }

}
