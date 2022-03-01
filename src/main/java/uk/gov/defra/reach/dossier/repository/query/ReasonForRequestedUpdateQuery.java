package uk.gov.defra.reach.dossier.repository.query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import uk.gov.defra.reach.dossier.dto.ReasonForUpdate;

/**
 * A query to extract the reasons for update from a dossier
 */
public class ReasonForRequestedUpdateQuery extends AbstractReasonForUpdateQuery implements Query<List<ReasonForUpdate>> {

  private static final String REQUESTED_QUERY_TEMPLATE = "SELECT "
      + "  reasons.item.value('(*:Number/text())[1]', 'nvarchar(max)') number,\n"
      + "  reasons.item.value('(*:Remarks/text())[1]', 'nvarchar(max)') remarks\n"
      + FROM_CROSS_APPLY
      + "  Data.nodes('/*:Document/*:Content//*:SpecificSubmissions/*:ReasonForUpdating/*:AfterRequestDecisionRegulatoryBody/*:entry') reasons(item)\n"
      + WHERE_CLAUSE;

  String getTemplate() {
    return REQUESTED_QUERY_TEMPLATE;
  }

  ReasonForUpdate buildReasonForUpdate(ResultSet rs) throws SQLException {
    return new ReasonForUpdate(null, null, rs.getString("number"), rs.getString("remarks"));
  }
}
