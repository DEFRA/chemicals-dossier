package uk.gov.defra.reach.dossier.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.graphql.spring.boot.test.GraphQLResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.gov.defra.reach.dossier.dto.ReasonForUpdate;

public class ReasonsForUpdateIT extends AbstractIT {

  @Test
  void queryDossier_returnsReasonsForRegistrationSpontaneousUpdate() {
    String dossierId = indexDossier("update-reasons.i6z");
    GraphQLResponse response = executeGraphQLQuery("query($id:ID!) { \n"
        + "    getDossier(id:$id) { \n"
        + "        reasonsForSpontaneousUpdate {\n"
        + "          justificationCode\n"
        + "          justificationText\n"
        + "          remarks\n"
        + "        } \n"
        + "    }\n"
        + "}", dossierId);

    List<ReasonForUpdate> reasonsForSpontaneousUpdate = response.getList("$.data.getDossier.reasonsForSpontaneousUpdate", ReasonForUpdate.class);
    assertThat(reasonsForSpontaneousUpdate).hasSize(19);
    assertThat(reasonsForSpontaneousUpdate.get(0))
        .hasFieldOrPropertyWithValue("justificationCode", "61917")
        .hasFieldOrPropertyWithValue("remarks", "change from individual to joint submission");
    assertThat(reasonsForSpontaneousUpdate.get(1))
        .hasFieldOrPropertyWithValue("justificationCode", "61870")
        .hasFieldOrPropertyWithValue("remarks", "change of registration type (full /intermidiate)");
    assertThat(reasonsForSpontaneousUpdate.get(18))
        .hasFieldOrPropertyWithValue("justificationCode", "1342")
        .hasFieldOrPropertyWithValue("justificationText", "reason")
        .hasFieldOrPropertyWithValue("remarks", "other: reason");
  }

  @Test
  void queryDossier_returnsPpordreasonsForSpontaneousUpdate() {
    String dossierId = indexDossier("ppord-update-reasons.i6z");
    GraphQLResponse response = executeGraphQLQuery("query($id:ID!) { \n"
        + "    getDossier(id:$id) { \n"
        + "        reasonsForSpontaneousUpdate {\n"
        + "          justificationCode\n"
        + "          justificationText\n"
        + "          remarks\n"
        + "        } \n"
        + "    }\n"
        + "}", dossierId);

    List<ReasonForUpdate> reasonsForSpontaneousUpdate = response.getList("$.data.getDossier.reasonsForSpontaneousUpdate", ReasonForUpdate.class);
    assertThat(reasonsForSpontaneousUpdate).hasSize(3);
    assertThat(reasonsForSpontaneousUpdate.get(0))
        .hasFieldOrPropertyWithValue("justificationCode", "61917")
        .hasFieldOrPropertyWithValue("remarks", "Remark 1");
    assertThat(reasonsForSpontaneousUpdate.get(1))
        .hasFieldOrPropertyWithValue("justificationCode", "61870")
        .hasFieldOrPropertyWithValue("remarks", "Remark 2");
    assertThat(reasonsForSpontaneousUpdate.get(2))
        .hasFieldOrPropertyWithValue("justificationCode", "1342")
        .hasFieldOrPropertyWithValue("justificationText", "Another reason entirely")
        .hasFieldOrPropertyWithValue("remarks", "Remark 3");
  }

  @Test
  void queryDossier_returnsEmptyreasonsForSpontaneousUpdate() {
    String dossierId = indexDossier("secret_heptanoate-REG.i6z");
    GraphQLResponse response = executeGraphQLQuery("query($id:ID!) { \n"
        + "    getDossier(id:$id) { \n"
        + "        reasonsForSpontaneousUpdate {\n"
        + "          justificationCode\n"
        + "          remarks\n"
        + "        } \n"
        + "    }\n"
        + "}", dossierId);

    List<ReasonForUpdate> reasonsForSpontaneousUpdate = response.getList("$.data.getDossier.reasonsForSpontaneousUpdate", ReasonForUpdate.class);
    assertThat(reasonsForSpontaneousUpdate).isEmpty();
  }

  @Test
  void queryDossier_returnsReasonsForRegistrationRequestedUpdate() {
    String dossierId = indexDossier("requested-update-reasons.i6z");
    GraphQLResponse response = executeGraphQLQuery("query($id:ID!) { \n"
        + "    getDossier(id:$id) { \n"
        + "        reasonsForRequestedUpdate {\n"
        + "          number\n"
        + "          remarks\n"
        + "        } \n"
        + "    }\n"
        + "}", dossierId);

    List<ReasonForUpdate> reasonsForRequestedUpdate = response.getList("$.data.getDossier.reasonsForRequestedUpdate", ReasonForUpdate.class);
    System.out.println(reasonsForRequestedUpdate);
    assertThat(reasonsForRequestedUpdate).hasSize(1);
    assertThat(reasonsForRequestedUpdate.get(0))
        .hasFieldOrPropertyWithValue("number", "5353425345")
        .hasFieldOrPropertyWithValue("remarks", "some random text");
  }

}

