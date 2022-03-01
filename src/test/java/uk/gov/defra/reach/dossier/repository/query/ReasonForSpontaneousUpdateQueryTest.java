package uk.gov.defra.reach.dossier.repository.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.defra.reach.dossier.dto.ReasonForUpdate;

class ReasonForSpontaneousUpdateQueryTest {

  private final ReasonForSpontaneousUpdateQuery reasonForSpontaneousUpdateQuery = new ReasonForSpontaneousUpdateQuery();

  @SneakyThrows
  @Test
  void testResultSetExtractor() {
    ResultSet resultSet = Mockito.mock(ResultSet.class);
    doReturn(true, true, false).when(resultSet).next();
    when(resultSet.getString("justificationCode")).thenReturn("j1").thenReturn("j2");
    when(resultSet.getString("justificationText")).thenReturn("t1").thenReturn(null);
    when(resultSet.getString("remarks")).thenReturn("r1").thenReturn("r2");
    List<ReasonForUpdate> reasonForUpdates = reasonForSpontaneousUpdateQuery.getResultSetExtractor().extractData(resultSet);

    assertThat(reasonForUpdates).hasSize(2);
    assertThat(reasonForUpdates.get(0).getJustificationCode()).isEqualTo("j1");
    assertThat(reasonForUpdates.get(0).getJustificationText()).isEqualTo("t1");
    assertThat(reasonForUpdates.get(0).getRemarks()).isEqualTo("r1");
    assertThat(reasonForUpdates.get(1).getJustificationCode()).isEqualTo("j2");
    assertThat(reasonForUpdates.get(1).getJustificationText()).isNull();
    assertThat(reasonForUpdates.get(1).getRemarks()).isEqualTo("r2");
  }

  @Test
  void getSQLQuery_forSU_shouldReturnQueryTemplatePlusDossierIdAndStorageLocation() {
    String suSqlQuery = reasonForSpontaneousUpdateQuery.getSQLQuery();
    assertThat(suSqlQuery).isEqualTo("SELECT   reasons.item.value('(*:Justification/*:value/text())[1]', 'nvarchar(max)') justificationCode,\n" +
        "  reasons.item.value('(*:Justification/*:other/text())[1]', 'nvarchar(max)') justificationText,\n" +
        "  reasons.item.value('(*:Remarks/text())[1]', 'nvarchar(max)') remarks\n" +
        "FROM Dossier.DossierData\n" +
        "CROSS APPLY  Data.nodes('/*:Document/*:Content//*:SpecificSubmissions/*:ReasonForUpdating/*:AfterSpontaneousUpdate/*:entry')" +
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
