package uk.gov.defra.reach.dossier.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.graphql.spring.boot.test.GraphQLResponse;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class DossierQueryIT extends AbstractIT {

  @Test
  void queryDossier_returnsNullForUnknownId() {
    GraphQLResponse response = executeGraphQLQuery("query($id:ID!) { getDossier(id:$id) { dossierType } }", UUID.randomUUID().toString());
    assertThat(response.get("$.data.getDossier")).isNull();
  }

}
