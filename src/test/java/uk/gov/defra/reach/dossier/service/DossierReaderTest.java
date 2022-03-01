package uk.gov.defra.reach.dossier.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;
import uk.gov.defra.reach.dossier.domain.DossierFile;

public class DossierReaderTest {

  private final DossierReader dossierReader = new DossierReader();

  @Test
  public void readDossier_extractsFiles() {
    List<DossierFile> dossierFiles = dossierReader.readDossier(createZippedDossierInputStream());
    assertThat(dossierFiles).hasSize(2);
    assertThat(dossierFiles).extracting("name").contains("manifest.xml", "document.i6d");
  }

  @Test
  public void readDossier_errorsForUnreadableFiles() {
    assertThatExceptionOfType(DossierParseException.class)
        .isThrownBy(() -> dossierReader.readDossier(createBrokenZippedDossierInputStream()));
  }

  @SneakyThrows
  @Test
  public void readDossier_errorsForMaliciousFile() {
    File dossierFile = ResourceUtils.getFile("classpath:dossiers/billion-laughs.i6z");
    FileInputStream fileInputStream = new FileInputStream(dossierFile);

    assertThatExceptionOfType(DossierParseException.class)
        .isThrownBy(() -> dossierReader.readDossier(fileInputStream));
  }

  @SneakyThrows
  private static InputStream createZippedDossierInputStream() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try (
        ZipOutputStream zout = new ZipOutputStream(baos);
        InputStream manifestStream = DossierReaderTest.class.getResourceAsStream("/xml/manifest.xml");
        InputStream documentStream = DossierReaderTest.class.getResourceAsStream("/xml/dossier.xml");
    ) {
      ZipEntry manifestEntry = new ZipEntry("manifest.xml");
      zout.putNextEntry(manifestEntry);
      zout.write(manifestStream.readAllBytes());
      zout.closeEntry();

      ZipEntry documentEntry = new ZipEntry("document.i6d");
      zout.putNextEntry(documentEntry);
      zout.write(documentStream.readAllBytes());
      zout.closeEntry();

      ZipEntry randomEntry = new ZipEntry("random.txt");
      zout.putNextEntry(randomEntry);
      zout.write(new byte[]{1, 2, 3, 4});
      zout.closeEntry();
    }

    return new ByteArrayInputStream(baos.toByteArray());
  }

  @SneakyThrows
  private static InputStream createBrokenZippedDossierInputStream() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try (ZipOutputStream zout = new ZipOutputStream(baos)) {
      ZipEntry manifestEntry = new ZipEntry("manifest.xml");
      zout.putNextEntry(manifestEntry);
      zout.write(new byte[]{1, 2, 3, 4});
      zout.closeEntry();
    }

    return new ByteArrayInputStream(baos.toByteArray());
  }

}
