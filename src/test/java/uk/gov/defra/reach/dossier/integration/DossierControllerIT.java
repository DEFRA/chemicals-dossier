package uk.gov.defra.reach.dossier.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.graphql.spring.boot.test.GraphQLResponse;
import org.junit.jupiter.api.Test;

public class DossierControllerIT extends AbstractIT {

  @Test
  void indexDossier_savesDossierIntoDatastore() {
    String dossierId = indexDossier("valid-dossier-6.3.0.0.i6z");
    assertThat(getDossierType(dossierId)).isEqualTo(dossierId);
  }

  private String getDossierType(String dossierId) {
    GraphQLResponse response = executeGraphQLQuery("query($id:ID!) { getDossier(id:$id) { id } }", dossierId);
    return response.get("$.data.getDossier.id");
  }
}
