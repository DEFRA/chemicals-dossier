package uk.gov.defra.reach.dossier.service;

import static org.apache.xerces.impl.Constants.DISALLOW_DOCTYPE_DECL_FEATURE;
import static org.apache.xerces.impl.Constants.EXTERNAL_GENERAL_ENTITIES_FEATURE;
import static org.apache.xerces.impl.Constants.EXTERNAL_PARAMETER_ENTITIES_FEATURE;
import static org.apache.xerces.impl.Constants.LOAD_EXTERNAL_DTD_FEATURE;
import static org.apache.xerces.impl.Constants.SAX_FEATURE_PREFIX;
import static org.apache.xerces.impl.Constants.XERCES_FEATURE_PREFIX;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import uk.gov.defra.reach.dossier.domain.DossierFile;

/**
 * Reads the XML contents of a zipped dossier.
 */
@Service
public class DossierReader {

  private static final DocumentBuilderFactory DOCUMENT_BUILDER_FACTORY = DocumentBuilderFactory.newInstance();

  private static final String MANIFEST_FILE_NAME = "manifest.xml";

  private static final String DOSSIER_DOCUMENT_FILE_EXTENSION = ".i6d";

  static {
    DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
    DOCUMENT_BUILDER_FACTORY.setXIncludeAware(false);
    DOCUMENT_BUILDER_FACTORY.setExpandEntityReferences(false);

    try {
      DOCUMENT_BUILDER_FACTORY.setFeature(XERCES_FEATURE_PREFIX + DISALLOW_DOCTYPE_DECL_FEATURE, true);
      DOCUMENT_BUILDER_FACTORY.setFeature(SAX_FEATURE_PREFIX + EXTERNAL_GENERAL_ENTITIES_FEATURE, false);
      DOCUMENT_BUILDER_FACTORY.setFeature(SAX_FEATURE_PREFIX + EXTERNAL_PARAMETER_ENTITIES_FEATURE, false);
      DOCUMENT_BUILDER_FACTORY.setFeature(XERCES_FEATURE_PREFIX + LOAD_EXTERNAL_DTD_FEATURE, false);
    } catch (ParserConfigurationException e) {
      throw new IllegalStateException("Error configuring document builder factory");
    }
    DOCUMENT_BUILDER_FACTORY.setNamespaceAware(true);
  }


  /**
   * Extracts the contents of XML files from within a zipped dossier.
   *
   * @param inputStream input stream for the dossier
   * @return list of dossier files with filename and contents
   */
  public List<DossierFile> readDossier(InputStream inputStream) {
    List<DossierFile> dossierFiles = new ArrayList<>();

    try (ZipInputStream zis = new ZipInputStream(inputStream)) {
      ZipEntry zipEntry;
      while ((zipEntry = zis.getNextEntry()) != null) {
        NonClosingInputStream ncis = new NonClosingInputStream(zis);
        if (zipEntry.getName().equals(MANIFEST_FILE_NAME) || zipEntry.getName().endsWith(DOSSIER_DOCUMENT_FILE_EXTENSION)) {
          DocumentBuilder documentBuilder = DOCUMENT_BUILDER_FACTORY.newDocumentBuilder();
          Document document = documentBuilder.parse(ncis);
          dossierFiles.add(new DossierFile(zipEntry.getName(), document));
        }
        zis.closeEntry();
      }
    } catch (IOException | ParserConfigurationException | SAXException e) {
      throw new DossierParseException("Error reading dossier file", e);
    }

    return dossierFiles;
  }

  /**
   * InputStream which does not close the underlying stream to allow individual files to be read from within a zipped stream without it closing prematurely.
   */
  private static class NonClosingInputStream extends FilterInputStream {

    protected NonClosingInputStream(InputStream in) {
      super(in);
    }

    @Override
    public void close() {
      // Do nothing
    }
  }

}
