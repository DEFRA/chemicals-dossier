package uk.gov.defra.reach.dossier.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobInputStream;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import uk.gov.defra.reach.dossier.domain.StorageContainer;

@ExtendWith(MockitoExtension.class)
class DossierLoaderTest {

  private static final String STORAGE_NAME = "storage reference";

  private DossierLoader dossierLoader;

  @Mock
  private CloudBlobContainer productionContainer;

  @Mock
  private CloudBlobContainer temporaryContainer;

  @BeforeEach
  public void setup() {
    dossierLoader = new DossierLoader(productionContainer, temporaryContainer);
  }

  @Test
  public void loadDossier_returnsInputStream_fromProductionStorage() throws URISyntaxException, StorageException {
    BlobInputStream blobInputStream = mockForInputStreamFromContainer(productionContainer);

    InputStream returnedInputStream = dossierLoader.loadDossier(StorageContainer.PRODUCTION, STORAGE_NAME);

    assertThat(returnedInputStream).isEqualTo(blobInputStream);
  }

  @Test
  public void loadDossier_returnsInputStream_fromTemporaryStorage() throws URISyntaxException, StorageException {
    BlobInputStream blobInputStream = mockForInputStreamFromContainer(temporaryContainer);

    InputStream returnedInputStream = dossierLoader.loadDossier(StorageContainer.TEMPORARY, STORAGE_NAME);

    assertThat(returnedInputStream).isEqualTo(blobInputStream);
  }

  private BlobInputStream mockForInputStreamFromContainer(CloudBlobContainer blobContainer) throws URISyntaxException, StorageException {
    CloudBlockBlob cloudBlockBlob = mock(CloudBlockBlob.class);
    BlobInputStream blobInputStream = mock(BlobInputStream.class);

    when(blobContainer.getBlockBlobReference(STORAGE_NAME)).thenReturn(cloudBlockBlob);
    when(cloudBlockBlob.openInputStream()).thenReturn(blobInputStream);
    return blobInputStream;
  }

  @Test
  void loadDossier_ThrowsIllegalStateException_WhenURISyntaxExceptionIsThrownByGetBlockBlobReference() throws URISyntaxException, StorageException {
    when(productionContainer.getBlockBlobReference(any(String.class))).thenThrow(new StorageException(null, null, null));
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> dossierLoader.loadDossier(StorageContainer.PRODUCTION, STORAGE_NAME));
  }

  @Test
  void loadDossier_ThrowsURISyntaxException_WhenURISyntaxExceptionIsThrownByGetBlockBlobReference() throws URISyntaxException, StorageException {
    when(productionContainer.getBlockBlobReference(any(String.class))).thenThrow(new URISyntaxException("", ""));
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> dossierLoader.loadDossier(StorageContainer.PRODUCTION, STORAGE_NAME));
  }
}
