package uk.gov.defra.reach.dossier.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DossierConfig {
  @Value("${reach.dossier.days-to-expire}")
  private Integer daysToExpire;

  @Bean
  public Integer getDaysToExpire() {
    return daysToExpire;
  }
}
