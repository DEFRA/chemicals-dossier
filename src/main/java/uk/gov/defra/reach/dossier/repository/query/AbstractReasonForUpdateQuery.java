package uk.gov.defra.reach.dossier.repository.query;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.ResultSetExtractor;
import uk.gov.defra.reach.dossier.dto.ReasonForUpdate;

/**
 * A query to extract the reasons for update from a dossier
 */
abstract class AbstractReasonForUpdateQuery implements Query<List<ReasonForUpdate>> {

  static final String FROM_CROSS_APPLY = "FROM Dossier.DossierData\n"
      + "CROSS APPLY";

  static final String WHERE_CLAUSE = "WHERE %1$s\n"
      + "AND FileName =(SELECT Data.value('(/*:manifest/*:contained-documents/*:document[@id=/*:manifest/*:base-document-uuid/text()]/*:name/@*:href)[1]',"
      + "                 'nvarchar(max)')\n"
      + "               FROM Dossier.DossierData\n"
      + "               WHERE %1$s\n"
      + "               AND FileName = 'manifest.xml');";

  @Override
  public String getSQLQuery() {
    return String.format(getTemplate(), "DossierId = :dossierId");
  }

  @Override
  public String getTempSQLQuery() {
    return String.format(getTemplate(), "StorageLocation = :storageLocation");
  }

  abstract String getTemplate();

  abstract ReasonForUpdate buildReasonForUpdate(ResultSet rs) throws java.sql.SQLException;

  @Override
  public ResultSetExtractor<List<ReasonForUpdate>> getResultSetExtractor() {
    return rs -> {
      List<ReasonForUpdate> reasonsForUpdate = new ArrayList<>();
      while (rs.next()) {
        reasonsForUpdate.add(buildReasonForUpdate(rs));
      }
      return reasonsForUpdate;
    };
  }
}
