package uk.gov.defra.reach.dossier.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql.spring.boot.test.GraphQLResponse;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import lombok.SneakyThrows;
import uk.gov.defra.reach.dossier.controller.DossierIndexRequest;
import uk.gov.defra.reach.dossier.domain.StorageContainer;
import uk.gov.defra.reach.dossier.service.DossierLoader;
import uk.gov.defra.reach.security.AuthenticatedUser;
import uk.gov.defra.reach.security.LegalEntity;
import uk.gov.defra.reach.security.Role;
import uk.gov.defra.reach.security.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-int.properties")
public abstract class AbstractIT {

  private static final AuthenticatedUser AUTHENTICATED_USER = new AuthenticatedUser(new User(), new LegalEntity(), Role.REACH_MANAGER);

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

  @BeforeEach
  private void mockSecurityPrincipal() {
    Authentication authentication = Mockito.mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(AUTHENTICATED_USER);
    when(authentication.getCredentials()).thenReturn(testJwtToken);
    SecurityContextHolder.getContext().setAuthentication(authentication);
  }

  protected String indexDossier(String fileName) {

    DossierIndexRequest dossierIndexRequest = new DossierIndexRequest();
    UUID dossierId = UUID.randomUUID();
    dossierIndexRequest.setDossierId(dossierId);
    dossierIndexRequest.setStorageLocation(UUID.randomUUID().toString());

    mockDossierLoaderForFile(fileName, dossierIndexRequest.getStorageLocation());

    HttpEntity<DossierIndexRequest> request = new HttpEntity<>(dossierIndexRequest, headers());
    ResponseEntity<Void> response = null;
try {
     response = restTemplate.postForEntity("/dossier/index", request, Void.class);
} catch (NullPointerException ex) {
  ex.printStackTrace();
}
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
