package uk.gov.defra.reach.dossier.repository.query;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.defra.reach.dossier.dto.ReasonForUpdate;

import java.sql.ResultSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

class ReasonForRequestedUpdateQueryTest {

  private final ReasonForRequestedUpdateQuery reasonForRequestedUpdateQuery = new ReasonForRequestedUpdateQuery();

  @SneakyThrows
  @Test
  void testResultSetExtractor() {
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    doReturn(true,  false).when(resultSet).next();
    when(resultSet.getString("number")).thenReturn("123456789");
    when(resultSet.getString("remarks")).thenReturn("r1");
    List<ReasonForUpdate> reasonForUpdates = reasonForRequestedUpdateQuery.getResultSetExtractor().extractData(resultSet);

    assertThat(reasonForUpdates).hasSize(1);
    assertThat(reasonForUpdates.get(0).getNumber()).isEqualTo("123456789");
    assertThat(reasonForUpdates.get(0).getRemarks()).isEqualTo("r1");
  }

  @Test
  void getSQLQuery_forRU_shouldReturnQueryTemplatePlusDossierIdAndStorageLocation() {
    String suSqlQuery = reasonForRequestedUpdateQuery.getSQLQuery();
    assertThat(suSqlQuery).isEqualTo("SELECT " +
        "  reasons.item.value('(*:Number/text())[1]', 'nvarchar(max)') number,\n" +
        "  reasons.item.value('(*:Remarks/text())[1]', 'nvarchar(max)') remarks\n" +
        "FROM Dossier.DossierData\n" +
        "CROSS APPLY  Data.nodes('/*:Document/*:Content//*:SpecificSubmissions/*:ReasonForUpdating/*:AfterRequestDecisionRegulatoryBody/*:entry')" +
        " reasons(item)\n" +
        "WHERE DossierId = :dossierId\n" +
        "AND FileName =(SELECT" +
        " Data.value('(/*:manifest/*:contained-documents/*:document[@id=/*:manifest/*:base-document-uuid/text()]/*:name/@*:href)[1]'," +
        "                 'nvarchar(max)')\n" +
        "               FROM Dossier.DossierData\n" +
        "               WHERE DossierId = :dossierId\n" +
        "               AND FileName = 'manifest.xml');");

  }
}
