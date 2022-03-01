package uk.gov.defra.reach.dossier.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

public class HealthcheckIT extends AbstractIT {

  @Value("${spring.application.version}")
  private String appVersion;

  @Test
  void healthcheck_shouldReturnHealthyStateWhenAppIsRunningCorrectly() {
    String response = restTemplate.getForObject("/healthcheck", String.class);
    JSONObject healthcheckDetails = new JSONObject(response);
    JSONObject dbDetails = (JSONObject) healthcheckDetails.get("details");

    String dbStatus = dbDetails.get("database").toString();

    assertThat(healthcheckDetails.get("health")).isEqualTo("HEALTHY");
    assertThat(dbStatus).isEqualTo("HEALTHY");
    assertThat(healthcheckDetails.get("version")).isEqualTo(appVersion);
  }
}
