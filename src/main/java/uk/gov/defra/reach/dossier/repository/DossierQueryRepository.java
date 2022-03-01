package uk.gov.defra.reach.dossier.repository;

import java.util.Map;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.defra.reach.dossier.config.DossierConfig;
import uk.gov.defra.reach.dossier.repository.query.Query;

@Repository
public class DossierQueryRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;
  private final DossierConfig dossierConfig;

  public DossierQueryRepository(NamedParameterJdbcTemplate jdbcTemplate, DossierConfig dossierConfig) {
    this.jdbcTemplate = jdbcTemplate;
    this.dossierConfig = dossierConfig;
  }

  public <T> T queryDossier(Query<T> query, UUID dossierId) {
    return jdbcTemplate.query(query.getSQLQuery(), Map.of("dossierId", dossierId), query.getResultSetExtractor());
  }

  public <T> T queryTempDossier(Query<T> query, String storageLocation) {
    return jdbcTemplate.query(query.getTempSQLQuery(), Map.of("storageLocation", storageLocation), query.getResultSetExtractor());
  }

  public void persistDossier(String storageLocation, UUID dossierId) {
    jdbcTemplate.update(
        "UPDATE dossier.DossierData " +
        "SET StorageState = 'PRODUCTION', " +
        "    DossierId = :dossierId " +
        "WHERE " +
        "  StorageLocation = :storageLocation",
        Map.of(
            "storageLocation", storageLocation,
            "dossierId", dossierId
        ));
  }

  /**
   * Delete obsolete temporary dossiers
   * @return number of files deleted from database.
   */
  public int performMaintenance() {
    return jdbcTemplate.update("DELETE FROM Dossier.DossierData\n"
                               + "WHERE DossierData.StorageState = 'TEMPORARY' AND\n"
                               + "DossierData.CreatedAt < DATEADD(day, :days, GETDATE());",
                               Map.of("days", -1 * dossierConfig.getDaysToExpire()));
  }

}
