package uk.gov.defra.reach.dossier.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.gov.defra.reach.dossier.config.DossierConfig;
import uk.gov.defra.reach.dossier.domain.DossierFile;

@JdbcTest
@AutoConfigureTestDatabase(replace= Replace.NONE)
@ComponentScan
@TestPropertySource(locations = "classpath:application-dev.properties")
@ContextConfiguration(classes = {DossierConfig.class})
class DossierFileRepositoryIT {

  @Autowired
  private DossierFileRepository dossierFileRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private DossierConfig dossierConfig;

  @SuppressWarnings("ConstantConditions")
  @Test
  void saveDossierFiles_shouldSaveAllDossierFiles() {
    List<DossierFile> dossierFiles = List.of(new DossierFile("test1", createTestDocument()), new DossierFile("test2", createTestDocument()));
    String storageLocation = UUID.randomUUID().toString();
    dossierFileRepository.saveDossierData(storageLocation, dossierFiles);

    Integer count = jdbcTemplate.queryForObject("select count(*) from Dossier.DossierData where StorageLocation = '" + storageLocation + "'", Integer.class);
    assertThat(count).isEqualTo(2);

    SqlRowSet rowSet = jdbcTemplate.queryForRowSet("select * from Dossier.DossierData where StorageLocation = '" + storageLocation + "' order by FileName");
    rowSet.first();
    assertThat(rowSet.getString("StorageLocation")).isEqualTo(storageLocation);
    assertThat(rowSet.getString("FileName")).isEqualTo("test1");
    assertThat(rowSet.getString("Data")).isEqualTo("<testing><onetwothree/></testing>");
    rowSet.next();
    assertThat(rowSet.getString("StorageLocation")).isEqualTo(storageLocation);
    assertThat(rowSet.getString("FileName")).isEqualTo("test2");
    assertThat(rowSet.getString("Data")).isEqualTo("<testing><onetwothree/></testing>");
  }

  @Test
  void deleteByDossierId_shouldDeleteAllDossierFiles() {
    List<DossierFile> dossierFiles = List.of(new DossierFile("test1", createTestDocument()), new DossierFile("test2", createTestDocument()));
    UUID dossierId = UUID.randomUUID();
    String storageLocation = UUID.randomUUID().toString();
    dossierFileRepository.saveDossierData(storageLocation, dossierFiles);

    dossierFileRepository.deleteByDossierId(dossierId);

    Integer count = jdbcTemplate.queryForObject("select count(*) from Dossier.DossierData where DossierId = '" + dossierId.toString() + "'", Integer.class);
    assertThat(count).isEqualTo(0);
  }

  @SneakyThrows
  private static Document createTestDocument() {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    Document doc = docBuilder.newDocument();
    Element rootElement = doc.createElement( "testing" );
    doc.appendChild( rootElement );
    Element element = doc.createElement( "onetwothree" );
    rootElement.appendChild( element );
    return doc;
  }

}
