package uk.gov.defra.reach.dossier.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.graphql.spring.boot.test.GraphQLResponse;
import org.junit.jupiter.api.Test;

public class DeleteDossierIT extends AbstractIT {

  @Test
  void deleteDossier_removesDossierFromDataStore() {
    String dossierId = indexDossier("valid-dossier-6.3.0.0.i6z");
    executeGraphQLQuery("mutation($id:String!) { deleteDossier(id:$id) }", dossierId);
    GraphQLResponse response = executeGraphQLQuery("query($id:ID!) { getDossier(id:$id) { id } }", dossierId);
    assertThat(response.get("$.data.getDossier", Object.class)).isNull();
  }

}
