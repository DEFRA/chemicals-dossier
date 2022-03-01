package uk.gov.defra.reach.dossier.repository.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import uk.gov.defra.reach.dossier.dto.ReasonForUpdate;

/**
 * A query to extract the reasons for update from a dossier
 */
public class ReasonForSpontaneousUpdateQuery extends AbstractReasonForUpdateQuery implements Query<List<ReasonForUpdate>> {

  private static final String SPONTANEOUS_QUERY_TEMPLATE = "SELECT "
      + "  reasons.item.value('(*:Justification/*:value/text())[1]', 'nvarchar(max)') justificationCode,\n"
      + "  reasons.item.value('(*:Justification/*:other/text())[1]', 'nvarchar(max)') justificationText,\n"
      + "  reasons.item.value('(*:Remarks/text())[1]', 'nvarchar(max)') remarks\n"
      + FROM_CROSS_APPLY
      + "  Data.nodes('/*:Document/*:Content//*:SpecificSubmissions/*:ReasonForUpdating/*:AfterSpontaneousUpdate/*:entry') reasons(item)\n"
      + WHERE_CLAUSE;

  String getTemplate() {
    return SPONTANEOUS_QUERY_TEMPLATE;
  }

  ReasonForUpdate buildReasonForUpdate(ResultSet rs) throws SQLException {
    return new ReasonForUpdate(rs.getString("justificationCode"), rs.getString("justificationText"), null, rs.getString("remarks"));
  }
}
