package uk.gov.defra.reach.dossier.repository.query;

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.ResultSetExtractor;
import uk.gov.defra.reach.dossier.xpath.DocumentField;
import uk.gov.defra.reach.dossier.xpath.ManifestDocumentXPath;

/**
 * A query for extracting one or more field values from a single dossier document.
 *
 * @param <T> the type of fields relevant to the dossier document.
 */
public abstract class AbstractFieldQuery<T extends DocumentField> implements Query<Map<String, String>> {

  private static final String QUERY_TEMPLATE = "SELECT\n"
      + "%s\n"
      + "FROM Dossier.DossierData\n"
      + "WHERE DossierId = :dossierId\n"
      + "  AND FileName = (SELECT Data.value('(%s)[1]', 'nvarchar(max)')\n"
      + "                  FROM Dossier.DossierData\n"
      + "                  WHERE DossierId = :dossierId\n"
      + "                  AND FileName = 'manifest.xml'\n"
      + ");";

  private static final String TEMP_QUERY_TEMPLATE = "SELECT\n"
                                               + "%s\n"
                                               + "FROM Dossier.DossierData\n"
                                               + "WHERE StorageLocation = :storageLocation\n"
                                               + "  AND FileName = (SELECT Data.value('(%s)[1]', 'nvarchar(max)')\n"
                                               + "                  FROM Dossier.DossierData\n"
                                               + "                  WHERE StorageLocation = :storageLocation\n"
                                               + "                  AND FileName = 'manifest.xml'\n"
                                               + ");";


  private final ManifestDocumentXPath documentXPath;

  private final Set<T> fields = new LinkedHashSet<>();

  public AbstractFieldQuery(ManifestDocumentXPath documentXPath) {
    this.documentXPath = documentXPath;
  }

  public void addField(T xpath) {
    fields.add(xpath);
  }

  @Override
  public String getSQLQuery() {
    String selects = fields.stream()
        .map(xpath -> "Data.value('(" + xpath.getXPath() + ")[1]', 'nvarchar(max)') AS " + xpath.getFieldName())
        .collect(Collectors.joining(",\n "));
    return String.format(QUERY_TEMPLATE, selects, documentXPath.getXPath());
  }

  @Override
  public String getTempSQLQuery() {
    String selects = fields.stream()
        .map(xpath -> "Data.value('(" + xpath.getXPath() + ")[1]', 'nvarchar(max)') AS " + xpath.getFieldName())
        .collect(Collectors.joining(",\n "));
    return String.format(TEMP_QUERY_TEMPLATE, selects, documentXPath.getXPath());
  }

  @Override
  public ResultSetExtractor<Map<String, String>> getResultSetExtractor() {
    return rs -> {
      if (rs.next()) {
        return fields.stream().collect(Collectors.toMap(DocumentField::getFieldName, xpath -> {
          String value;
          try {
            value = rs.getString(xpath.getFieldName());
          } catch (SQLException e) {
            throw new IllegalStateException("Error occurred extracting results", e);
          }
          return Objects.requireNonNullElse(value, "");
        }));
      } else {
        return null;
      }
    };
  }

  public boolean hasFields() {
    return !fields.isEmpty();
  }

}
