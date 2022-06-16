package uk.gov.defra.reach.dossier.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.w3c.dom.Document;
import uk.gov.defra.reach.dossier.config.DossierConfig;
import uk.gov.defra.reach.dossier.domain.DossierFile;
import uk.gov.defra.reach.dossier.repository.query.DossierDocumentFieldQuery;
import uk.gov.defra.reach.dossier.xpath.DossierDocumentField;

@JdbcTest
@AutoConfigureTestDatabase(replace= Replace.NONE)
@ComponentScan
@TestPropertySource(locations = "classpath:application-dev.properties")
@ContextConfiguration(classes = {DossierConfig.class})
class DossierQueryRepositoryIT {

  @Autowired
  private DossierFileRepository dossierFileRepository;

  @Autowired
  private DossierQueryRepository dossierQueryRepository;

  @Test
  void queryDossier_shouldExtractValuesFromDossierFiles() {
    String storageLocation = UUID.randomUUID().toString();
    UUID dossierId = UUID.randomUUID();
    dossierFileRepository.saveDossierData(storageLocation, loadTestDocuments());
    dossierQueryRepository.persistDossier(storageLocation, dossierId);

    DossierDocumentFieldQuery queryBuilder = new DossierDocumentFieldQuery();
    queryBuilder.addField(DossierDocumentField.DOSSIER_TYPE);
    Map<String, String> result = dossierQueryRepository.queryDossier(queryBuilder, dossierId);

    assertThat(result).contains(entry(DossierDocumentField.DOSSIER_TYPE.getFieldName(), "R_INQUIRY"));
  }

  @Test
  void queryTempDossier_shouldExtractValuesFromTempDossierFiles() {
    String storageLocation = UUID.randomUUID().toString();
    dossierFileRepository.saveDossierData(storageLocation, loadTestDocuments());

    DossierDocumentFieldQuery queryBuilder = new DossierDocumentFieldQuery();
    queryBuilder.addField(DossierDocumentField.DOSSIER_TYPE);
    Map<String, String> result = dossierQueryRepository.queryTempDossier(queryBuilder, storageLocation);

    assertThat(result).contains(entry(DossierDocumentField.DOSSIER_TYPE.getFieldName(), "R_INQUIRY"));
  }

  @Test
  void getDossierByStorageLocation_shouldCheckForDossierExistance() {
    String storageLocation = UUID.randomUUID().toString();
    assertThat(dossierFileRepository.getDossierByStorageLocation(storageLocation)).isNull();
    dossierFileRepository.saveDossierData(storageLocation, loadTestDocuments());
    assertThat(dossierFileRepository.getDossierByStorageLocation(storageLocation)).isNotNull();
  }

  @Test
  void getDossierById_shouldCheckForDossierExistance() {
    UUID dossierId = UUID.randomUUID();
    String storageLocation = UUID.randomUUID().toString();
    assertThat(dossierFileRepository.getDossierById(dossierId)).isNull();
    dossierFileRepository.saveDossierData(storageLocation, loadTestDocuments());
    dossierQueryRepository.persistDossier(storageLocation, dossierId);
    assertThat(dossierFileRepository.getDossierById(dossierId)).isNotNull();
  }

  @SneakyThrows
  private static List<DossierFile> loadTestDocuments() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
    Document manifestDocument = documentBuilder.parse(DossierQueryRepositoryIT.class.getResourceAsStream("/xml/manifest.xml"));
    Document dossierDocument = documentBuilder.parse(DossierQueryRepositoryIT.class.getResourceAsStream("/xml/dossier.xml"));

    return List.of(
        new DossierFile("manifest.xml", manifestDocument),
        new DossierFile("719e351f-3bde-41c3-baba-ceb3f7af23c8_719e351f-3bde-41c3-baba-ceb3f7af23c8.i6d", dossierDocument));
  }

}
