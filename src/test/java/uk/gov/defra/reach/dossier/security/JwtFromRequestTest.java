package uk.gov.defra.reach.dossier.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

public class JwtFromRequestTest {

  @Test
  public void getJwtFromRequest_returnsToken_whenValid() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer token123");

    String result = JwtFromRequest.getJwtFromRequest(request);
    assertThat(result).isEqualTo("token123");
  }

  @Test
  public void getJwtFromRequest_returnsNull_whenBearerMissing() {
    MockHttpServletRequest request = new MockHttpServletRequest();

    request.addHeader(HttpHeaders.AUTHORIZATION, "token123");

    String result = JwtFromRequest.getJwtFromRequest(request);
    assertThat(result).isEqualTo(null);
  }

  @Test
  public void getJwtFromRequest_returnsNull_whenInvalid() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bear token123");

    String result = JwtFromRequest.getJwtFromRequest(request);
    assertThat(result).isEqualTo(null);
  }

  @Test
  public void getJwtFromRequest_returnsNull_whenNoAuthorizationHeader() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    String result = JwtFromRequest.getJwtFromRequest(request);
    assertThat(result).isEqualTo(null);
  }
}
