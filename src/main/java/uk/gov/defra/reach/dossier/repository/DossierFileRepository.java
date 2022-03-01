package uk.gov.defra.reach.dossier.repository;

import java.sql.SQLXML;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import uk.gov.defra.reach.dossier.domain.DossierFile;
import uk.gov.defra.reach.dossier.dto.Dossier;

@Repository
public class DossierFileRepository {

  private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

  private final JdbcTemplate jdbcTemplate;
  private final DossierMapper dossierMapper = new DossierMapper();

  public DossierFileRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Persists the data from within a single dossier
   * @param storageLocation of the dossier to be saved
   * @param dossierFiles list of dossier files
   */
  @Transactional
  public void saveDossierData(String storageLocation, List<DossierFile> dossierFiles) {
    List<Object[]> batchArgs = dossierFiles.stream()
        .map(dossierFile -> new Object[]{storageLocation, dossierFile.getName(), dossierFile.getXml()})
        .collect(Collectors.toList());

    jdbcTemplate.batchUpdate("INSERT INTO Dossier.DossierData(StorageLocation, FileName, Data) VALUES (?,?,?)", batchArgs, dossierFiles.size(),
        (ps, argument) -> {
          Document doc = (Document) argument[2];
          SQLXML sqlxml = ps.getConnection().createSQLXML();
          try {
            Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            // Must omit the XML declaration as SQLServer wants UTF-16 for its XML columns rather than the the standard UTF-8.
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(domSource, new StreamResult(sqlxml.setCharacterStream()));

            ps.setString(1, argument[0].toString()); // dossier id
            ps.setString(2, argument[1].toString()); // file name
            ps.setSQLXML(3, sqlxml); // XML
          } catch (TransformerException e) {
            throw new IllegalStateException("Error transforming XML", e);
          }
        });
  }

  @Transactional
  public void deleteByDossierId(UUID dossierId) {
    jdbcTemplate.update("DELETE FROM Dossier.DossierData WHERE DossierId = ?", dossierId);
  }

  @Transactional
  public void deleteByStorageLocation(String storageLocation) {
    jdbcTemplate.update("DELETE FROM Dossier.DossierData WHERE StorageLocation = ?", storageLocation);
  }

  public Dossier getDossierByStorageLocation(String storageLocation) {
    try {
      return jdbcTemplate.queryForObject("SELECT TOP(1) DossierId, StorageLocation FROM Dossier.DossierData WHERE StorageLocation = ?",
          dossierMapper,
          storageLocation);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public Dossier getDossierById(UUID dossierId) {
    try {
      return jdbcTemplate.queryForObject("SELECT TOP(1) DossierId, StorageLocation FROM Dossier.DossierData WHERE DossierId = ?", dossierMapper, dossierId);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

}
