package uk.gov.defra.reach.dossier.repository.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.defra.reach.dossier.xpath.DocumentField;
import uk.gov.defra.reach.dossier.xpath.ManifestDocumentXPath;

class AbstractFieldQueryTest {

  private static final String EXPECTED_SQL =
      "SELECT\n"
          + "Data.value('(A_xpath)[1]', 'nvarchar(max)') AS A_field,\n"
          + " Data.value('(B_xpath)[1]', 'nvarchar(max)') AS B_field\n"
          + "FROM Dossier.DossierData\n"
          + "WHERE DossierId = :dossierId\n"
          + "  AND FileName = "
          + "(SELECT Data.value('(/*:manifest/*:contained-documents/*:document[@id=/*:manifest/*:base-document-uuid/text()]/*:name/@*:href)[1]',"
          + " 'nvarchar(max)')\n"
          + "                  FROM Dossier.DossierData\n"
          + "                  WHERE DossierId = :dossierId\n"
          + "                  AND FileName = 'manifest.xml'\n"
          + ");";

  private static final String EXPECTED_TEMP_SQL =
      "SELECT\n"
          + "Data.value('(A_xpath)[1]', 'nvarchar(max)') AS A_field,\n"
          + " Data.value('(B_xpath)[1]', 'nvarchar(max)') AS B_field\n"
          + "FROM Dossier.DossierData\n"
          + "WHERE StorageLocation = :storageLocation\n"
          + "  AND FileName = "
          + "(SELECT Data.value('(/*:manifest/*:contained-documents/*:document[@id=/*:manifest/*:base-document-uuid/text()]/*:name/@*:href)[1]',"
          + " 'nvarchar(max)')\n"
          + "                  FROM Dossier.DossierData\n"
          + "                  WHERE StorageLocation = :storageLocation\n"
          + "                  AND FileName = 'manifest.xml'\n"
          + ");";

  @Test
  void getSQLQuery_shouldContainSelectedFields() {
    TestFieldQuery testQuery = new TestFieldQuery();
    testQuery.addField(TestDocumentField.A);
    testQuery.addField(TestDocumentField.B);

    String sqlQuery = testQuery.getSQLQuery();

    assertThat(sqlQuery).isEqualToIgnoringWhitespace(EXPECTED_SQL);
  }

  @Test
  void getTempSQLQuery_shouldContainSelectedFields() {
    TestFieldQuery testQuery = new TestFieldQuery();
    testQuery.addField(TestDocumentField.A);
    testQuery.addField(TestDocumentField.B);

    String sqlQuery = testQuery.getTempSQLQuery();

    assertThat(sqlQuery).isEqualToIgnoringWhitespace(EXPECTED_TEMP_SQL);
  }

  @Test
  @SneakyThrows
  void resultSetExtractor_shouldExtractSelectedFieldsFromResult() {
    TestFieldQuery testQuery = new TestFieldQuery();
    testQuery.addField(TestDocumentField.A);
    testQuery.addField(TestDocumentField.B);

    ResultSet resultSet = Mockito.mock(ResultSet.class);
    when(resultSet.next()).thenReturn(true, false);
    when(resultSet.getString(TestDocumentField.A.getFieldName())).thenReturn("a value");
    when(resultSet.getString(TestDocumentField.B.getFieldName())).thenReturn("b value");

    Map<String, String> result = testQuery.getResultSetExtractor().extractData(resultSet);

    assertThat(result).contains(
        entry(TestDocumentField.A.getFieldName(), "a value"),
        entry(TestDocumentField.B.getFieldName(), "b value"));
  }

  private class TestFieldQuery extends AbstractFieldQuery<TestDocumentField> {

    public TestFieldQuery() {
      super(ManifestDocumentXPath.DOSSIER_FILE_NAME);
    }
  }

  private enum TestDocumentField implements DocumentField {

    A, B;

    @Override
    public String getXPath() {
      return name() + "_xpath";
    }

    @Override
    public String getFieldName() {
      return name() + "_field";
    }
  }

}
