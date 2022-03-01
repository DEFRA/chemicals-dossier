package uk.gov.defra.reach.dossier.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestConfiguration.class })
@TestPropertySource(locations = "classpath:application-dev.properties")
public class JwtTokenValidatorTest {

  @Value("${test.jwt.token}")
  private String testJwtToken;

  @Value("${reach.dossier.jwt.secret}")
  private String testJwtKey;

  private JwtTokenValidator jwtTokenValidator;

  @BeforeEach
  public void setup() {
    jwtTokenValidator = new JwtTokenValidator(testJwtKey);
  }

  @Test
  public void validateToken_shouldReturnTrue_whenTokenIsValid() {
    assertThat(jwtTokenValidator.validateToken(testJwtToken)).isTrue();
  }

  @Test
  public void validateToken_shouldReturnFalse_whenTokenIsInvalid() {
    assertThat(jwtTokenValidator.validateToken("invalidToken")).isFalse();
  }
}
