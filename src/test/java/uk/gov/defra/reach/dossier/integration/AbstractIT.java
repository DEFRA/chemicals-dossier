package uk.gov.defra.reach.dossier.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.jayway.jsonpath.Option;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;
import uk.gov.defra.reach.dossier.controller.DossierIndexRequest;
import uk.gov.defra.reach.dossier.domain.StorageContainer;
import uk.gov.defra.reach.dossier.repository.DossierQueryRepository;
import uk.gov.defra.reach.dossier.service.DossierIndexingService;
import uk.gov.defra.reach.dossier.service.DossierLoader;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-int.properties")
public class AbstractIT {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  protected TestRestTemplate restTemplate;

  @Value("${test.jwt.token}")
  private String testJwtToken;

  @MockBean(name = "productionContainer")
  private CloudBlobContainer productionContainer;

  @MockBean(name = "temporaryContainer")
  private CloudBlobContainer temporaryContainer;

  @MockBean
  private DossierLoader dossierLoader;

  protected String indexDossier(String fileName) {
    DossierIndexRequest dossierIndexRequest = new DossierIndexRequest();
    UUID dossierId = UUID.randomUUID();
    dossierIndexRequest.setDossierId(dossierId);
    dossierIndexRequest.setStorageLocation(UUID.randomUUID().toString());

    mockDossierLoaderForFile(fileName, dossierIndexRequest.getStorageLocation());

    HttpEntity<DossierIndexRequest> request = new HttpEntity<>(dossierIndexRequest, headers());

    ResponseEntity<Void> response = restTemplate.postForEntity("/dossier/index", request, Void.class);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    String graphQlMutation = String.format("{"
                                           + "\"query\": \"%s\","
                                           + "\"variables\": {"
                                           + "  \"storageLocation\": \"%s\","
                                           + "  \"dossierId\": \"%s\""
                                           + "}"
                                           + "}",
        "mutation($storageLocation:String!, $dossierId: ID!) { persistDossier(storageLocation: $storageLocation, dossierId: $dossierId) }",
        dossierIndexRequest.getStorageLocation(),
        dossierIndexRequest.getDossierId());
    HttpEntity<String> persistRequest = new HttpEntity<>(graphQlMutation, headers());
    ResponseEntity<Void> persistResponse = restTemplate.postForEntity("/graphql", persistRequest, Void.class);
    assertThat(persistResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    return dossierIndexRequest.getDossierId().toString();
  }

  protected GraphQLResponse executeGraphQLQuery(String query, String dossierId) {
    String requestString = String.format("{\n"
                                         + "    \"query\": \"%s\",\n"
                                         + "    \"variables\": {\n"
                                         + "        \"id\": \"%s\"\n"
                                         + "    }\n"
                                         + "}", query, dossierId);

    HttpEntity<String> request = new HttpEntity<>(requestString, headers());
    ResponseEntity<String> response = restTemplate.exchange("/graphql", HttpMethod.POST, request, String.class);
    GraphQLResponse graphQLResponse = new GraphQLResponse(response, objectMapper);
    if (graphQLResponse.getRawResponse().getBody().contains("\"errors\"")) {
      List<String> errors = graphQLResponse.getList("$.errors..message", String.class);
      if (!errors.isEmpty()) {
        throw new RuntimeException(errors.get(0));
      }
    }
    return graphQLResponse;
  }

  protected HttpHeaders headers() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(testJwtToken);
    return headers;
  }

  @SneakyThrows
  private void mockDossierLoaderForFile(String dossierName, String storageLocation) {
    File dossierFile = ResourceUtils.getFile("classpath:dossiers/" + dossierName);
    FileInputStream fileInputStream = new FileInputStream(dossierFile);
    when(dossierLoader.loadDossier(StorageContainer.TEMPORARY, storageLocation)).thenReturn(fileInputStream);
  }

}
