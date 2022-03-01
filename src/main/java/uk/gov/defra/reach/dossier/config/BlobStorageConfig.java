package uk.gov.defra.reach.dossier.config;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Azure Blob storage containers
 */
@Configuration
public class BlobStorageConfig {

  @Value("${azure.storage.production.connectionString}")
  private String productionConnectionString;

  @Value("${azure.storage.production.containerName}")
  private String productionContainerName;

  @Value("${azure.storage.temporary.connectionString}")
  private String temporaryConnectionString;

  @Value("${azure.storage.temporary.containerName}")
  private String temporaryContainerName;

  @Bean
  public CloudBlobContainer productionContainer() throws URISyntaxException, InvalidKeyException, StorageException {
    return getCloudBlobContainer(productionConnectionString, productionContainerName);
  }

  @Bean
  public CloudBlobContainer temporaryContainer() throws URISyntaxException, InvalidKeyException, StorageException {
    return getCloudBlobContainer(temporaryConnectionString, temporaryContainerName);
  }

  private static CloudBlobContainer getCloudBlobContainer(String connectionString, String containerName)
      throws URISyntaxException, InvalidKeyException, StorageException {
    CloudStorageAccount storageAccount = CloudStorageAccount.parse(connectionString);
    CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
    CloudBlobContainer container = blobClient.getContainerReference(containerName);
    if (!container.exists()) {
      throw new IllegalStateException("Storage container [" + containerName + "] does not exist!");
    }
    return container;
  }

}
