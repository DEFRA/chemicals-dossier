package uk.gov.defra.reach.dossier.service;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.InputStream;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.defra.reach.dossier.domain.StorageContainer;

/**
 * Service to load dossiers from blob storage
 */
@Component
public class DossierLoader {

  private final CloudBlobContainer productionContainer;

  private final CloudBlobContainer temporaryContainer;

  @Autowired
  public DossierLoader(CloudBlobContainer productionContainer, CloudBlobContainer temporaryContainer) {
    this.productionContainer = productionContainer;
    this.temporaryContainer = temporaryContainer;
  }

  /**
   * Loads a dossier file from blob storage
   *
   * @param storageContainer the container from which the dosiser file will be obtained
   * @param storageReference the storage reference to identify the dossier file
   * @return an inputstream for the dossier file
   */
  public InputStream loadDossier(StorageContainer storageContainer, String storageReference) {
    CloudBlobContainer container = getContainer(storageContainer);
    try {
      CloudBlockBlob blob = container.getBlockBlobReference(storageReference);
      return blob.openInputStream();
    } catch (URISyntaxException | StorageException e) {
      throw new IllegalStateException("Unable to read dossier file from storage", e);
    }
  }

  private CloudBlobContainer getContainer(StorageContainer storageContainer) {
    switch (storageContainer) {
      case PRODUCTION:
        return productionContainer;
      case TEMPORARY:
        return temporaryContainer;
      default:
        throw new IllegalStateException("Unknown container type: " + storageContainer);
    }
  }

}
